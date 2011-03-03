/*
 * author: Sara, Prajwol
 */
package se.kth.ID2216.bdrem.proxy.localdb;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.ContentValues;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import se.kth.ID2216.bdrem.proxy.model.MyFriend;
import se.kth.ID2216.bdrem.util.Report;

public class MyLocalDB {
	
	private static final String DATABASE_NAME = "birthdayReminderDB.db";
	private static final String TABLE_FRIEND = "friend";
	private static final String TABLE_SETTINGS = "settings";
	private static final int DATABASE_VERSION = 1;	
	
	public static final String KEY_ID = "friendID";
	public static final int ID_COLUMN = 1;
	//The name and column index of each column in friend's table
	public static final String KEY_FBID = "facebookID";
	public static final int FBID_COLUMN = 2;
	public static final String KEY_NAME = "name";
	public static final int NAME_COLUMN = 3;
	public static final String KEY_BIRTHDAY = "birthday";
	public static final int BIRTHDAY_COLUMN = 4;
	public static final String KEY_PIC = "picture";
	public static final int PIC_COLUMN = 5;
	public static final String KEY_BDAYMESSAGE = "message";
	public static final int BDAYMESSAGE_COLUMN = 6;
	public static final String KEY_AUTOPOST = "false";
	public static final int AUTOPOST_COLUMN = 7;	
	
	//The name and column index of each column in setting's table
	public static final String KEY_SETTINGS_ID = "settingsId";
	public static final String KEY_SETTINGS_VALUE = "settingsValue";
	public static final int SETTINGS_KEY_COLUMN = 1;
	public static final int SETTINGS_VALUE_COLUMN = 2;
	
	//Create a new database
	private static final String DATABASE_CREATE = "create table "  + 
	TABLE_FRIEND + " (" + KEY_ID + KEY_FBID + KEY_NAME + KEY_BIRTHDAY +
	KEY_PIC + KEY_BDAYMESSAGE + KEY_AUTOPOST + " );";
	
	private static final String SETTINGS_CREATE = "create table " + TABLE_SETTINGS
	+ " ( " + " );"; 
	
	private SQLiteDatabase localDB;
	private final Context context;
	private LocalDBHelper dbHelper;
	
	public MyLocalDB(Context context){
		this.context = context;
		this.dbHelper = new LocalDBHelper(context, DATABASE_NAME, 
				null, DATABASE_VERSION);
	}
	
	public MyLocalDB open() throws SQLException{
		try{
			localDB = dbHelper.getWritableDatabase();
		}
		catch(SQLiteException ex){
			localDB = dbHelper.getReadableDatabase();		
		}		
		return this;
	}
	
	private static class LocalDBHelper extends SQLiteOpenHelper{
		
		public LocalDBHelper(Context context, String name, 
				CursorFactory factory, int version){
			super(context, name, factory, version);			
		}
		
		@Override
		public void onCreate(SQLiteDatabase db){
			db.execSQL(DATABASE_CREATE);
		}
		 @Override
		 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			 Log.w("TASKDBAdapter", "Upgrading from " + oldVersion +
					 " to " + newVersion + ", which may destroy all the old data.");
			 
			 //Drop the old table
			 db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND);
			 //Create a new one
			 onCreate(db);
		 }		
	}	

	//Local storage should have one table for key-value pair
	public String getSettings(String settingsKey) {
		String settingsValue = "";
		try{
			Cursor settings = localDB.query(true, TABLE_SETTINGS, 
					new String[] {KEY_SETTINGS_ID, KEY_SETTINGS_VALUE},
					KEY_SETTINGS_ID + "=" + settingsKey, null, null, null, 
					null, null);
			if((settings.getCount() == 0) || !settings.moveToFirst()){
				throw new SQLException("No settings found for key " + settingsKey);
			}
			settingsValue = settings.getString(SETTINGS_VALUE_COLUMN);			
		}
		catch(SQLiteException ex){
			ex.getMessage();
			ex.getStackTrace();
		}		
		return settingsValue;
	}	
	
	public List<MyFriend> getAllFriends() {
		List<MyFriend> friends = new ArrayList<MyFriend>();		
		String[] resultColumns = new String[]{KEY_ID, KEY_FBID, KEY_NAME, 
				KEY_BIRTHDAY, KEY_PIC, KEY_BDAYMESSAGE, KEY_AUTOPOST};
		//Extract all friends from localDB
		Cursor allRows = localDB.query(true, TABLE_FRIEND, resultColumns, 
				null, null, null, null, null, null);
		//Populate friends
		if(allRows.moveToFirst()){
			do{
				String fbId = allRows.getString(FBID_COLUMN);
				String name = allRows.getString(NAME_COLUMN);
				String birthday = allRows.getString(BIRTHDAY_COLUMN);
				String picture = allRows.getString(PIC_COLUMN);
				String message = allRows.getString(BDAYMESSAGE_COLUMN);
				boolean isAutoPost = (allRows.getString(AUTOPOST_COLUMN) == "false") ?  false : true;
				MyFriend friend = new MyFriend(fbId, name, birthday, picture, 
						message, isAutoPost);
				friends.add(friend);
			}
			while(allRows.moveToNext());
		}
		return friends;
	}

	public Report storeFriend(MyFriend friend) {
		Cursor friendResults = localDB.query(true, TABLE_FRIEND, 
				new String[]{KEY_ID, KEY_FBID}, KEY_FBID + "=" + friend.getFbID(), 
				null, null, null, null, null); 
		Report report = null;
		//If friend doesn't exist, insert it
		if((friendResults.getCount() == 0) || !friendResults.moveToFirst()){			
			report = insertFriend(friend);
			return report;
		}
		//If friend exists, update it
		else{
			long rowId = friendResults.getLong(ID_COLUMN);
			report = updateFriend(rowId, friend);
			return report;
		}		
	}
	
	//Insert friend in database
	public Report insertFriend(MyFriend friend){
		ContentValues newFriend = new ContentValues();
		newFriend.put(KEY_FBID, friend.getFbID());
		newFriend.put(KEY_NAME, friend.getName());
		newFriend.put(KEY_BIRTHDAY, friend.getBday());
		newFriend.put(KEY_PIC, friend.getPic());
		newFriend.put(KEY_BDAYMESSAGE, friend.getBdayMessage());
		boolean isAutoPost = friend.isAutoPost();
		if(isAutoPost){
			newFriend.put(KEY_AUTOPOST, "true");
		}
		else
			newFriend.put(KEY_AUTOPOST, "false");
		
		try{
			localDB.insert(TABLE_FRIEND, null, newFriend);
		}
		catch(SQLiteException ex){
			ex.getMessage();
			ex.getStackTrace();
			return new Report(false, "Something went wrong!");
		}
		
		return new Report(true, "Friend inserted successfully!");
	}
	
	// Update friend in database
	public Report updateFriend(long rowId, MyFriend friend){
		ContentValues updatedFriend = new ContentValues();
		
		updatedFriend.put(KEY_FBID, friend.getFbID());
		updatedFriend.put(KEY_NAME, friend.getName());
		updatedFriend.put(KEY_BIRTHDAY, friend.getBday());
		updatedFriend.put(KEY_PIC, friend.getPic());
		updatedFriend.put(KEY_BDAYMESSAGE, friend.getBdayMessage());
		boolean isAutoPost = friend.isAutoPost();
		if(isAutoPost){
			updatedFriend.put(KEY_AUTOPOST, "true");
		}
		else
			updatedFriend.put(KEY_AUTOPOST, "false");
		
		try{
			String where = KEY_ID + "=" + rowId;
			localDB.update(TABLE_FRIEND, updatedFriend, where, null);
		}
		catch(SQLiteException ex){
			ex.getMessage();
			ex.getStackTrace();
			return new Report(false, "Something went wrong!");
		}
		return new Report(true, "Friend is updated successfully!");		
	}	

	public Report removeFriend(long rowId) {
		
		String where = KEY_ID + "=" + rowId; 
		try{			
			localDB.delete(TABLE_FRIEND, where, null);
		}
		catch(SQLiteException ex){
			ex.getMessage();
			ex.getStackTrace();
			return new Report(false, "Something went wrong!");
		}
		return new Report(false, "Friend is removed successfully!");
	}
	
	public void close(){
		localDB.close();
	}
}
