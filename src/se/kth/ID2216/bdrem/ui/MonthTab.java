/*
 * author - Prajwol 
 * 			Saad
 */
package se.kth.ID2216.bdrem.ui;

import java.util.List;

import se.kth.ID2216.bdrem.R;
import se.kth.ID2216.bdrem.proxy.fb.MyFacebook;
import se.kth.ID2216.bdrem.proxy.model.Filter;
import se.kth.ID2216.bdrem.proxy.model.MyFriend;
import se.kth.ID2216.bdrem.util.MyUtils;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class MonthTab extends ListActivity {
	private MyFacebook fb = MyFacebook.getInstance();
	private BcReceiver bcr = null;
	private MyAdapter adapter;
	private List<MyFriend> list;
	private ProgressDialog busyDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		busyDialog = new ProgressDialog(this);
		busyDialog.setIndeterminate(true);
		busyDialog.setMessage("Please wait ...");
		busyDialog.show();
		refreshList();
	}

	private void refreshList() {
		// list = fb.getFilteredFriends(Filter.MONTH);
		list = fb.getFilteredFriends(Filter.MONTH);
		adapter = new MyAdapter(MonthTab.this, list);
		setListAdapter(adapter);
		busyDialog.dismiss();
	}

	@Override
	protected void onResume() {
		if (bcr == null) {
			bcr = new BcReceiver();
			registerReceiver(bcr, new IntentFilter(MyUtils.FRIENDLIST_CHANGED));
		}
		super.onResume();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		MyFriend friend = (MyFriend) this.getListAdapter().getItem(position);

		Intent intent = new Intent(MonthTab.this, PersonalGreeting.class);
		intent.putExtra("fbID", friend.getFbID());
		intent.putExtra("name", friend.getName());
		startActivity(intent);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.layout.menu, menu);
		return (super.onPrepareOptionsMenu(menu));
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.globalGreeting:
			intent = new Intent(MonthTab.this, GlobalGreeting.class);
			startActivity(intent);
			break;
		case R.id.Settings:
			intent = new Intent(MonthTab.this, Settings.class);
			startActivity(intent);
		}
		return (super.onOptionsItemSelected(item));
	}

	public class BcReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			runOnUiThread(new Runnable() {
				public void run() {
					refreshList();
				}
			});
		}
	}
}