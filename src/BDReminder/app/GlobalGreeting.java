package BDReminder.app;

import BDReminder.app.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class GlobalGreeting extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_greeting);
        
    }
    public void doBackToMain(View v) {
		finish();
	}
    public void doSaveGreeting(View v){
    	Toast.makeText(this, "Global Greeting Saved", Toast.LENGTH_SHORT)
		.show();
    	finish();
    }
}