package se.kth.ID2216.bdrem.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MyUtils {
	public static final String TAG = "BdRem";
	public static final String FRIENDLIST_CHANGED = "FRIENDLIST_CHANGED";

	public static String getCurrentMonth() {
		return getTodaysDate()[1];
	}

	public static String[] getCurrentWeekDays() {
		return new String[] {
				getTodaysDate()[2],
				getLastDayofCurrentWeek().length() == 1 ? "0"
						+ getLastDayofCurrentWeek() : getLastDayofCurrentWeek() };
	}

	public static String[] getTodaysDate() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String todaysDate = dateFormat.format(cal.getTime());
		String[] dates = todaysDate.split("-");
		return dates;
	}

	public static String getLastDayofCurrentWeek() {
		int yearValue = Integer.valueOf(getTodaysDate()[0]);
		int monthValue = Integer.valueOf(getTodaysDate()[1]);
		int dayValue = Integer.valueOf(getTodaysDate()[2]);

		Calendar calendar = new GregorianCalendar(yearValue, monthValue,
				dayValue);

		return String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
	}
}
