/*
 * author: Prajwol
 */
package se.kth.ID2216.bdrem.proxy.model;

import java.util.Date;

import se.kth.ID2216.bdrem.util.Report;

public class MyFriend {
	public String fbID;
	public String name;
	public Date bday;
	public String bdayMessage;
	public boolean isAutoPost;

	public MyFriend(String fbID, String name, Date bday, String bdayMessage,
			boolean isAutoPost) {
		this.fbID = fbID;
		this.name = name;
		this.bday = bday;
		this.bdayMessage = bdayMessage;
		this.isAutoPost = isAutoPost;
	}
	
	public Report post(String message){
		//1. post the message to this friend's wall
		//2. prepare appropriate Report, following is just an example
		return new Report(false, "Method not implemented yet!");
	}
}
