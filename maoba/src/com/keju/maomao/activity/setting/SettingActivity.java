package com.keju.maomao.activity.setting;

import java.io.File;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.activity.LoginActivity;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.util.FileUtil;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

/**
 * 设置界面
 * 
 * @author lhm
 * @date 创建时间：2013-10-30
 */
public class SettingActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle;
	private LinearLayout rlNotice, rlAbout, rlVersionTest, rlFeedback;
	private LinearLayout rlClearCache;// 清除缓存
	private Button btnLogout;

	private File photoDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_page);
		findView();
		fillData();

	}

	private void fillData() {
		tvTitle.setText("设置");

	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		rlNotice = (LinearLayout) findViewById(R.id.rlNotice);
		rlNotice.setOnClickListener(this);
		rlClearCache = (LinearLayout) this.findViewById(R.id.rlClearCache);
		rlClearCache.setOnClickListener(this);
		rlAbout = (LinearLayout) findViewById(R.id.rlAbout);
		rlAbout.setOnClickListener(this);
		rlVersionTest = (LinearLayout) findViewById(R.id.rlVersionTest);
		rlVersionTest.setOnClickListener(this);
		rlFeedback = (LinearLayout) findViewById(R.id.rlFeedback);
		rlFeedback.setOnClickListener(this);
		btnLogout = (Button) this.findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			break;
		case R.id.rlClearCache:
			new ClearCacheTask().execute();
			break;
		case R.id.rlNotice:
			openActivity(SettingNoticeActivity.class);
			break;
		case R.id.rlVersionTest:
			if (NetUtil.checkNet(this)) {
				UmengUpdateAgent.update(this);
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

					@Override
					public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
						switch (updateStatus) {
						case 0:
							UmengUpdateAgent.showUpdateDialog(SettingActivity.this, updateInfo);
							break;
						case 1: // has no update
							showShortToast("已经是最新版本");
							break;
						case 2: // none wifi
							showShortToast("没有wifi连接， 只在wifi下更新");
							break;
						case 3: // time out
							showShortToast("连接服务器超时");
							break;

						}
					}
				});
			} else {
				showShortToast(R.string.NoSignalException);
			}
			break;
		case R.id.rlAbout:
			openActivity(SettingAboutActivity.class);
			break;
		case R.id.rlFeedback:
			openActivity(SettingFeedbackActivity.class);
			break;
		case R.id.btnLogout:
			showAlertDialog("提示", "确定要注销登录吗？", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// MainActivity.rb_home.setChecked(true);
					// MainActivity.rb_my.setChecked(false);
					SharedPrefUtil.clearUserinfo(SettingActivity.this);
					SharedPrefUtil.setInfoComplete(SettingActivity.this, false);
					openActivity(LoginActivity.class);
				}
			}, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}, new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {

				}
			});
			break;
		default:
			break;
		}
	}

	/***
	 * 
	 * 清除缓存
	 */
	private class ClearCacheTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd("正在清除....");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			photoDir = new File(Environment.getExternalStorageDirectory() + "/" + Constants.APP_DIR_NAME);
				return FileUtil.deleteFiles(photoDir);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dismissPd();
			if (result == true) {
				showShortToast("清除成功");
			} else {
				showShortToast("你已清除了");
			}
		}
	}

}
