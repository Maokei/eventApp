package com.event.business.util;

import java.util.StringTokenizer;
import java.util.Vector;

public class CommentInfo {
	private String comment;
	private String mail;
	private String date;
	private String time;
	
	public CommentInfo() {}
	
	public void resolveComment(String line) {
		Vector<String> commentAndInfo = getTokens(line, "\"\t");
		
		if(commentAndInfo != null) {
			Vector<String> commentInfo = getTokens(commentAndInfo.get(0).trim(), " ()");
			mail = commentInfo.elementAt(0).trim();
			date = commentInfo.elementAt(1);
			time = commentInfo.elementAt(2);
			comment = commentAndInfo.elementAt(1).trim();
		}
	}
	
	private Vector<String> getTokens(String line, String delim) {
		Vector<String> tokens = new Vector<>();
		StringTokenizer strToken = new StringTokenizer(line, delim);
		while(strToken.hasMoreTokens()) {
			String token = strToken.nextToken().trim();
			if(!token.equals("")) {
				tokens.addElement(token);
			}
		}
		return tokens;
	}

	public String getComment() {
		return comment;
	}

	public String getMail() {
		return mail;
	}

	public String getDate() {
		return date;
	}

	public String getTime() {
		return time;
	}
}