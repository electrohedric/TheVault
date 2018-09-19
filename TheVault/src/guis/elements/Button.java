package guis.elements;

import constants.Mode;
import gl.Texture;
import io.ClickListener;
import objects.GameObject;

public class Button extends GameObject implements ClickListener {

	private int width;
	private int height;
	protected boolean mouseHovered;
	private boolean feedback;
	protected boolean clickedOn;
	private boolean enabled;
	protected Runnable callback;
	
	/**
	 * Creates a button that executes a task when pressed
	 * @param x Center X position
	 * @param y Center Y position
	 * @param scale Percentage of texture size to render at
	 * @param texture Path to button texture
	 * @param feedback Will increase brightness when hovered if <code>true</code>
	 * @param callback {@link Runnable} function to call when pressed
	 */
	public Button(float x, float y, float scale, Texture texture, Mode screen, boolean feedback, Runnable callback) {
		super(x, y, 0, scale);
		this.width = texture.getWidth();
		this.height = texture.getHeight();
		this.mouseHovered = false;
		this.clickedOn = false;
		this.feedback = feedback;
		this.callback = callback;
		this.enabled = true;
		setActiveTexture(texture);
		ClickListener.addToCallback(this, screen);
	}
	
	/**
	 * Tests to see if the button was clicked on and sets <code>mouseHovered</code> property which will call <code>callback</code> on next call to <code>handleRelease</code>
	 * NOTE: ABSOLUTELY ESSENTIAL THIS IS CALLED FREQUENTLY OR BUTTON WILL NOT RESPOND
	 */
	public void update() {
		if(enabled) {
			if(AACollideMouse()) {
				if(!mouseHovered) {
					if(feedback)
						onEnter();
					mouseHovered = true;
				}
			} else {
				if(mouseHovered) {
					if(feedback)
						onExit();
					mouseHovered = false;
				}
			}
		} else
			mouseHovered = false;
	}
	
	void onEnter() {
		brightScale = 0.2f; // slight brightness increase to show highlighted
	}
	
	void onExit() {
		brightScale = 0.0f; // brightness normal when not highlighted
	}
	
	/** sole purpose is glitter and custom functionality when selected on top of user-defined function */
	public void select() {
		if(callback != null)
			callback.run();
	}
	
	
	@Override
	public void handleClick(int button) {
		if(mouseHovered && enabled)
			clickedOn = true;
		else
			clickedOn = false;
	}

	@Override
	public void handleRelease(int button) {
		if(mouseHovered && clickedOn && enabled) // only run if the click was pressed and released on this button
			select();
	}
	
	public boolean isMouseHovering() {
		return mouseHovered && enabled;
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
		onExit();
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean hasFeedback() {
		return feedback;
	}
	
	public void setFeedback(boolean feedback) {
		this.feedback = feedback;
	}

	public boolean isClickedOn() {
		return clickedOn;
	}

	public Runnable getCallback() {
		return callback;
	}
	
	public void setCallback(Runnable callback) {
		this.callback = callback;
	}

}
