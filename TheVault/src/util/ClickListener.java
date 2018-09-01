package util;

import java.util.ArrayList;
import java.util.List;

import constants.Mode;

public interface ClickListener {
	
	public abstract void handleClick(int button);
	public abstract void handleRelease(int button);
	
	/** list that contains all Objects that wish to receive PLAY mouse click input */
	public static List<ClickListener> playMouseClickCallback = new ArrayList<>();
	/** list that contains all Objects that wish to receive TITLE mouse click input */
	public static List<ClickListener> titleMouseClickCallback = new ArrayList<>();
	/** list that contains all Objects that wish to receive SETUP mouse click input */
	public static List<ClickListener> setupMouseClickCallback = new ArrayList<>();
	
	public static void addToCallback(ClickListener self, Mode receiveType) {
		getCallbackList(receiveType).add(self);
	}
	
	public static List<ClickListener> getCallbackList(Mode mode) {
		switch(mode) {
		case PAUSED: break;
		case PLAY: return playMouseClickCallback;
		case TITLE: return titleMouseClickCallback;
		case SETUP: return setupMouseClickCallback;
		default: break;
		}
		// throw an error instead of returning null which just pushes the issue down the line
		throw new UnsupportedOperationException("The ClickListener doesn't support " + mode + " mode yet. Might want to add that.");
	}
	
}
