/**
 * 
 */
package com.keju.maomao.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.keju.maomao.helper.BusinessHelper;

/**
 * 酒吧活动 实体类
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-31 上午10:26:17
 */
public class EventBean implements Serializable {
	private static final long serialVersionUID = 4617043918315208981L;

	private int eventId; 
	private String eventTitle;
	private String eventAddress;
	private String startTime;// 活动开始时间
	private String endTime; // 活动结束时间
	private int joinNumber;
	private String photoUrl;
	private String recommendPhotoUrl;// 推荐活动图片
	private String collectTime;//收藏时间
	private Boolean isCollect;
	
	private int barId;
	private String barName;
	private String barAddress;
	private String eventContent;

	/**
	 * @param obj
	 * @throws JSONException
	 */
	public EventBean(JSONObject obj) throws JSONException {

		if (obj.has("pub_id")) {
			this.eventId = obj.getInt("pub_id");
		}
		if (obj.has("id")) {
			this.eventId = obj.getInt("id");
		}

		if (obj.has("title")) {
			this.eventTitle = obj.getString("title");

		}
		if (obj.has("city_county")) {
			this.eventAddress = obj.getString("city_county");
		}

		if (obj.has("start_date")) {
			this.startTime = obj.getString("start_date");
		}
		if (obj.has("end_date")) {
			this.endTime = obj.getString("end_date");

		}
         if(obj.has("collect_time")){
        	this.collectTime = obj.getString("collect_time");
         }
		if (obj.has("join_people_number")) {
			this.joinNumber = obj.getInt("join_people_number");
		}
		if (obj.has("pic_path")) {
			this.photoUrl = BusinessHelper.PIC_BASE_URL + obj.getString("pic_path");
		}
		if (obj.has("pic_path")) {
			this.recommendPhotoUrl = BusinessHelper.PIC_BASE_URL + obj.getString("pic_path");
		}

		if (obj.has("pub_name")) {
			this.barName = obj.getString("pub_name");
		}
		if (obj.has("county")) {
			this.barAddress = obj.getString("county");
		}

		if (obj.has("activity_info")) {
			this.eventContent = obj.getString("activity_info");
		}
		if(obj.has("is_collect")){
			this.isCollect = obj.getBoolean("is_collect");
		}

	}

	/**
	 * 构建list list 最后得到的数据是:解析出的有所有的数据 使用 for 循环加载所有的 Array
	 **/
	public static List<EventBean> constractList(JSONArray array) throws JSONException {
		List<EventBean> list = new ArrayList<EventBean>();
		for (int i = 0; i < array.length(); i++) {
			EventBean bean = new EventBean(array.getJSONObject(i));
			list.add(bean);
		}
		return list;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int id) {
		this.eventId = id;
	}

	public int getBarId() {
		return barId;
	}

	public void setBarId(int barId) {
		this.barId = barId;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getEventAddress() {
		return eventAddress;
	}

	public void setEventAddress(String eventAddress) {
		this.eventAddress = eventAddress;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getJoinNumber() {
		return joinNumber;
	}

	public void setJoinNumber(int joinNumber) {
		this.joinNumber = joinNumber;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getRecommendPhotoUrl() {
		return recommendPhotoUrl;
	}

	public void setRecommendPhotoUrl(String recommendPhotoUrl) {
		this.recommendPhotoUrl = recommendPhotoUrl;
	}

	public String getBarName() {
		return barName;
	}

	public void setBarName(String barName) {
		this.barName = barName;
	}

	public String getBarAddress() {
		return barAddress;
	}

	public void setBarAddress(String barAddress) {
		this.barAddress = barAddress;
	}

	public String getEventContent() {
		return eventContent;
	}

	public void setEventContent(String eventContent) {
		this.eventContent = eventContent;
	}

	public String getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}

	public Boolean getIsCollect() {
		return isCollect;
	}

	public void setIsCollect(Boolean isCollect) {
		this.isCollect = isCollect;
	}

}
