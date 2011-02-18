/*
 * author: Prajwol
 */
package se.kth.ID2216.bdrem.proxy.localdb;

import java.util.ArrayList;
import java.util.List;

import se.kth.ID2216.bdrem.proxy.model.MyFriend;
import se.kth.ID2216.bdrem.util.Report;

public class MyLocalDB {

	public String getSetting(String key) {
		// INFO: local storage should have one table for key-value pair
		// 1. extract the value owned by key
		return "Not implemented yet";
	}

	public List<MyFriend> getAllFriends() {
		List<MyFriend> friends = new ArrayList<MyFriend>();
		// 1. extract all friends from localdb
		// 2. pupulate friends
		return friends;
	}

	public Report storeFriend(MyFriend friend) {
		// 1. if friend doesn't exist, insert it
		// 2. if friend exists, update it
		// 3. prepare appropriate Report, following is just an example
		return new Report(false, "Method not implemented yet!");
	}

	public Report removeFriend(MyFriend friend) {
		// 1. delete friend from local storage
		// 2. prepare appropriate Report, following is just an example
		return new Report(false, "Method not implemented yet!");
	}
}
