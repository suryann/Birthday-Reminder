package se.kth.ID2216.bdrem.ui;

import se.kth.ID2216.bdrem.R;
import se.kth.ID2216.bdrem.R.id;
import se.kth.ID2216.bdrem.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Toast;

public class Alert2 extends Activity {
	/** Called when the activity is first created. */
	private AbsoluteLayout message;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert2);

		message = (AbsoluteLayout) findViewById(R.id.abMessage);
	}

	public void doCreateGreeting(View v) {
		message.setVisibility(View.VISIBLE);
	}

	public void doSendGlobalGreeting(View v) {
		Toast.makeText(this, "Global Greeting Send", Toast.LENGTH_SHORT)
				.show();
		finish();
	}
	public void doPost(View v) {
		Toast.makeText(this, "Greeting Send", Toast.LENGTH_SHORT)
				.show();
		finish();
	}
}