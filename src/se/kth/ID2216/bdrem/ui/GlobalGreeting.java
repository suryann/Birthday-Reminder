package se.kth.ID2216.bdrem.ui;

import se.kth.ID2216.bdrem.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GlobalGreeting extends Activity {
	private TextView greeting;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.global_greeting);
		greeting = (TextView) findViewById(R.id.tvGlobalGreeting);
		greeting.setText(Main.db.getSettings("global_message"));
	}

	public void doBackToMain(View v) {
		finish();
	}

	public void doSaveGreeting(View v) {
		Main.db.setSettings("global_greeting", greeting.getText().toString());
		Toast.makeText(this, "Global Greeting Saved", Toast.LENGTH_SHORT)
				.show();
		finish();
	}
}