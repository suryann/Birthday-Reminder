/*
 * author - Prajwol 
 * 			Saad
 */
package se.kth.ID2216.bdrem.ui;

import static se.kth.ID2216.bdrem.util.MyUtils.TAG;

import java.util.List;
import java.util.Map;

import se.kth.ID2216.bdrem.R;
import se.kth.ID2216.bdrem.proxy.fb.MyFacebook;
import se.kth.ID2216.bdrem.util.MyUtils;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ContactTab extends ListActivity {
	private MyFacebook fb = MyFacebook.getInstance();
	private BcReceiver bcr = null;
	private SimpleAdapter adapter;
	private List<Map<String, String>> list;
	private ProgressDialog busyDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		busyDialog = new ProgressDialog(this);
		busyDialog.setIndeterminate(true);
		busyDialog.setMessage("Refreshing");
	}

	private void refreshList() {
		busyDialog.show();
		new Thread() {
			public void run() {
				list = fb.getAllFriendsAsMap();
				Log.v(TAG, "contactstab- refresh called. " + list.size());
				adapter = new SimpleAdapter(ContactTab.this,
						(List<? extends Map<String, ?>>) list,
						R.layout.contact_tab, new String[] { "fbID", "pic",
								"name", "bday" }, new int[] { 0, R.id.icon,
								R.id.label, R.id.label2 });
				 handler.sendEmptyMessage(0);
			}
		}.start();
	}

	@Override
	protected void onResume() {
		if (bcr == null) {
			bcr = new BcReceiver();
			registerReceiver(bcr, new IntentFilter(MyUtils.FRIENDLIST_CHANGED));
		}
		refreshList();
		super.onResume();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Map<String, String> params = (Map<String, String>) this
				.getListAdapter().getItem(position);

		Intent intent = new Intent(ContactTab.this, PersonalGreeting.class);
		intent.putExtra("fbID", params.get("fbID"));
		intent.putExtra("name", params.get("name"));
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
			intent = new Intent(ContactTab.this, GlobalGreeting.class);
			startActivity(intent);
			break;
		case R.id.Settings:
			intent = new Intent(ContactTab.this, Settings.class);
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

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setListAdapter(adapter);
			busyDialog.dismiss();
		}
	};
}