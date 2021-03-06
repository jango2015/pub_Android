/**
 * 
 */
package com.keju.maomao.activity.friendpersonalcenter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.keju.maomao.AsyncImageLoader;
import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.AsyncImageLoader.ImageCallback;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.bean.FriendPersonalCentreBean;
import com.keju.maomao.bean.PersonalCentreBean;
import com.keju.maomao.bean.ResponseBean;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;

/**
 * 好友收到的礼物
 * 
 * @author zhouyong
 * @data 创建时间：2013-12-16 下午2:47:34
 */
public class GetGiftActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;

	private ArrayList<FriendPersonalCentreBean> giftBean = new ArrayList<FriendPersonalCentreBean>();
	private GridView gvGife;
	private GiftAdapter adapter;

	private ProgressDialog pd;

	private String giftType = "friend";
	private int page = 1;
    private int friendId;
	Display display;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_get_gift_list);
		
		friendId = (int) getIntent().getExtras().getInt(Constants.EXTRA_DATA);
		display = this.getWindowManager().getDefaultDisplay();
		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		gvGife = (GridView) findViewById(R.id.gvGife);

	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);

		btnRight.setOnClickListener(this);

		adapter = new GiftAdapter();
		gvGife.setAdapter(adapter);

		tvTitle.setText("收到的礼物");
		if (NetUtil.checkNet(GetGiftActivity.this)) {
			new GetGiftTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}

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

	public class GetGiftTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			 showPd(R.string.loading);
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				return new BusinessHelper().getGiftList(friendId, page, giftType);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			if (result != null) {
				if (result.has("status")) {
					try {
						int status = result.getInt("status");
						if (status == Constants.REQUEST_SUCCESS) {

							if (result.has("gift")) {
								JSONArray arr = result.getJSONArray("gift");
								if (arr != null) {
									ArrayList<FriendPersonalCentreBean> inviteBean = (ArrayList<FriendPersonalCentreBean>) FriendPersonalCentreBean
											.constractList(arr);
									if (inviteBean.size() > 0) {
										giftBean.addAll(inviteBean);
										adapter.notifyDataSetChanged(); // 通知更新
									} else {
										showShortToast("还没有人给他送礼物哦");
									}
									adapter.notifyDataSetChanged();
								}
							}
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
	 * 礼物图片显示适配
	 * 
	 * @author zhouyong
	 * 
	 */
	private class GiftAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return giftBean.size();
		}

		@Override
		public Object getItem(int position) {
			return giftBean.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final FriendPersonalCentreBean bean = giftBean.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.friend_get_gift_item, null);
				holder.ivGiftPhoto = (ImageView) convertView.findViewById(R.id.ivGiftPhoto);
				holder.tvGiftName = (TextView) convertView.findViewById(R.id.tvGiftName);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ViewHolder holderUse = holder;

			int itemWidth = (display.getWidth() - 4 * 10) / 4;
			android.view.ViewGroup.LayoutParams param = holder.ivGiftPhoto.getLayoutParams();
			param.width = itemWidth;
			param.height = itemWidth;

			holder.tvGiftName.setText(bean.getGiftName());
			holder.ivGiftPhoto.setLayoutParams(param);
			String giftUrl = bean.getGiftphotoUrl();
			holder.ivGiftPhoto.setTag(giftUrl);
			Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(giftUrl, new ImageCallback() {

				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView image = (ImageView) gvGife.findViewWithTag(imageUrl);
					if (image != null) {
						if (imageDrawable != null) {
							image.setImageDrawable(imageDrawable);
							GiftAdapter.this.notifyDataSetChanged();
						} else {
							image.setImageResource(R.drawable.ic_default);
						}
					}
				}
			});
			if (cacheDrawble != null) {
				holder.ivGiftPhoto.setImageDrawable(cacheDrawble);
			} else {
				holder.ivGiftPhoto.setImageResource(R.drawable.ic_default);
			}
			return convertView;
		}

	}

	class ViewHolder {
		private ImageView ivGiftPhoto;
		private TextView tvGiftName;
	}

}
