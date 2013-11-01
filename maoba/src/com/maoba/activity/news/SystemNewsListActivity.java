/**
 * 
 */
package com.maoba.activity.news;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.bean.NewsBean;
import com.maoba.bean.ResponseBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;
import com.maoba.util.SharedPrefUtil;

/**
 * 系统信息列表
 * 
 * @author zhuoyong
 * @data 创建时间：2013-10-30 下午2:20:01
 */
public class SystemNewsListActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLift;
	private TextView tvTitle;

	private ListView lvSysNewsList;
	private Adapter adapter;
	private ArrayList<NewsBean> sysNesBean = new ArrayList<NewsBean>();

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_news_list);
		findView();
		fillData();
	}

	private void findView() {
		ibLift = (ImageButton) this.findViewById(R.id.ibLeft);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		lvSysNewsList = (ListView) this.findViewById(R.id.lvSysNewsList);
	}

	private void fillData() {
		ibLift.setImageResource(R.drawable.ic_btn_left);
		ibLift.setOnClickListener(this);
		tvTitle.setText("系统消息");

		adapter = new Adapter();
		lvSysNewsList.setAdapter(adapter);

		if (NetUtil.checkNet(SystemNewsListActivity.this)) {
			new SysNewsListTask().execute();
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

	/**
	 * 获取系统消息数据
	 * 
	 * */
	public class SysNewsListTask extends AsyncTask<Void, Void, ResponseBean<NewsBean>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pd == null) {
				pd = new ProgressDialog(SystemNewsListActivity.this);
			}
			pd.setMessage(getString(R.string.loading));
			pd.show();
		}

		@Override
		protected ResponseBean<NewsBean> doInBackground(Void... params) {
			int userId = SharedPrefUtil.getUid(SystemNewsListActivity.this);
			try {
				return new BusinessHelper().getSysLetter(userId);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBean<NewsBean> result) {
			super.onPostExecute(result);
			if (pd != null) {
				pd.dismiss();
			}
			if (result.getStatus() != Constants.REQUEST_FAILD) {
				List<NewsBean> tempList = result.getObjList();
				if (tempList.size() > 0) {
					sysNesBean.addAll(tempList);
					adapter.notifyDataSetChanged();
				} else {
					showShortToast("没有系统信息");
				}
			}
		}

	}

	/**
	 * 获取系统消息适配器
	 * 
	 * */
	public class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			return sysNesBean.size();
		}

		@Override
		public Object getItem(int position) {
			return sysNesBean.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			NewsBean bean = sysNesBean.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.system_news_item, null);
				holder.tvNewsContent = (TextView) convertView.findViewById(R.id.tvSystemNews);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvNewsContent.setText(bean.getContent());
			return convertView;
		}

	}

	class ViewHolder {
		private TextView tvNewsContent;
	}

}