/*
 * Author - Prajwol
 * 			Saad
 */

package se.kth.ID2216.bdrem.ui;

import static se.kth.ID2216.bdrem.util.MyUtils.TAG;
import se.kth.ID2216.bdrem.R;
import se.kth.ID2216.bdrem.proxy.fb.MyFacebook;
import se.kth.ID2216.bdrem.service.BdRemService;
import se.kth.ID2216.bdrem.util.MyUtils;
import se.kth.ID2216.bdrem.util.Note;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

//ref- http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/app/AlarmService.html
public class Main extends TabActivity {
	private MyFacebook fb = MyFacebook.getInstance();

	private TabHost tabHost;
	private final String FRIENDS_TAB = "friends";
	private final String MONTH_TAB = "month";
	private final String WEEK_TAB = "week";

	private PendingIntent mAlarmSender;
	private boolean isAlarmSet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAlarmSender = PendingIntent.getService(Main.this, 0, new Intent(
				Main.this, BdRemService.class), 0);

		setContentView(R.layout.main);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		TabSpec contactSpec = tabHost.newTabSpec(FRIENDS_TAB).setIndicator(
				"Friends", getResources().getDrawable(R.drawable.image))
				.setContent(new Intent(Main.this, ContactTab.class));

		TabSpec monthSpec = tabHost.newTabSpec(MONTH_TAB).setIndicator(
				"Coming Month", getResources().getDrawable(R.drawable.image))
				.setContent(new Intent(Main.this, MonthTab.class));

		TabSpec weekSpec = tabHost.newTabSpec(WEEK_TAB).setIndicator(
				"Coming Week", getResources().getDrawable(R.drawable.image))
				.setContent(new Intent(Main.this, WeekTab.class));

		tabHost.addTab(contactSpec);
		tabHost.addTab(monthSpec);
		tabHost.addTab(weekSpec);

		tabHost.setCurrentTab(0);

		if (!fb.isReady()) {
			Log.v(TAG, "main- initiating facebook");
			fb.init(this);// after finishing, this will call loadContents itself
		}else{
			loadContents();
		}
	}

	public void loadContents() {
		// TODO
		// 1. if fb.myFriends has data don't do nothing
		// 1. contacttab should already have loaded it. return;
		// 2. if fb.myFriends has not data, populate if from localdb;
		// 3. if fb.myFriends has data now, notify(Note.FRIENDLIST_CHANGED)
		// 3. else if fb.myFriends still doesn't have no data,
		// fb.reLoadallFriends;
		// 4. initiate alarm

		// FIXME this is for test
		fb.reLoadAllFriends();

		if (!isAlarmSet) {
			setAlarm(true);
		}
	}

	private void setAlarm(boolean isON) {
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (isON) {
			// TODO change the following so that alarm repeats at say 2.00 hrs?
			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock
					.elapsedRealtime(), 15 * 1000, mAlarmSender);
			isAlarmSet = true;
			Log.v(TAG, "main- alarm set");
		} else {
			am.cancel(mAlarmSender);
			isAlarmSet = true;
			Log.v(TAG, "main- alarm cancelled");
		}
	}

	public void notify(Note what) {
		switch (what) {
		case FRIENDLIST_CHANGED:
			sendBroadcast(new Intent(MyUtils.FRIENDLIST_CHANGED));
			// setup timer
			break;
		default:
			break;
		}
	}
}