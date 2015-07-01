package com.liferay.mobile.screens.osos.gestures;

/**
 * @author Javier Gamarra
 */
public interface FlingListener {

	void onFling(Movement movement);

	enum Movement {
		UP, LEFT, RIGHT, DOWN, TOUCH
	}
}
