package guis.elements;

import java.util.ArrayList;
import java.util.List;

public class RadioButtonChannel {
	
	private List<RadioButton> buttons;
	private RadioButton pressed;
	
	/**
	 * Creates a Channel for which radiobuttons can communicate and provides feedback for the pressed button
	 * @param radioButtons List of RadioButtons to add to this channel. Programmer must specify default by calling select() on button
	 */
	public RadioButtonChannel() {
		this.buttons = new ArrayList<>();
		this.pressed = null; // don't know that yet. will be the first button added though
	}
	
	protected void setPressed(RadioButton justPressed) {
		if(pressed != null)
			pressed.brightScale = 0.0f; // return previously pressed button to normal
		justPressed.brightScale = -0.4f; // set newly pressed button darker to show chosen
		pressed = justPressed;
		
	}
	
	public RadioButton getPressed() {
		return pressed;
	}
	
	public void add(RadioButton button) {
		buttons.add(button);
	}
	
}
