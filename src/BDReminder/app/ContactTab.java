package BDReminder.app;

import java.util.ArrayList;
import java.util.HashMap;
import BDReminder.app.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ContactTab extends ListActivity {
	/** Called when the activity is first created. */
	//SimpleAdapter mSchedule;
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		//setContentView(R.layout.row_tab);
		
		//ListView list = (ListView) findViewById(R.id.SCHEDULE);		 
		
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", "Saad");
		map.put("BD", "23rd Feburary, 2011");
		mylist.add(map);
		
		map = new HashMap<String, String>();
		map.put("name", "Sara");
		map.put("BD", "25th Feburary, 2011");
		mylist.add(map);
		
		map = new HashMap<String, String>();
		map.put("name", "Prawjol");
		map.put("BD", "30th Feburary, 2011");
		mylist.add(map);
		
		map = new HashMap<String, String>();
		map.put("name", "Nina");
		map.put("BD", "3rd March, 2011");
		mylist.add(map);
		
		map = new HashMap<String, String>();
		map.put("name", "Ali");
		map.put("BD", "13th March, 2011");
		mylist.add(map);
		
		map = new HashMap<String, String>();
		map.put("name", "Ahmed");
		map.put("BD", "29th March, 2011");
		mylist.add(map);
		
		SimpleAdapter mSchedule = new SimpleAdapter(this, mylist, R.layout.contact_tab,
		            new String[] {"name", "BD"}, new int[] {R.id.label, R.id.label2});
		this.setListAdapter(mSchedule);
		
}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		//Object o = this.getListAdapter().getItem(position);
		//String keyword = o.toString();
		Intent intent = new Intent(ContactTab.this, PersonalGreeting.class);
		startActivity(intent);
	//	Toast.makeText(this, "You selected: " + keyword, Toast.LENGTH_LONG)
	//			.show();

	}
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication())
				.inflate(R.layout.menu, menu);
		return(super.onPrepareOptionsMenu(menu));
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
		return(super.onOptionsItemSelected(item));
	}
}