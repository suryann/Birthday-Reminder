/*
 * author: Sara, Prajwol
 */
package se.kth.ID2216.bdrem.proxy.localdb;

import static se.kth.ID2216.bdrem.util.MyUtils.TAG;

import java.util.ArrayList;
import java.util.List;

import se.kth.ID2216.bdrem.proxy.model.Filter;
import se.kth.ID2216.bdrem.proxy.model.MyFriend;
import se.kth.ID2216.bdrem.util.MyUtils;
import se.kth.ID2216.bdrem.util.Report;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class MyLocalDB {
	private static final String DATABASE_NAME = "bdrem2";
	private static final String TABLE_FRIEND = "friend";
	private static final String TABLE_SETTINGS = "settings";
	private static final int DATABASE_VERSION = 3;

	public static final String KEY_ID = "friendID";
	public static final int ID_COLUMN = 0;
	// The name and column index of each column in friend's table
	public static final String KEY_FBID = "facebookID";
	public static final int FBID_COLUMN = 1;
	public static final String KEY_NAME = "name";
	public static final int NAME_COLUMN = 2;
	public static final String KEY_BIRTHDAY = "birthday";
	public static final int BIRTHDAY_COLUMN = 3;
	public static final String KEY_PIC = "picture";
	public static final int PIC_COLUMN = 4;
	public static final String KEY_BDAYMESSAGE = "message";
	public static final int BDAYMESSAGE_COLUMN = 5;
	public static final String KEY_AUTOPOST = "autopost";
	public static final int AUTOPOST_COLUMN = 6;

	// The name and column index of each column in setting's table
	public static final String KEY_SETTINGS_ID = "settingsId";
	public static final String KEY_SETTINGS_VALUE = "settingsValue";
	public static final int SETTINGS_KEY_COLUMN = 1;
	public static final int SETTINGS_VALUE_COLUMN = 2;

	private static final String SETTINGS_CREATE = "create table "
			+ TABLE_SETTINGS + " ( " + KEY_SETTINGS_ID + KEY_SETTINGS_VALUE
			+ " );";

	private static final String FRIENDS_CREATE = "create table " + TABLE_FRIEND
			+ " (" + KEY_ID + " integer primary key autoincrement, " + KEY_FBID
			+ " int, " + KEY_NAME + " varchar(50)," + KEY_BIRTHDAY
			+ " char(25), " + KEY_PIC + " char(50)," + KEY_BDAYMESSAGE
			+ " varchar(255), " + KEY_AUTOPOST + " varchar(10)" + " );";

	private SQLiteDatabase localDB;
	// private final Context context;
	private LocalDBHelper dbHelper;

	public MyLocalDB(Context context) {
		// this.context = context;
		this.dbHelper = new LocalDBHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public void open() throws SQLException {
		localDB = dbHelper.getWritableDatabase();
	}

	private static class LocalDBHelper extends SQLiteOpenHelper {

		public LocalDBHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "DB create");
			Log.d(TAG, FRIENDS_CREATE);
			Log.d(TAG, SETTINGS_CREATE);
			db.execSQL(FRIENDS_CREATE);
			db.execSQL(SETTINGS_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "Upgrading from " + oldVersion + " to " + newVersion
					+ ", which may destroy all the old data.");

			// Drop the old table
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
			// Create a new one
			onCreate(db);
		}
	}

	// Local storage should have one table for key-value pair
	public String getSettings(String settingsKey) {
		String settingsValue = "";
		try {
			Cursor settings = localDB.query(true, TABLE_SETTINGS, new String[] {
					KEY_SETTINGS_ID, KEY_SETTINGS_VALUE }, KEY_SETTINGS_ID
					+ "=" + settingsKey, null, null, null, null, null);
			if ((settings.getCount() == 0) || !settings.moveToFirst()) {
				throw new SQLException("No settings found for key "
						+ settingsKey);
			}
			settingsValue = settings.getString(SETTINGS_VALUE_COLUMN);
		} catch (SQLiteException ex) {
			ex.getMessage();
			ex.getStackTrace();
		}
		return settingsValue;
	}

	public List<MyFriend> getAllFriends() {
		return getFriendsFilteredBy(null);
	}

	public MyFriend getFriendByFbID(String facebookId) {

		Cursor friendResults = localDB.query(false, TABLE_FRIEND, null,
				KEY_FBID + "=" + facebookId, null, null, null, null, null);

		if ((friendResults.getCount() == 0) || !friendResults.moveToFirst()) {
			return null;
		}

		MyFriend friend = new MyFriend();
		friend.setBdayMessage(friendResults.getString(BDAYMESSAGE_COLUMN));
		friend.setAutoPost(Boolean.valueOf(friendResults
				.getString(AUTOPOST_COLUMN)));
		return friend;
	}

	public List<MyFriend> getFriendsFilteredBy(Filter filterBy) {
		List<MyFriend> friends = new ArrayList<MyFriend>();
		String[] resultColumns = new String[] { KEY_ID, KEY_FBID, KEY_NAME,
				KEY_BIRTHDAY, KEY_PIC, KEY_BDAYMESSAGE, KEY_AUTOPOST };

		String selection = null;
		String[] selectionArgs = null;

		if (filterBy != null) {
			switch (filterBy) {
			case MONTH:
				selection = KEY_BIRTHDAY + " LIKE ?";
				selectionArgs = new String[1];
				selectionArgs[0] = MyUtils.getCurrentMonth() + "%";
				break;
			case WEEK:
				selection = KEY_BIRTHDAY + " >= ? and " + KEY_BIRTHDAY
						+ " <= ?";
				selectionArgs = new String[2];
				selectionArgs[0] = MyUtils.getCurrentMonth() + "/"
						+ MyUtils.getCurrentWeekDays()[0];
				selectionArgs[1] = MyUtils.getCurrentMonth() + "/"
						+ MyUtils.getCurrentWeekDays()[1];
				Log.v(TAG, "localdb- " + selectionArgs[0] + ","
						+ selectionArgs[1]);
				break;
			default:
				break;
			}
		}

		Cursor allRows = localDB.query(false, TABLE_FRIEND, resultColumns,
				selection, selectionArgs, null, null, null, null);

		if (allRows.moveToFirst()) {
			do {
				String fbId = allRows.getString(FBID_COLUMN);
				String name = allRows.getString(NAME_COLUMN);
				String birthday = allRows.getString(BIRTHDAY_COLUMN);
				String picture = allRows.getString(PIC_COLUMN);
				String message = allRows.getString(BDAYMESSAGE_COLUMN);
				boolean isAutoPost = (allRows.getString(AUTOPOST_COLUMN) == "false") ? false
						: true;
				MyFriend friend = new MyFriend(fbId, name, birthday, picture,
						message, isAutoPost);
				friends.add(friend);
			} while (allRows.moveToNext());
		}
		return friends;
	}

	public Report storeFriends(List<MyFriend> friends) {
		int count = 0;
		for (MyFriend friend : friends) {
			if (storeFriend(friend).isSuccess == true) {
				count++;
			}
		}

		if (count == friends.size()) {
			return new Report(true, count + " friends stored.");
		} else {
			return new Report(false, "Only " + count + " friends stored.");
		}
	}

	public Report storeFriend(MyFriend friend) {
		Cursor friendResults = localDB.query(false, TABLE_FRIEND, new String[] {
				KEY_ID, KEY_FBID }, KEY_FBID + "=" + friend.getFbID(), null,
				null, null, null, null);
		Report report = null;
		// If friend doesn't exist, insert it
		if ((friendResults.getCount() == 0) || !friendResults.moveToFirst()) {
			report = insertFriend(friend);
			return report;
		}
		// If friend exists, update it
		else {
			long rowId = friendResults.getLong(ID_COLUMN);
			String message = friend.getBdayMessage();
			boolean isAutoPost = friend.isAutoPost();
			report = saveMessage(rowId, message, isAutoPost);
			// report = updateFriend(rowId, friend);
			return report;
		}
	}

	// Insert friend in database
	public Report insertFriend(MyFriend friend) {
		ContentValues newFriend = new ContentValues();
		newFriend.put(KEY_FBID, friend.getFbID());
		newFriend.put(KEY_NAME, friend.getName());
		newFriend.put(KEY_BIRTHDAY, friend.getBday());
		newFriend.put(KEY_PIC, friend.getPic());
		newFriend.put(KEY_BDAYMESSAGE, friend.getBdayMessage());
		boolean isAutoPost = friend.isAutoPost();
		if (isAutoPost) {
			newFriend.put(KEY_AUTOPOST, "true");
		} else
			newFriend.put(KEY_AUTOPOST, "false");

		try {
			localDB.insert(TABLE_FRIEND, null, newFriend);
		} catch (SQLiteException ex) {
			ex.getMessage();
			ex.getStackTrace();
			return new Report(false, "Something went wrong!");
		}

		return new Report(true, "Friend inserted successfully!");
	}

	// Update friend in database
	public Report updateFriend(long rowId, MyFriend friend) {

		ContentValues updatedFriend = new ContentValues();

		updatedFriend.put(KEY_FBID, friend.getFbID());
		updatedFriend.put(KEY_NAME, friend.getName());
		updatedFriend.put(KEY_BIRTHDAY, friend.getBday());
		updatedFriend.put(KEY_PIC, friend.getPic());
		updatedFriend.put(KEY_BDAYMESSAGE, friend.getBdayMessage());
		boolean isAutoPost = friend.isAutoPost();
		if (isAutoPost) {
			updatedFriend.put(KEY_AUTOPOST, "true");
		} else
			updatedFriend.put(KEY_AUTOPOST, "false");

		try {
			String where = KEY_ID + "=" + rowId;
			localDB.update(TABLE_FRIEND, updatedFriend, where, null);
		} catch (SQLiteException ex) {
			ex.getMessage();
			ex.getStackTrace();
			return new Report(false, "Something went wrong!");
		}
		return new Report(true, "Friend is updated successfully!");
	}

	public Report saveMessage(long rowId, String message, boolean isAutoPost) {

		ContentValues updatedFriend = new ContentValues();
		updatedFriend.put(KEY_BDAYMESSAGE, message);

		if (isAutoPost) {
			updatedFriend.put(KEY_AUTOPOST, "true");
		} else
			updatedFriend.put(KEY_AUTOPOST, "false");

		try {
			String where = KEY_ID + "=" + rowId;
			localDB.update(TABLE_FRIEND, updatedFriend, where, null);
		} catch (SQLiteException ex) {
			ex.getMessage();
			ex.getStackTrace();
			return new Report(false, "Something went wrong!");
		}
		return new Report(true, "Friend is updated successfully!");
	}

	public Report saveMessageByFbID(String ID, String message,
			boolean isAutoPost) {

		ContentValues updatedFriend = new ContentValues();
		updatedFriend.put(KEY_BDAYMESSAGE, message);
		updatedFriend.put(KEY_AUTOPOST, isAutoPost ? "true" : "false");

		try {
			String where = KEY_FBID + " = ?";
			localDB.update(TABLE_FRIEND, updatedFriend, where,
					new String[] { ID });
		} catch (SQLiteException ex) {
			ex.getMessage();
			ex.getStackTrace();
			return new Report(false, "Something went wrong!");
		}
		return new Report(true, "Friend is updated successfully!");
	}

	public Report removeFriend(long rowId) {

		String where = KEY_ID + "=" + rowId;
		try {
			localDB.delete(TABLE_FRIEND, where, null);
		} catch (SQLiteException ex) {
			ex.getMessage();
			ex.getStackTrace();
			return new Report(false, "Something went wrong!");
		}
		return new Report(false, "Friend is removed successfully!");
	}

	public void close() {
		localDB.close();
	}
}
