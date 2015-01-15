package com.weivapp.utils;

import java.util.List;

import android.app.Application;

import com.weivapp.model.UserInfo;

/**
 * Created by gvv-fueled on 10/06/14.
 */
public class AccountUtils {

	private static AccountUtils instance;
	private List<UserInfo> cachedContacts;





	private AccountUtils() {
	}

	public static synchronized void init() {

		if (null == instance) {
			instance = new AccountUtils();
		}
	}

	public static AccountUtils getInstance() {

		return instance;
	}


	public List<UserInfo> getCachedContacts() {

		return cachedContacts;
	}

	public void setCachedContacts(List<UserInfo> cachedContacts) {
		this.cachedContacts = cachedContacts;
	}
}
