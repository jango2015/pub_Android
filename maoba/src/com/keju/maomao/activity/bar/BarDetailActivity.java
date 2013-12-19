/**
 * 
 */
package com.keju.maomao.activity.bar;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keju.maomao.AsyncImageLoader;
import com.keju.maomao.AsyncImageLoader.ImageCallback;
import com.keju.maomao.CommonApplication;
import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.activity.event.EventDetailActivity;
import com.keju.maomao.activity.friendpersonalcenter.FriendPersonalCenter;
import com.keju.maomao.activity.mapview.LocationMapActivity;
import com.keju.maomao.activity.personalcenter.PersonalCenter;
import com.keju.maomao.bean.BarBean;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 酒吧详情
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-22 下午4:05:12
 */
public class BarDetailActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;

	private TextView tvName, tvDistanceLabel, tvAddress, tvTelNumber, tvBarType, tvIntro;
	private TextView tvShowNum;// 签到人数
	private ImageView ivImage;
	private ImageView ivNext;// 签到

	private LinearLayout viewShowList;
	private LinearLayout viewAddress;// 酒啊地址

	private ProgressDialog pd;
	private CommonApplication app;

	private boolean isCollectingTask = false;// 是否收藏

	private BarBean bean;
	private List<BarBean> barDetailList = new ArrayList<BarBean>();// 酒吧详情
	private List<BarBean> showList = new ArrayList<BarBean>();// 签到

	// 活动
	private LinearLayout viewEvent;
	private ImageView ivEvent;
	private TextView tvEventTitle, tvEventEndTime;
	private LinearLayout viewEventShow;// 如果酒吧没有活动就不显示
	
	private int eventId;
	private Boolean iscollect;//活动是否被收藏

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bar_details);
		MobclickAgent.onError(this);
		bean = (BarBean) getIntent().getExtras().getSerializable(Constants.EXTRA_DATA);
		MobclickAgent.onEvent(this, "bar_details");

		app = (CommonApplication) getApplication();
		app.addActivity(this);

		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		tvName = (TextView) this.findViewById(R.id.tvName);
		tvDistanceLabel = (TextView) this.findViewById(R.id.tvDistanceLabel);
		tvAddress = (TextView) this.findViewById(R.id.tvAddress);
		tvTelNumber = (TextView) this.findViewById(R.id.tvTelNumber);
		tvBarType = (TextView) this.findViewById(R.id.tvBarType);
		tvIntro = (TextView) this.findViewById(R.id.tvIntro);
//		tvHot = (TextView) this.findViewById(R.id.tvHot);
		tvShowNum = (TextView) this.findViewById(R.id.tvShowCount);
		ivImage = (ImageView) this.findViewById(R.id.ivImage);

		viewShowList = (LinearLayout) this.findViewById(R.id.viewShowList);// 签到
		viewAddress = (LinearLayout) this.findViewById(R.id.viewAddress);

		ivNext = (ImageView) this.findViewById(R.id.ivNext);

		// 活动
		viewEvent = (LinearLayout) this.findViewById(R.id.viewEvent);
		ivEvent = (ImageView) this.findViewById(R.id.ivEvent);
		tvEventTitle = (TextView) this.findViewById(R.id.tvEventTitle);
		tvEventEndTime = (TextView) this.findViewById(R.id.tvEventEndTime);
		viewEventShow = (LinearLayout) this.findViewById(R.id.viewEventShow);

	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setText("收藏");
		btnRight.setOnClickListener(this);
		ivImage.setOnClickListener(this);
		ivNext.setOnClickListener(this);
		tvTelNumber.setOnClickListener(this);

		viewAddress.setOnClickListener(this);
        viewEvent.setOnClickListener(this);
		tvTitle.setText("酒吧详情");

		tvName.setText(bean.getBar_Name());// 酒吧名字
		tvBarType.setText(bean.getBarType());// 酒吧类型
		tvIntro.setText(  bean.getBar_Intro());// 酒吧内容
		tvTelNumber.setText(bean.getTelephone());// 酒吧号码
//		tvHot.setText(bean.getHot());// 酒吧人气

		if (NetUtil.checkNet(BarDetailActivity.this)) {
			new GetBarDetailTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}
		// 距离
		double latitude;
		try {
			latitude = Double.parseDouble(bean.getLatitude());
		} catch (NumberFormatException e) {
			latitude = 0;
		}
		double longitude;
		try {
			longitude = Double.parseDouble(bean.getLongitude());
		} catch (NumberFormatException e) {
			longitude = 0;
		}
		try {
			if (app.getLastLocation() != null) {
				double distance = StringUtil.getDistance(app.getLastLocation().getLatitude(), app.getLastLocation()
						.getLongitude(), latitude, longitude);
				if (distance > 1000) {
					distance = distance / 1000;
					tvDistanceLabel.setText(String.format("%.1f", distance) + "km");
				} else {
					tvDistanceLabel.setText(String.format("%.0f", distance) + "m");
				}
			} else {
				tvDistanceLabel.setText("");
			}
		} catch (Exception e) {
		}

		String imageUrl = bean.getShowPhotoUrl();
		ivImage.setTag(imageUrl);
		Drawable cacheDrawable = AsyncImageLoader.getInstance().loadDrawable(imageUrl, new ImageCallback() {

			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				ImageView image = (ImageView) ivImage.findViewWithTag(imageUrl);
				if (image != null) {
					if (imageDrawable != null) {
						ivImage.setImageDrawable(imageDrawable);
					} else {
						ivImage.setImageResource(R.drawable.ic_default);
					}
				}
			}
		});
		if (cacheDrawable != null) {
			ivImage.setImageDrawable(cacheDrawable);
		} else {
			ivImage.setImageResource(R.drawable.ic_default);
		}

		ivImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Bundle b = new Bundle();
				b.putSerializable(Constants.EXTRA_DATA, bean);
				openActivity(ShowBarEnvironmentActivity.class, b);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			break;
		case R.id.btnRight:
			if (isCollectingTask == false) {
				showShortToast("正在执行收藏操作,请稍等...");
				// return;
			}
			if (NetUtil.checkNet(this)) {
				isCollectingTask = true;
				new CollectTask().execute();
				refreshData();
			} else {
				showShortToast(R.string.NoSignalException);
			}
			break;
		case R.id.ivNext:
			break;
		case R.id.viewAddress:
			try {
				String address1 = null;
				String address = bean.getBar_Address();
				StringTokenizer token = new StringTokenizer(address, "$");
				String[] add = new String[3];
				int i1 = 0;
				while (token.hasMoreTokens()) {
					add[i1] = token.nextToken();
					i1++;
					address1 = add[0];
				}
				Intent intent = Intent.getIntent("intent://map/marker?location=" + bean.getLatitude() + ","
						+ bean.getLongitude() + "&title=" + bean.getBar_Name() + "&content=" + address1
						+ bean.getBarStreet() + "&src=冒冒#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
				startActivity(intent);
			} catch (Exception e) {
				if (TextUtils.isEmpty(bean.getLatitude()) || TextUtils.isEmpty(bean.getLongitude())) {
					showShortToast("酒吧经纬度数据为空");
					return;
				}
				// 跳转到自定义的sdk地图里
				Intent intent = new Intent(this, LocationMapActivity.class);
				intent.putExtra(Constants.EXTRA_DATA, bean);
				startActivity(intent);
			}
			break;
		case R.id.tvTelNumber:
			String phoneNum = bean.getTelephone();
			if (TextUtils.isEmpty(phoneNum)) {
				return;
			}
			Intent callIntent = new Intent();
			callIntent.setAction(Intent.ACTION_DIAL);
			callIntent.setData(Uri.parse("tel:" + phoneNum));
			startActivity(callIntent);
			break;
		case R.id.viewEvent:
           Bundle b = new Bundle();
           b.putInt(Constants.EXTRA_DATA,eventId);
           b.putString("BARNAME", bean.getBar_Name());
           b.putBoolean("ISCOLLECT", iscollect);
           openActivity(EventDetailActivity.class, b);
			break;
		default:
			break;
		}
	}

	/**
	 * 刷新数据
	 */
	private void refreshData() {
		if (NetUtil.checkNet(BarDetailActivity.this)) {
			// pageIndex = 1;
			new GetBarDetailTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}
	}

	/**
	 * 
	 * 获取酒吧详情
	 * 
	 * */

	public class GetBarDetailTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pd == null) {
				pd = new ProgressDialog(BarDetailActivity.this);
			}
			pd.setMessage(getString(R.string.loading));
			pd.show();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			Integer uid = SharedPrefUtil.getUid(BarDetailActivity.this);
			try {
				return new BusinessHelper().getBarDetail(bean.getBar_id(), uid);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (pd != null) {
				pd.dismiss();
			}
			if (result != null) {
				if (result.has("status")) {
					try {
						int status = result.getInt("status");
						if (status == Constants.REQUEST_SUCCESS) {
							// btnRight.setText(result.getString("is_collect"));
							String address = result.getString("county");
							String showCount = result.getString("show_count");
							tvShowNum.setText( showCount +"人");
							StringTokenizer token = new StringTokenizer(address, "$");
							String[] add = new String[3];
							int i = 0;
							while (token.hasMoreTokens()) {
								add[i] = token.nextToken();
								i++;
								String address1 = add[0];
								tvAddress.setText(address1 + bean.getBarStreet());// 酒吧地址
							}

							if (result.has("picture_list")) {
								JSONArray showArrList = result.getJSONArray("picture_list");
								if (showArrList != null) {
									ArrayList<BarBean> showBeans = (ArrayList<BarBean>) BarBean
											.constractList(showArrList);
									if (showList.size() < showBeans.size()) {
										showList.addAll(showBeans);
										fillShowList(showBeans);
									}
								}
								JSONArray barDetailList1 = result.getJSONArray("pub_list");
								ArrayList<BarBean> barDetailBeans = (ArrayList<BarBean>) BarBean
										.constractList(barDetailList1);
								barDetailList.addAll(barDetailBeans);
							}
							JSONObject objEvent = null;
							if (!result.getString("activity").equals("null")) {
								objEvent = result.getJSONObject("activity");
								// 加载活动图片
								if(objEvent.has("pic_path")){
									String imageUrl = BusinessHelper.PIC_BASE_URL + objEvent.getString("pic_path");
									ivEvent.setTag(imageUrl);
									Drawable cacheDrawable = AsyncImageLoader.getInstance().loadDrawable(imageUrl,
											new ImageCallback() {

												@Override
												public void imageLoaded(Drawable imageDrawable, String imageUrl) {
													ImageView image = (ImageView) ivEvent.findViewWithTag(imageUrl);
													if (image != null) {
														if (imageDrawable != null) {
															ivEvent.setImageDrawable(imageDrawable);
														} else {
															ivEvent.setImageResource(R.drawable.ic_default);
														}
													}
												}
											});
									if (cacheDrawable != null) {
										ivEvent.setImageDrawable(cacheDrawable);
									} else {
										ivEvent.setImageResource(R.drawable.ic_default);
									}
								}
								tvEventTitle.setText(objEvent.getString("activity_info"));
								tvEventEndTime.setText(objEvent.getString("end_date"));
								eventId = objEvent.getInt("id");
								viewEventShow.setVisibility(View.VISIBLE);
								
								iscollect = objEvent.getBoolean("is_collect");
								
							} else {
								viewEventShow.setVisibility(View.GONE);
								return;
							}

						} else {
							showShortToast(R.string.json_exception);
						}
					} catch (JSONException e) {
						showShortToast(R.string.json_exception);
					}
				}

			} else {
				showShortToast("服务器连接失败");
			}

		}
	}

	/**
	 * 收藏
	 * 
	 * @author Zhouyong
	 * 
	 */
	private class CollectTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(BarDetailActivity.this);
			try {
				return new BusinessHelper().collectBar(uid, bean.getBar_id());
			} catch (SystemException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (result != null) {
				try {
					int status = result.getInt("status");
					if (status == Constants.REQUEST_SUCCESS) {
						showShortToast("收藏成功");
					} else {
						showShortToast("你已经收藏过了");
					}
				} catch (JSONException e) {
					showShortToast(R.string.json_exception);
				}
			} else {
				showShortToast(R.string.connect_server_exception);
			}
			isCollectingTask = false;
		}

	}

	/**
	 * 填充签到数据
	 * 
	 * @param list
	 * 
	 */
	private void fillShowList(final List<BarBean> showlist) {
		if (showlist == null) {
			return;
		}
		for (int i = 0; i < showlist.size(); i++) {
			final BarBean showBean = showlist.get(i);
			LinearLayout.LayoutParams paramItem = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			paramItem.rightMargin = 6;
			final View view = getLayoutInflater().inflate(R.layout.show_item, null);
			view.setLayoutParams(paramItem);
			ImageView ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);

			String picUrl = showBean.getShowPhotoUrl();
			ivPhoto.setTag(picUrl);
			Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(picUrl, new ImageCallback() {
				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView image = (ImageView) viewShowList.findViewWithTag(imageUrl);
					if (image != null) {
						if (imageDrawable != null) {
							image.setImageDrawable(imageDrawable);
						} else {
							image.setImageResource(R.drawable.ic_default);
						}
					}
				}
			});
			if (cacheDrawble != null) {
				ivPhoto.setImageDrawable(cacheDrawble);
			} else {
				ivPhoto.setImageResource(R.drawable.ic_default);
			}
			ivPhoto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int uid = SharedPrefUtil.getUid(BarDetailActivity.this);
					if (uid == showBean.getUserId()) {
						openActivity(PersonalCenter.class);
					} else {
						Bundle b = new Bundle();
						b.putSerializable(Constants.EXTRA_DATA, showBean.getUserId());
						openActivity(FriendPersonalCenter.class, b);
					}
				}
			});
			viewShowList.addView(view);
		}

	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// if (NetUtil.checkNet(BarDetailActivity.this)) {
	// new GetBarDetailTask().execute();
	// } else {
	// showShortToast(R.string.NoSignalException);
	// }
	// }

}
