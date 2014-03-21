package com.czs.yat.activity;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.czs.yat.R;
import com.czs.yat.data.Result;

public class ResultAdapter extends BaseAdapter
{
	private ArrayList<Result> results = null;
	private LayoutInflater layoutInflater = null;
	private Context context = null;

	public ResultAdapter(ArrayList<Result> results, Context context)
	{
		super();
		this.results = results;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount()
	{
		return results.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return results.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2)
	{
		Result result = results.get(arg0);
		ChatHolder chatHolder = null;
		if (null == convertView)
		{
			chatHolder = new ChatHolder();
			convertView = layoutInflater.inflate(R.layout.list_item, null);
			chatHolder.titleTextView = (TextView) convertView
					.findViewById(R.id.tv_li_title);
			chatHolder.introTextView = (TextView) convertView
					.findViewById(R.id.tv_li_intro);
			convertView.setTag(chatHolder);
		}
		else
		{
			chatHolder = (ChatHolder) convertView.getTag();
		}
		
		chatHolder.titleTextView.setText(result.getTitle());
		chatHolder.introTextView.setText(Html.fromHtml(result.getIntro()));

		return convertView;
	}

	private class ChatHolder
	{
		private TextView titleTextView; 
		private TextView introTextView; 
	}
}
