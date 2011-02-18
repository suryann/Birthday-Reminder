/*
 * author: Prajwol
 */
package se.kth.ID2216.bdrem.proxy.fb;

import java.util.ArrayList;
import java.util.List;

import se.kth.ID2216.bdrem.proxy.model.Filter;
import se.kth.ID2216.bdrem.proxy.model.MyFriend;

public class MyFacebook {
	public static final String APP_ID = "187131474654587"; // imp
	private List<MyFriend> myFriends = new ArrayList<MyFriend>();

	public void reLoadAllFriends() {
		myFriends.clear();
		// 1. get friends from facebook
		// 2. fetch friends from localdb
		// 3. merge the information from 1 and 2
		// 4. populate myFriends
	}

	public List<MyFriend> getAllFriends() {
		return myFriends;
	}

	public List<MyFriend> getFilteredFriends(Filter filterBy) {
		List<MyFriend> filteredFriends = new ArrayList<MyFriend>();
		// 1. extract proper myFriend from myFriends based on filterBy
		// 2. populate filteredFriends
		return filteredFriends;
	}
}
