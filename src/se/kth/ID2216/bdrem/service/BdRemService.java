/*
 * author: Prajwol
 */

package se.kth.ID2216.bdrem.service;

import static se.kth.ID2216.bdrem.util.MyUtils.TAG;
import se.kth.ID2216.bdrem.R;
import se.kth.ID2216.bdrem.proxy.fb.MyFacebook;
import se.kth.ID2216.bdrem.ui.Main;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

//ref - http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/app/AlarmService_Service.html
public class BdRemService extends Service {
	private final IBinder binder = new MyBinder();

	private MyFacebook fb = MyFacebook.getInstance();
	// private Main main = null; //use this to access Main

	private NotificationManager myNotification;

	@Override
	public void onCreate() {
		myNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		showNotification();
		new Thread(mTask, "BdRemService").start();
		Log.i(TAG, "bdremservice- Service created");
	}

	@Override
	public void onDestroy() {
		myNotification.cancel(R.string.app_name);
		Toast.makeText(this, "Birthday Reminder service finished",
				Toast.LENGTH_SHORT).show();
	}

	Runnable mTask = new Runnable() {
		public void run() {
			//TODO
			// 1. Update the local DB from facebook 
			// 2. Note - local DB should be merged than replaced
			// 3. Get the friends who have birthday today
			// 2. Post the appropriate message in their wall OR/AND
			// 3. Show alert
			// 4. (2) and (3) should be based on settings like
			// - automatic post, global/personal message

			// E.g. to post the message - this is for sara :)
			Log.i(TAG, "bdremservice- Tying to post");
			//fb.post("569335195", "Testing at " + new Date());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BdRemService.this.stopSelf();
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	private void showNotification() {
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, Main.class), 0);

		Notification notification = new Notification(R.drawable.user,
				"Birthday Reminder service is running", System
						.currentTimeMillis());
		notification.setLatestEventInfo(this, "Service in action",
				"Birthday Reminder service is running", contentIntent);

		myNotification.notify(R.string.app_name, notification);
	}

	// public void setMain(Main main) {
	// this.main = main;
	// }

	class MyBinder extends Binder {
		BdRemService getService() {
			return BdRemService.this;
		}
	}
}
