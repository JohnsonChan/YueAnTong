package com.czs.yat;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.czs.yat.data.Result;

public class ResultActivity extends Activity
{
	private static final int CACHE_SIZE = 100 * 1024 * 1024; // 鏈�ぇ100M鐨勭紦瀛�
	private WebView resultWebView = null;
	private ImageButton backImageButton = null;
	private ImageButton searchImageButton = null;
	private ImageButton collectImageButton = null;
	private ImageButton moreImageButton = null;
	private Result result = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		resultWebView = (WebView) findViewById(R.id.wb_result);
		backImageButton = (ImageButton)findViewById(R.id.ib_back);
		searchImageButton = (ImageButton)findViewById(R.id.ib_search);
		collectImageButton = (ImageButton)findViewById(R.id.ib_collect);
		moreImageButton = (ImageButton)findViewById(R.id.ib_more);
		
		result = (Result)getIntent().getSerializableExtra("result");
		
		backImageButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
		searchImageButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
		collectImageButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(ResultActivity.this, R.string.global_collect_success, 5).show();
			}
		});
		
		moreImageButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
			}
		});

		WebSettings webSettings = resultWebView.getSettings();
		// webSettings.setAppCacheEnabled(true);
		webSettings.setAppCacheMaxSize(CACHE_SIZE);
		webSettings.setJavaScriptEnabled(true);
		resultWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//		webSettings.setPluginsEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		resultWebView.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				view.loadUrl(url); // webview閲岄潰鐨勯摼鎺ヨ繕鍦ㄥ悓涓�釜椤甸潰涓姞杞�
				return true;
			}
		});

		resultWebView
				.loadUrl(result.getUrl()); // 鎵嬪姩host淇敼
	}

}
