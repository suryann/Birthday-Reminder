/*
 * author - Prajwol 
 * 			Saad
 */
package se.kth.ID2216.bdrem.ui;

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
		busyDialog.setMessage("Please wait ...");
		busyDialog.show();
	}

	private void refreshList() {		
		list = fb.getAllFriendsAsMap();
		adapter = new SimpleAdapter(this, list, R.layout.contact_tab,
				new String[] { "name", "bday" }, new int[] { R.id.label,
						R.id.label2 });
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(ContactTab.this, PersonalGreeting.class);
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
		case R.id.Alert1:
			intent = new Intent(ContactTab.this, Alert1.class);
			startActivity(intent);
			break;
		case R.id.Alert2:
			intent = new Intent(ContactTab.this, Alert2.class);
			startActivity(intent);
			break;
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