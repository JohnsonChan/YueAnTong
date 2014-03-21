package com.czs.yat.net;


import org.json.JSONObject;

import android.os.AsyncTask;


import com.czs.yat.callback.SignUpListener;
import com.czs.yat.util.Http.HttpBuilder;

public class SignUpTask extends AsyncTask<Void, Void, String>
{
    private final static String URL = "/yat/admin/yatRegAction.jsp?";
	private String password = null; // 请求类型
	private String account = null; // key关键字
	private SignUpListener signUpListener = null; // 消息获取监听器
	private int status = -1;



	public SignUpTask(String account,
			String password, SignUpListener signUpListener)
	{
		this.account = account;
		this.password = password;
		this.signUpListener = signUpListener;
	}

	@Override
	protected String doInBackground(Void... params)
	{
		String message = null;
		HttpBuilder httpBuilder = new HttpBuilder();
		try
		{
			String result = httpBuilder
					.url(NetHelper.HOST + URL)
					.formBodyWithDefault("username", account, "password", password).post();
			System.out.println(result);
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
		signUpListener.refreshSignUpResult(status, result);
	}
}