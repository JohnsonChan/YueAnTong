package com.czs.yat.activity;


import com.czs.yat.R;
import com.czs.yat.R.id;
import com.czs.yat.R.layout;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity
{
	private AlphaAnimation animation = null;
//	private TextView wisdomTexView = null;
	private RelativeLayout backgroundRelativeLayout = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
//		wisdomTexView = (TextView) findViewById(R.id.tv_wisdom);
		backgroundRelativeLayout = (RelativeLayout) findViewById(R.id.rlt_splash_body);

//		String wisdoms[] = getResources().getStringArray(R.array.wisdom);
//		Random random = new Random(System.currentTimeMillis());
//		wisdomTexView.setText(wisdoms[random.nextInt(wisdoms.length)]);

		animation = new AlphaAnimation(0.0f, 1.0f);
//		animation.setDuration(1000 * (random.nextInt(5) + 1));
		animation.setDuration(1000);
		animation.setAnimationListener(new AnimationListener()
		{
			public void onAnimationStart(Animation animation)
			{
			}

			public void onAnimationRepeat(Animation animation)
			{
			}

			public void onAnimationEnd(Animation animation)
			{
				startActivity(new Intent(SplashActivity.this,
						MainActivity.class));
				unregisterReceiver(broadcastReceiver);
				finish();
			}
		});
		backgroundRelativeLayout.setAnimation(animation);

	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			finish();
		}
	};

	protected void onDestroy()
	{
		super.onDestroy();
		animation = null;
//		wisdomTexView = null;
	};

	@Override
	public void onResume()
	{
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction("");
		this.registerReceiver(this.broadcastReceiver, filter);
	}
}
