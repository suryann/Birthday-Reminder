/*
 * Author - prajwolkumar.nakarmi@gmail.com
 */

package BDReminder.app;

import org.json.JSONException;
import org.json.JSONObject;
import BDReminder.app.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TabHost.TabSpec;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class Main extends TabActivity {

	private static final String TAG = "Main";
	public static final String MY_APP_ID = "102923773115476";

	TabHost tabHost;
	final String FRIENDS_TAB = "friends";
	final String MONTH_TAB = "month";
	final String WEEK_TAB = "week";
	
	private TextView statusLabel;
	private EditText status;
	private TextView time;
	private Button queue;

	private String timeToMatch = "";
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	private Timer timer;

	private static final int TIME_DIALOG_ID = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		
		
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		TabSpec contactSpec = tabHost
				.newTabSpec(FRIENDS_TAB)
				.setIndicator("Friends",
						getResources().getDrawable(R.drawable.image))
				.setContent(
						new Intent(Main.this, ContactTab.class));

		TabSpec monthSpec = tabHost
				.newTabSpec(MONTH_TAB)
				.setIndicator("Coming Month",
						getResources().getDrawable(R.drawable.image))
				.setContent(new Intent(Main.this, MonthTab.class));

		// intent = new Intent().setClass(this, WeekTab.class);
		TabSpec weekSpec = tabHost
				.newTabSpec(WEEK_TAB)
				.setIndicator("Coming Week",
						getResources().getDrawable(R.drawable.image))
				.setContent(new Intent(Main.this, WeekTab.class));

		tabHost.addTab(contactSpec);
		tabHost.addTab(monthSpec);
		tabHost.addTab(weekSpec);

		tabHost.setCurrentTab(0);
	
		
		
		
		mFacebook = new Facebook(MY_APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		mFacebook.authorize(this, new String[] { "publish_stream" },
				new MyAuthorizeListener());

		bindService(new Intent(Main.this, Timer.class), mConnection,
				Context.BIND_AUTO_CREATE);
		startService(new Intent(Main.this, Timer.class));
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, 0, 0, true);
		}
		return null;
	}

	public void doSelectTime(View v) {
		showDialog(TIME_DIALOG_ID);
	}

	public void doQueue(View v) {
		timeToMatch = time.getText().toString();
		queue.setEnabled(false);
	}

	public void post() {
		Bundle params = new Bundle();
		params.putString("message", status.getText().toString());

		mAsyncRunner.request("me/feed", params, "POST",
				new MyStatusPostListner());
	}

	class MyAuthorizeListener extends BaseDialogListener {
		public void onComplete(Bundle values) {
			Log.i(TAG, "Authorization successfull");
			mAsyncRunner.request("me", new MyRequestListener());
		}
	}

	class MyRequestListener extends BaseRequestListener {
		public void onComplete(final String response) {
			try {
				Log.d(TAG, "Response: " + response.toString());
				JSONObject json = Util.parseJson(response);
				final String userName = json.getString("name");
//				Main.this.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						statusLabel.setText(userName + "'s status");
//					}
//				});
			} catch (JSONException e) {
				Log.e(TAG, "JSONException: " + e.getMessage());
			} catch (FacebookError e) {
				Log.e(TAG, "FacebookError: " + e.getMessage());
			}
		}
	}

	class MyStatusPostListner extends BaseRequestListener {
		public void onComplete(final String response) {
			Log.i(TAG, "Response: " + response.toString());
			clearTimeToMatch();
			Main.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					queue.setEnabled(true);
				}
			});
		}
	}

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			time.setText(hourOfDay + ":" + minute);
			queue.setEnabled(true);
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			timer = ((Timer.MyBinder) service).getService();
			timer.setMain(Main.this);
			Log.v(TAG, "Service connected");
		}

		public void onServiceDisconnected(ComponentName className) {
			timer = null;
		}
	};

	public String getTimeToMatch() {
		return timeToMatch;
	}

	public void clearTimeToMatch() {
		timeToMatch = "";
	}
}