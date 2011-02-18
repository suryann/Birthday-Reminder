/*
 * author: Prajwol
 */
package se.kth.ID2216.bdrem.proxy.model;

import java.util.HashMap;
import java.util.Map;

import se.kth.ID2216.bdrem.util.Report;

public class MyFriend {
	public String fbID;
	public String name;
	public String bday; // can be missing, be in form of mm/dd or mm/dd/yyyy
	public String pic;
	public String bdayMessage;
	public boolean isAutoPost;

	public MyFriend(String fbID, String name, String bday, String pic) {
		this.fbID = fbID;
		this.name = name;
		this.bday = bday;
		this.pic = pic;
	}

	public MyFriend(String fbID, String name, String bday, String bdayMessage,
			boolean isAutoPost) {
		this.fbID = fbID;
		this.name = name;
		this.bday = bday;
		this.bdayMessage = bdayMessage;
		this.isAutoPost = isAutoPost;
	}

	public Report post(String message) {
		// 1. post the message to this friend's wall
		// 2. prepare appropriate Report, following is just an example
		return new Report(false, "Method not implemented yet!");
	}

	public Map<String, String> getMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		map.put("bday", bday);
		return map;
	}
}
