package com.czs.yat.net;


import org.json.JSONObject;

import android.os.AsyncTask;

import com.czs.yat.callback.LoginListener;
import com.czs.yat.util.Http.HttpBuilder;

public class LoginTask extends AsyncTask<Void, Void, String>
{
    private final static String URL = "/yat/admin/yatLogAction.jsp";
	private String password = null; // 请求类型
	private String account = null; // key关键字
	private LoginListener loginListener = null; // 消息获取监听器
	private int status = -1;   // 0成功，1密码有误，2用户名有误

	public LoginTask(String account,
			String password, LoginListener loginListener)
	{
		this.account = account;
		this.password = password;
		this.loginListener = loginListener;
	}

	@Override
	protected String doInBackground(Void... params)
	{
		String message = null;
		HttpBuilder httpBuilder = new HttpBuilder();
		try
		{
			String result = httpBuilder
					.url(NetHelper.HOST+URL)
					.formBodyWithDefault("username", account, "password", password).post();
			if (null != result)
			{
				JSONObject jsonObject = new JSONObject(result);
				status = jsonObject.getInt("status");
				message = jsonObject.getString("message");
			}
		}
		catch (Exception e)
		{
			return null;
		}
		return message;
	}

	@Override
	protected void onPostExecute(String result)
	{
		loginListener.refreshLoginResult(status, result);
	}
}