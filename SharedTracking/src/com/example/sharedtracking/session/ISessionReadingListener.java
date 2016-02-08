package com.example.sharedtracking.session;

import com.example.sharedtracking.response.ReadingResponse;

public interface ISessionReadingListener {

	public void onSessionRead(ReadingResponse response);
}
