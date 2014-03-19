package com.czs.yat;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.czs.yat.LoginTask.LoginListener;
import com.czs.yat.ResultGetListTask.ResultsGetListListener;
import com.czs.yat.SignUpTask.SignUpListener;
import com.czs.yat.data.Result;
import com.czs.yat.data.YatConstants;
import com.czs.yat.view.DimPopupWindow;
import com.czs.yat.view.SlideMenu;

public class MainActivity extends Activity implements ResultsGetListListener,
		LoginListener, SignUpListener
{
	private DimPopupWindow loginDimPopupWindow = null;
	private DimPopupWindow signupDimPopupWindow = null;
	private TextView loginTitleTextView = null;
	private TextView signupTitleTextView = null;
	private LinearLayout unLoginLayout = null;
	private TextView loginAccountTextView;

	private SlideMenu slideMenu;
	private TextView loginTextView = null;
	private TextView signupTextView = null;
	private TextView indexTextView = null;
	private TextView titleTextView = null;
	private ImageView titleImageView = null;
	private TextView chemTextView = null;
	private TextView lawTextView = null;
	private TextView standardTextView = null;
	private TextView operationTextView = null;
	private TextView lawBasisTextView = null;
	private TextView equipmentTextView = null;
	private TextView licensingTextView = null;
	private TextView contactsTextView = null;
	private TextView exitTextView = null;
	private ImageView menuImageView = null;
	private EditText keyEditText = null;
	private ArrayList<Result> results = null;
	private ResultAdapter resultAdapter = null;
	private ListView resultListView = null;
	private Button searchButton = null;
	// private LinearLayout loadLinearLayout = null;
	private String keyValue = null;
	private String keyType = null;
	private String account = null;
	private String password = null;

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;

	private void showLoginWindow()
	{
		View view = getLayoutInflater().inflate(R.layout.popup_login, null,
				false);
		this.loginDimPopupWindow = new DimPopupWindow(MainActivity.this,
				R.style.popwin_animation, Gravity.CENTER);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		this.loginDimPopupWindow.setWindowWidth((int) (dm.widthPixels * 0.9));
		this.loginDimPopupWindow.setContentView(view);
		this.loginDimPopupWindow.setBackDismiss(true);
		this.loginTitleTextView = (TextView) view
				.findViewById(R.id.tv_pl_title);
		this.loginTitleTextView.setText(R.string.popup_login_title);
		final AutoCompleteTextView accountEditText = (AutoCompleteTextView) view
				.findViewById(R.id.at_pl_account);

		final EditText passwordEditText = (EditText) view
				.findViewById(R.id.et_pl_password);

		TextView cancelTextView = (TextView) view
				.findViewById(R.id.tv_pl_cancel);
		TextView loginTextView = (TextView) view.findViewById(R.id.tv_pl_login);

		cancelTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainActivity.this.loginDimPopupWindow.dismiss();
				MainActivity.this.loginDimPopupWindow = null;
			}
		});
		loginTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				account = accountEditText.getText().toString();
				password = passwordEditText.getText().toString();
				if (account != null && "".equals(account))
				{
					Toast.makeText(MainActivity.this,
							R.string.account_not_blank, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (password != null && "".equals(password))
				{
					Toast.makeText(MainActivity.this,
							R.string.password_not_blank, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				new LoginTask(account, password, MainActivity.this).execute();
				MainActivity.this.loginTitleTextView.setText(R.string.logining);
			}
		});
		this.loginDimPopupWindow.show(MainActivity.this);
	}

	private void showSignUpWindow()
	{
		View view = getLayoutInflater().inflate(R.layout.popup_signup, null,
				false);
		this.signupDimPopupWindow = new DimPopupWindow(MainActivity.this,
				R.style.popwin_animation, Gravity.CENTER);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		this.signupDimPopupWindow.setWindowWidth((int) (dm.widthPixels * 0.9));
		this.signupDimPopupWindow.setContentView(view);
		this.signupDimPopupWindow.setBackDismiss(true);
		this.signupTitleTextView = (TextView) view
				.findViewById(R.id.tv_ps_title);
		this.signupTitleTextView.setText(R.string.popup_signup_title);
		final AutoCompleteTextView accountEditText = (AutoCompleteTextView) view
				.findViewById(R.id.at_ps_account);

		final EditText passwordEditText = (EditText) view
				.findViewById(R.id.et_ps_password);

		final EditText ensureEditText = (EditText) view
				.findViewById(R.id.et_ps_password_ensure);

		TextView cancelTextView = (TextView) view
				.findViewById(R.id.tv_ps_cancel);
		TextView signupTextView = (TextView) view
				.findViewById(R.id.tv_ps_signup);

		cancelTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainActivity.this.signupDimPopupWindow.dismiss();
				MainActivity.this.signupDimPopupWindow = null;
			}
		});
		signupTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				account = accountEditText.getText().toString();
				password = passwordEditText.getText().toString();
				String ensure = ensureEditText.getText().toString();
				if (account != null && "".equals(account))
				{
					Toast.makeText(MainActivity.this,
							R.string.account_not_blank, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (password != null && "".equals(password))
				{
					Toast.makeText(MainActivity.this,
							R.string.password_not_blank, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (ensure != null && (!password.equals(ensure)))
				{
					Toast.makeText(MainActivity.this,
							R.string.password_not_same, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				new SignUpTask(account, password, MainActivity.this).execute();
				MainActivity.this.signupTitleTextView
						.setText(R.string.signuping);
			}
		});
		this.signupDimPopupWindow.show(MainActivity.this);
	}

	private void initSlideMenu()
	{
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);

		unLoginLayout = (LinearLayout) slideMenu.findViewById(R.id.llt_unlogin);
		loginAccountTextView = (TextView) slideMenu
				.findViewById(R.id.tv_account);
		loginAccountTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showSignUpWindow();
			}
		});

		loginTextView = (TextView) slideMenu.findViewById(R.id.tv_login);
		loginTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showLoginWindow();
			}
		});

		signupTextView = (TextView) slideMenu.findViewById(R.id.tv_signup);
		signupTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showSignUpWindow();
			}
		});

		indexTextView = (TextView) slideMenu.findViewById(R.id.tv_index);
		indexTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				titleTextView.setText(R.string.global_index);
				titleImageView.setImageResource(R.drawable.menu_index);
				showMenu();
			}
		});

		chemTextView = (TextView) slideMenu.findViewById(R.id.tv_chemistry);
		chemTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				keyType = YatConstants.keyChem;
				titleTextView.setText(R.string.global_chemistry);
				titleImageView.setImageResource(R.drawable.menu_chem);
				showMenu();
			}
		});

		lawTextView = (TextView) slideMenu.findViewById(R.id.tv_law);
		lawTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				keyType = YatConstants.keyLaw;
				titleTextView.setText(R.string.global_law);
				titleImageView.setImageResource(R.drawable.menu_law);
				showMenu();
			}
		});

		standardTextView = (TextView) slideMenu.findViewById(R.id.tv_standard);
		standardTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				keyType = YatConstants.keyStandard;
				titleTextView.setText(R.string.global_standard);
				titleImageView.setImageResource(R.drawable.menu_standard);
				showMenu();
			}
		});

		operationTextView = (TextView) slideMenu
				.findViewById(R.id.tv_operation);
		operationTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				keyType = YatConstants.keyOperation;
				titleTextView.setText(R.string.global_operation);
				titleImageView.setImageResource(R.drawable.menu_operation);
				showMenu();
			}
		});

		lawBasisTextView = (TextView) slideMenu.findViewById(R.id.tv_law_basis);
		lawBasisTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				keyType = YatConstants.keyLawBasic;
				titleTextView.setText(R.string.global_law_basis);
				titleImageView.setImageResource(R.drawable.menu_basic);
				showMenu();
			}
		});

		equipmentTextView = (TextView) slideMenu
				.findViewById(R.id.tv_equipment);
		equipmentTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				keyType = YatConstants.keyEquipment;
				titleTextView.setText(R.string.global_equipment);
				titleImageView.setImageResource(R.drawable.menu_equipment);
				showMenu();
			}
		});

		licensingTextView = (TextView) slideMenu
				.findViewById(R.id.tv_licensing);
		licensingTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				keyType = YatConstants.keyLicensing;
				titleTextView.setText(R.string.global_licensing);
				titleImageView.setImageResource(R.drawable.menu_company);
				showMenu();
			}
		});

		contactsTextView = (TextView) slideMenu.findViewById(R.id.tv_contacts);
		contactsTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				keyType = YatConstants.keyContacts;
				titleTextView.setText(R.string.global_contacts);
				titleImageView.setImageResource(R.drawable.menu_contact);
				showMenu();
			}
		});

		exitTextView = (TextView) slideMenu.findViewById(R.id.tv_exit);
		exitTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sharedPreferences = getPreferences(MODE_PRIVATE);
		editor = sharedPreferences.edit();

		initSlideMenu();

		account = sharedPreferences.getString(YatConstants.ACCOUNT, null);
		password = sharedPreferences.getString(YatConstants.PASSWORD, null);
		if (account != null && password != null)
		{
			new LoginTask(account, password, this).execute();
		}

		results = new ArrayList<Result>();

		resultAdapter = new ResultAdapter(results, this);
		resultListView = (ListView) findViewById(R.id.lv_list);
		resultListView.setAdapter(this.resultAdapter);

		menuImageView = (ImageView) findViewById(R.id.title_bar_menu_btn);
		titleTextView = (TextView) findViewById(R.id.title_bar_name);
		titleImageView = (ImageView) findViewById(R.id.iv_title_icon);
		keyEditText = (EditText) findViewById(R.id.et_key);
		searchButton = (Button) findViewById(R.id.btn_search);

		// loadLinearLayout = (LinearLayout) findViewById(R.id.llt_progress);

		searchButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (keyEditText.getText().toString().equals(""))
				{
					Toast.makeText(MainActivity.this, R.string.not_blank,
							Toast.LENGTH_SHORT).show();
				}
				else
				{
					new ResultGetListTask("", 0, keyType, keyValue,
							MainActivity.this).execute();
					// loadLinearLayout.setVisibility(View.VISIBLE);
				}
			}
		});

		resultListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Intent intent = new Intent(MainActivity.this,
						ResultActivity.class);
				intent.putExtra("result", results.get(position));
				startActivity(intent);
			}
		});

		menuImageView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				showMenu();
				return false;
			}
		});

	}

	/**
	 * 閲嶅啓鏂规硶锛歰nResume, @see android.app.Activity#onResume() 鍙傛暟锛� * 瀹炵幇鍔熻兘锛�
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
	}

	private void showMenu()
	{
		if (slideMenu.isMainScreenShowing())
		{
			slideMenu.openMenu();
		}
		else
		{
			slideMenu.closeMenu();
		}
	}

	@Override
	public void refreshResults(ArrayList<Result> resultList)
	{
		if (resultList != null)
		{
			for (int i = 0; i < resultList.size(); i++)
			{
				results.add(resultList.get(i));
			}
			// loadLinearLayout.setVisibility(View.INVISIBLE);
		}
		resultList = null;
		for (int i = 0; i < results.size(); i++)
		{
			System.out.println(results.get(i).toString());
		}
		resultAdapter.notifyDataSetChanged();
	}

	// 登录结果刷新
	@Override
	public void refreshLoginResult(int status, String message)
	{
		if (status == 0) // 成功
		{
			if (this.loginDimPopupWindow != null)
			{
				this.loginDimPopupWindow.dismiss();
				this.loginDimPopupWindow = null;
			}
			editor.putString(YatConstants.ACCOUNT, account);
			editor.putString(YatConstants.PASSWORD, password);
			editor.commit();
			loginAccountTextView.setText(getString(R.string.account_welcome,
					account));
			unLoginLayout.setVisibility(View.GONE);
			loginAccountTextView.setVisibility(View.VISIBLE);
		}
		if (MainActivity.this.loginTitleTextView != null)
		{
			MainActivity.this.loginTitleTextView
					.setText(R.string.popup_login_title);
		}
		if (message == null)
		{
			Toast.makeText(MainActivity.this, R.string.net_error,
					Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)
					.show();
		}
	}

	// 注册结果刷新
	@Override
	public void refreshSignUpResult(int status, String message)
	{
		MainActivity.this.signupTitleTextView
				.setText(R.string.popup_signup_title);
		if (status == 1) // 成功
		{
			this.signupDimPopupWindow.dismiss();
			this.signupDimPopupWindow = null;
			editor.putString(YatConstants.ACCOUNT, account);
			editor.putString(YatConstants.PASSWORD, password);
			editor.commit();
			loginAccountTextView.setText(getString(R.string.account_welcome,
					account));
			unLoginLayout.setVisibility(View.GONE);
			loginAccountTextView.setVisibility(View.VISIBLE);
		}
		if (message == null)
		{
			Toast.makeText(MainActivity.this, R.string.net_error,
					Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)
					.show();
		}
	}
}
