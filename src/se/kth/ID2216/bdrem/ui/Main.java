/*
 * Author - Prajwol
 * 			Saad
 */

package se.kth.ID2216.bdrem.ui;

import static se.kth.ID2216.bdrem.util.MyUtils.TAG;
import se.kth.ID2216.bdrem.R;
import se.kth.ID2216.bdrem.proxy.fb.MyFacebook;
import se.kth.ID2216.bdrem.util.MyUtils;
import se.kth.ID2216.bdrem.util.Note;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Main extends TabActivity {
	private MyFacebook fb = MyFacebook.getInstance();

	private TabHost tabHost;
	private final String FRIENDS_TAB = "friends";
	private final String MONTH_TAB = "month";
	private final String WEEK_TAB = "week";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		// Log.v(TAG, "main- initiating facebook");
		fb.init(this);
	}

	public void loadContents() {
		fb.reLoadAllFriends();
	}

	public void notify(Note what) {
		switch (what) {
		case FRIENDLIST_CHANGED:
			sendBroadcast(new Intent(MyUtils.FRIENDLIST_CHANGED));
			break;
		default:
			break;
		}
	}
}