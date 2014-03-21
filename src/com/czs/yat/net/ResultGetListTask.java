package com.czs.yat.net;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.czs.yat.callback.ResultsGetListListener;
import com.czs.yat.data.Result;
import com.czs.yat.data.SearchType;
import com.czs.yat.util.Http.HttpBuilder;
import com.czs.yat.util.LogUtil;

public class ResultGetListTask extends AsyncTask<Void, Void, ArrayList<Result>>
{
	private String token = null; // 用户标志
	private int startId = 0; // 开始id
	private SearchType searchType;  // 搜索的类型
	private String keyWord = null; // key关键字
	private ResultsGetListListener newsGetListListener = null; // 消息获取监听器
	

	public ResultGetListTask(String token, int startId, SearchType searchType,
			String keyWord, ResultsGetListListener newsGetListListener)
	{
		this.token = token;
		this.startId = startId;
		this.searchType = searchType;
		this.keyWord = keyWord;
		this.newsGetListListener = newsGetListListener;
	}

	@Override
	protected ArrayList<Result> doInBackground(Void... params)
	{
		ArrayList<Result> results = null;
		HttpBuilder httpBuilder = new HttpBuilder();
		try
		{
		    String url = null;
		    if (TextUtils.isEmpty(keyWord))
		    {
		       url = NetHelper.getUrl(searchType);
		    }
		    else {
                url = NetHelper.getQueryUrl(searchType);
            }
		    LogUtil.d(url);
			String result = httpBuilder
					.url(NetHelper.HOST + url)
					.formBodyWithDefault("token", token, "startId",
							String.valueOf(startId),
							"keyWord", keyWord).post();
			
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
								.getString("summary"), newsJsonObject
								.getString("url")));
					}
				}
			}
		}
		catch (Exception e)
		{
		    LogUtil.d("wewe");
		    e.printStackTrace();
			return null;
		}
		   LogUtil.d(results.size()+"");
		return results;
	}

	@Override
	protected void onPostExecute(ArrayList<Result> resultList)
	{
		newsGetListListener.refreshResults(resultList);
	}
}
