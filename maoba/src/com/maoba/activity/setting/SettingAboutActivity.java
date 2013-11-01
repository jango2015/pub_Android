package com.maoba.activity.setting;

import java.nio.Buffer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.maoba.R;
import com.maoba.activity.base.BaseActivity;

/**
 * 关于冒冒
 * 
 * @author lhm
 * @date 创建时间：2013-10-30
 */
public class SettingAboutActivity extends BaseActivity implements
		OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle,tvUrl;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_about);

		findView();
		fillData();
	}

	@SuppressLint("ResourceAsColor")
	private void fillData() {
		// TODO Auto-generated method stub
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		tvTitle.setText("关于冒冒");
		//tvUrl.setTextColor(R.color.url_up);
	}

	private void findView() {
		// TODO Auto-generated method stub
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		ibLeft.setOnClickListener(this);
		tvTitle=(TextView) findViewById(R.id.tvTitle);
		tvTitle.setOnClickListener(this);
		tvUrl=(TextView) findViewById(R.id.tvUrl);
		tvUrl.setOnClickListener(this);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.tvUrl:
			//tvUrl.setTextColor(R.color.url_down);
			Uri uri =Uri.parse("http://www.maobake.com/"); 

			 Intent it = new Intent(Intent.ACTION_VIEW,uri); 

			 startActivity(it); 
			break;
		default:
			break;
		}
	}

}