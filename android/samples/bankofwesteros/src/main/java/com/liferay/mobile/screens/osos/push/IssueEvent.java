package com.liferay.mobile.screens.osos.push;

/**
 * @author Javier Gamarra
 */
public class IssueEvent {

	public IssueEvent(String message) {
		_message = message;
	}

	public String getMessage() {
		return _message;
	}

	private String _message;
}
