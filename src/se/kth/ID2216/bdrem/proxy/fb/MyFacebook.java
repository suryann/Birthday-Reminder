/*
 * author: Prajwol
 */
package se.kth.ID2216.bdrem.proxy.fb;

import static se.kth.ID2216.bdrem.util.MyUtils.TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.kth.ID2216.bdrem.proxy.model.Filter;
import se.kth.ID2216.bdrem.proxy.model.MyFriend;
import se.kth.ID2216.bdrem.proxy.model.RequestType;
import se.kth.ID2216.bdrem.ui.Main;
import se.kth.ID2216.bdrem.util.Note;
import se.kth.ID2216.bdrem.util.Report;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class MyFacebook {
	public static final String APP_ID = "187131474654587"; // imp

	private static MyFacebook me = null;
	private Facebook mFacebook;
	private boolean isReady;
	private AsyncFacebookRunner mAsyncRunner;

	private Main main;
	private List<MyFriend> myFriends = new ArrayList<MyFriend>();

	private MyFacebook() {
	}

	public static synchronized MyFacebook getInstance() {
		if (me == null) {
			me = new MyFacebook();
		}
		return me;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void init(Main main) {
		this.main = main;
		mFacebook = new Facebook(APP_ID);
		isReady = false;
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		mFacebook.authorize(main, new String[] { "publish_stream",
				"friends_birthday" }, new MyAuthorizeListener());
	}

	// ref- http://developers.facebook.com/docs/reference/api/user/
	public Report reLoadAllFriends() {
		if (!isReady) {
			Log.v(TAG, "myfacebook.reloadallfriends Not ready yet!");
			return new Report(false, "Not ready yet!");
		}

		Bundle params = new Bundle();
		params.putString("fields", "id,name,birthday,picture");
		mAsyncRunner.request("me/friends", params, new MyRequestListener(
				RequestType.FRIEND_LIST));

		Log.v(TAG, "myfacebook.reloadallfriends Fetch started.");
		return new Report(true, "Fetch started");
	}

	public List<MyFriend> getAllFriends() {
		return getFilteredFriends(null);
	}

	public List<MyFriend> getFilteredFriends(Filter filterBy) {
		return Main.db.getFriendsFilteredBy(filterBy);
	}

	public List<Map<String, String>> getAllFriendsAsMap() {
		return getFilteredFriendsAsMap(null);
	}

	public List<Map<String, String>> getFilteredFriendsAsMap(Filter filterBy) {
		List<MyFriend> friendList = Main.db.getFriendsFilteredBy(filterBy);

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (MyFriend friend : friendList) {
			list.add(friend.getMap());
		}
		return list;
	}

	public void post(String receiver, String message) {
		if (isReady) {
			Bundle params = new Bundle();
			params.putString("message", message);

			mAsyncRunner.request(receiver + "/feed", params, "POST",
					new MyRequestListener(RequestType.FEED_POST));
		}
	}

	class MyAuthorizeListener extends BaseDialogListener {
		public void onComplete(Bundle values) {
			Log.i(TAG, "Authorization successfull");
			isReady = true;
			main.loadContents();
		}
	}

	class MyRequestListener extends BaseRequestListener {
		private RequestType type;

		public MyRequestListener(RequestType type) {
			this.type = type;
		}

		public void onComplete(final String response) {
			try {
				switch (type) {
				case FRIEND_LIST:
					// Log.d(TAG, "myfacebook.friendlist Response: "
					// + response.toString());
					myFriends.clear();
					JSONArray jarr = Util.parseJson(response).getJSONArray(
							"data");
					for (int i = 0; i < jarr.length(); i++) {
						JSONObject json = jarr.getJSONObject(i);
						String fbID = json.getString("id");
						String name = json.getString("name");
						String bday = json.optString("birthday");// notice opt
						String pic = json.getString("picture");

						myFriends.add(new MyFriend(fbID, name, bday, pic));
					}
					main.notifyMain(Note.FRIENDLIST_RELOADED);
					break;
				case FEED_POST:
					Log.d(TAG, "myfacebook.feedpost Response: "
							+ response.toString());
					break;
				default:
					break;
				}
			} catch (JSONException e) {
				Log.e(TAG, "JSONException: " + e.getMessage());
			} catch (FacebookError e) {
				Log.e(TAG, "FacebookError: " + e.getMessage());
			}
		}
	}

	public boolean isReady() {
		return isReady;
	}

	public int getFriendsCount() {
		return myFriends.size();
	}

	public List<MyFriend> getMyFriends() {
		return myFriends;
	}

	public void setMyFriends(List<MyFriend> myFriends) {
		this.myFriends = myFriends;
	}
}
