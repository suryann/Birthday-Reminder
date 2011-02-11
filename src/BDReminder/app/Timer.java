/*
 * Author - prajwolkumar.nakarmi@gmail.com
 */

package BDReminder.app;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class Timer extends Service {
	private static final String TAG = "Timer";
	private final IBinder binder = new MyBinder();
	private Main main = null;
	private boolean isRunning = false;

	@Override
	public void onCreate() {
		Log.i(TAG, "Service created");
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "Service started with flag - " + flags);
		if (!isRunning) {
			isRunning = !isRunning;

			(new Thread() {
				public void run() {
					while (isRunning) {
						if (main == null) {
							continue;
						}
						
						Calendar c = Calendar.getInstance();
						String now = c.get(Calendar.HOUR_OF_DAY) + ":"
								+ c.get(Calendar.MINUTE);
						Log.i(TAG, "Timer Tick: " + now + ", "
								+ main.getTimeToMatch());
						if (now.equals(main.getTimeToMatch())) {
							main.post();
							main.clearTimeToMatch();
							Log.i(TAG, "Posted");
						}

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// wake up ;)
						}
					}
				}
			}).start();
		}
		return Service.START_STICKY;
	}

	class MyBinder extends Binder {
		Timer getService() {
			return Timer.this;
		}
	}

	public void setMain(Main main) {
		this.main = main;
	}
}
