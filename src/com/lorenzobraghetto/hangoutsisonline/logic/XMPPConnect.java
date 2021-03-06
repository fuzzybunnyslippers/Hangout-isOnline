package com.lorenzobraghetto.hangoutsisonline.logic;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.content.Context;
import android.content.SharedPreferences;

public class XMPPConnect {

	public static List<Friend> XMPPgetFriends(Context context, CallBack callBack) {
		List<Friend> friends = new ArrayList<Friend>();
		//final XMPPConnection connection = new XMPPConnection("gmail.com"); //Server is gmail.com for Google Talk.
		SharedPreferences pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE);

		SASLAuthentication.registerSASLMechanism(GTalkOAuthSASLMechanism.NAME,
				GTalkOAuthSASLMechanism.class);
		SASLAuthentication
				.supportSASLMechanism(GTalkOAuthSASLMechanism.NAME, 0);

		ConnectionConfiguration configuration = new ConnectionConfiguration(
				"talk.google.com", 5222, "gmail.com");
		configuration.setSASLAuthenticationEnabled(true);
		XMPPConnection connection = new XMPPConnection(configuration);

		try {
			connection.connect();
			connection.login(pref.getString("user", ""), pref.getString("token", "")); //Username and password.

		} catch (XMPPException e) {

			e.printStackTrace();
			return null;
		}
		callBack.onConnect();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		callBack.onDownloadFriends();

		Roster roasters = connection.getRoster();
		for (RosterEntry r : roasters.getEntries()) {
			String user = r.getUser();
			if (user != null) {
				if (r.getName() != null)
					friends.add(new Friend(r.getName(), roasters.getPresence(user)));
				else
					friends.add(new Friend(user, roasters.getPresence(user)));
			}

		}
		connection.disconnect();
		return friends;
	}

}
