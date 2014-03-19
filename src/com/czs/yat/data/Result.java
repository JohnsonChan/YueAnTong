package com.czs.yat.data;

import java.io.Serializable;

// 搜索结果类
public class Result implements Serializable
{
	private int id; // 结果id
	private String title; // 标题
	private String intro; // 简介
	private String url; // 对应的url
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getIntro()
	{
		return intro;
	}
	public void setIntro(String intro)
	{
		this.intro = intro;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public Result(int id, String title, String intro, String url)
	{
		super();
		this.id = id;
		this.title = title;
		this.intro = intro;
		this.url = url;
	}
	@Override
	public String toString()
	{
		return "Result [id=" + id + ", title=" + title + ", intro=" + intro
				+ ", url=" + url + "]";
	}
   

}
