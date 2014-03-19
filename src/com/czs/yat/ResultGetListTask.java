package com.czs.yat;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.czs.yat.data.Result;
import com.czs.yat.util.Http.HttpBuilder;

public class ResultGetListTask extends AsyncTask<Void, Void, ArrayList<Result>>
{
	private String token = null; // 用户标志
	private int startId = 0; // 开始id
	private String keyType = null; // 请求类型
	private String keyValue = null; // key关键字
	private ResultsGetListListener newsGetListListener = null; // 消息获取监听器

	interface ResultsGetListListener
	{
		void refreshResults(ArrayList<Result> resultList);
	}

	public ResultGetListTask(String token, int startId, String keyType,
			String keyValue, ResultsGetListListener newsGetListListener)
	{
		this.token = token;
		this.startId = startId;
		this.keyType = keyType;
		this.keyValue = keyValue;
		this.newsGetListListener = newsGetListListener;
	}

	@Override
	protected ArrayList<Result> doInBackground(Void... params)
	{
		ArrayList<Result> results = null;
		HttpBuilder httpBuilder = new HttpBuilder();
		try
		{
			String result = httpBuilder
					.url("http://218.192.99.29/yat/yatChem.jsp")
					.formBodyWithDefault("token", token, "startId",
							String.valueOf(startId), "keyType", keyType,
							"keyValue", keyValue).post();
			if (null != result)
			{
				JSONObject jsonObject = new JSONObject(result);
				if (0 != jsonObject.getInt("status"))
				{
					JSONArray jsonArray = jsonObject
							.getJSONArray("result_list");
					results = new ArrayList<Result>();
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject newsJsonObject = jsonArray.getJSONObject(i);
						results.add(new Result(newsJsonObject
								.getInt("result_id"), newsJsonObject
								.getString("title"), newsJsonObject
								.getString("intro"), newsJsonObject
								.getString("url")));
					}
				}
			}
		}
		catch (Exception e)
		{
			return null;
		}
		return results;
	}

	@Override
	protected void onPostExecute(ArrayList<Result> resultList)
	{
		newsGetListListener.refreshResults(resultList);
	}
}
