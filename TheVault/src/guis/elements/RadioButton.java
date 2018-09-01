package guis.elements;

import constants.Mode;
import gl.Texture;

public class RadioButton extends Button {
	
	private static int nextId = 0; // universally unique ids for the entire program makes equals easier
	private int id;
	private RadioButtonChannel channel;
	
	/**
	 * Inherits {@link Button#Button(float, float, float, Texture, Mode, boolean, Runnable) Button}
	 * @param commonChannel Channel for which the buttons communicate their state
	 */
	public RadioButton(RadioButtonChannel commonChannel, float x, float y, float scale, Texture texture, Mode screen, boolean feedback, Runnable callback) {
		super(x, y, scale, texture, screen, feedback, callback);
		this.channel = commonChannel;
		this.id = nextId++;
		channel.add(this);
	}
	
	@Override
	void onEnter() {
		if(!isPressed())
			super.onEnter();
	}
	
	@Override
	void onExit() {
		if(!isPressed())
			super.onExit();
	}
	
	@Override
	public void handleRelease(int button) {
		if(mouseHovered && clickedOn) { // only run if the click was pressed and released on this button
			select();
		}
	}
	
	@Override
	public void select() {
		channel.setPressed(this);
		super.select();
	}
	
	/**
	 * @return <code>true</code> if this button is chosen
	 */
	public boolean isPressed() {
		return this.is(channel.getPressed());
	}
	
	/** determines if this button is another. Should use instead of <code>equals(Object other)</code> */
	public boolean is(RadioButton other) {
		if(other == null)
			return false;
		return id == other.id;
	}

}
