/*
 * author: Prajwol
 */

package se.kth.ID2216.bdrem.service;

import static se.kth.ID2216.bdrem.util.MyUtils.TAG;

import java.util.List;

import se.kth.ID2216.bdrem.R;
import se.kth.ID2216.bdrem.proxy.fb.MyFacebook;
import se.kth.ID2216.bdrem.proxy.model.Filter;
import se.kth.ID2216.bdrem.proxy.model.MyFriend;
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
			Log.i(TAG, "bdremservice- started");
			// 1. Update the local DB from facebook
			// 2. Note - local DB should be merged than replaced
			fb.reLoadAllFriends();// syncFriends B-)

			// 3. Get the friends who have birthday today
			List<MyFriend> bdayFriends = Main.db
					.getFriendsFilteredBy(Filter.DAY);

			// 2. Post the appropriate message in their wall OR/AND
			// 3. Show alert
			// 4. (2) and (3) should be based on settings like
			// - automatic post, global/personal message
			for (MyFriend friend : bdayFriends) {
				String message = Main.db.getSettings("global_message");
				Log
						.i(TAG, "bdremservice- Tying to post to "
								+ friend.getName());
				// fb.post(friend.getFbID(), message);
				//TODO::
			}

			Log.i(TAG, "bdremservice- finished");
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

	class MyBinder extends Binder {
		BdRemService getService() {
			return BdRemService.this;
		}
	}
}
