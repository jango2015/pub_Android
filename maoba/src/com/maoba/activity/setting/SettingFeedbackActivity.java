package com.maoba.activity.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.maoba.R;
import com.maoba.activity.base.BaseActivity;

/**
 * 意见反馈
 * 
 * @author lhm
 * @date 创建时间：2013-10-31
 */
public class SettingFeedbackActivity extends BaseActivity implements OnClickListener, TextWatcher {
	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;
	private EditText etFeedback;
	private TextView tvNum;
	TextView hasnum;// 用来显示剩余字数
	int num = 140;// 限制的最大字数　

	private CharSequence temp;
	private int selectionStart;
	private int selectionEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_feedback);
		findView();
		fillData();
	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		tvTitle.setText("意见反馈");
		tvNum.setText(0 + "");
		btnRight.setText("发送");
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);

	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);

		ibLeft.setOnClickListener(this);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		etFeedback = (EditText) findViewById(R.id.etFeedback);
		etFeedback.addTextChangedListener(this);
		tvNum = (TextView) findViewById(R.id.tvNum);
		btnRight = (Button) findViewById(R.id.btnRight);
		btnRight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		int number = s.length();
		tvNum.setText("" + number);
		selectionStart = tvNum.getSelectionStart();
		selectionEnd = etFeedback.getSelectionEnd();
		if (temp.length() > num) {
			s.delete(selectionStart - 1, selectionEnd);
			int tempSelection = selectionEnd;
			etFeedback.setText(s);
			etFeedback.setSelection(tempSelection);// 设置光标在最后
			/*
			 * if (tvNum.length()>141) { showShortToast("最多可输入140个字..");
			 * 
			 * }
			 */
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		temp = s;
	}

}