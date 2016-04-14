package com.waruler.shoppingnotes.bean;

public class ShoppingNotesBean {

	private int id;
	private String title;
	private long createTime;

	/** 便签在数据库中的id **/
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/** 便签的标题 **/
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/** 便签的创建时间 **/
	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
