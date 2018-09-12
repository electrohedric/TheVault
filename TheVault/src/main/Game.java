package main;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import constants.Cursors;
import constants.Mode;
import constants.Shaders;
import constants.Sounds;
import constants.Textures;
import gl.Renderer;
import guis.PlayScreen;
import guis.SetupScreen;
import guis.TitleScreen;
import objects.Line;
import objects.Map;
import objects.Pawn;
import objects.Point;
import objects.Rect;
import util.animation.Animator;
import util.handling.Camera;
import util.handling.PawnHandler;
import util.input.ClickListener;
import util.input.Mouse;
import util.output.Log;
import util.output.Music;

public class Game {

	// The window handle
	public static long window;
	private static String TITLE = "Stain Game";
	public static int WIDTH;
	public static int HEIGHT;
	public static float delta = 0.0f;
	public static Mode mode = Mode.BLANK;
	public static Matrix4f proj = new Matrix4f(); // can't instantiate until WIDTH and HEIGHT are set
	public static Matrix4f projSave = new Matrix4f(); // projection matrix to save the initial state
	public static final Camera nullCamera = new Camera(0, 0); // null camera doesn't change and is mostly for rendering UIs
	public static Map map;
	public static List<Pawn> pawns = new ArrayList<>(); // list of pawns who are playing the game.
	
	public static void main(String[] args) {
		Log.log("LWJGL version " + Version.getVersion());

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private static void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints();

		// Create the window
		GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		WIDTH = vidMode.width();
		HEIGHT = vidMode.height();
		
		window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, glfwGetPrimaryMonitor(), 0);
		if (window == 0)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated
		// or released. This will be for events such as things that happen once, other
		// keys will be recognized with glfwGetKey
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true);
			}
		});
		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if(action == GLFW_PRESS) {
				List<ClickListener> clickList = ClickListener.getCallbackList(mode);
				for(int i = clickList.size() - 1; i >= 0; i--) // loop backwards to avoid concurrent modification
					clickList.get(i).handleClick(button);
			} else if(action == GLFW_RELEASE) {
				List<ClickListener> clickList = ClickListener.getCallbackList(mode);
				for(int i = clickList.size() - 1; i >= 0; i--)
					clickList.get(i).handleRelease(button);
			}
		});

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Disable v-sync. Run as fast as possible NO. Enable VSync Plz. This is how you destroy your GPU and CPU in one fell swoop
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		
		// init buffers and things after all our contexts have been created
		
		// This color is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		Log.log("OpenGL version " + glGetString(GL_VERSION));
		
		Log.log("Loading textures");
		Textures.init();
		Shaders.init();
		Log.log("Loading sounds");
		Sounds.init();
		Music.init();
		Log.log("Loading geometry");
		Cursors.init();
		Rect.init();
		Line.init();
		Point.init();
		Log.log("Loading the rest");
		proj = new Matrix4f().ortho(0, Game.WIDTH, 0, Game.HEIGHT, -1.0f, 1.0f);
		projSave = new Matrix4f(proj);
		map = new Map("map", Game.WIDTH * 0.5f, Game.HEIGHT * 0.5f);
		PawnHandler.getInstance(); // initial load. map must already be loaded
		
		// Set the clear color
		Renderer.setClearColor(0, 0, 0);

		/*
		 * +---+
		 * |   |
		 * +---+
		 * x, y, u, v
		 */
		
		// make stuff look nice
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		glEnable(GL_POINT_SMOOTH);
		glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
	}
	
	public static void checkError() {
		int error = glGetError();
		boolean errorOccured = false;
		while(error != 0) {
			System.out.println(error);
			error = glGetError();
			errorOccured = true;
		}
		if(errorOccured) {
			System.out.println("-----");
		}
	}
	
	public static void clearError() {
		while(glGetError() != 0) {} // just loop until error is 0
	}
	
	private static void loop() {
		TitleScreen.getInstance().switchTo();
		
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		double lastSystemTime = glfwGetTime();
		
		while(!glfwWindowShouldClose(window)) {
			updateGame();
			renderGame();
			
			glfwSwapBuffers(window); // swap the color buffers (tick)
			
			checkError();
			
			double currentSystemTime = glfwGetTime();
			delta = (float) (currentSystemTime - lastSystemTime);
			if(delta > 1.0f) delta = 0; // if delta is way too large (e.g. Game was out of focus) don't process a million frames, please
			lastSystemTime = currentSystemTime;
		}
		
		Textures.destroy();
		Shaders.destroy();
		Sounds.destroy();
		Cursors.destroy();
		Rect.destroy();
		Line.destroy();
		Point.destroy();
	}
	
	public static void restoreProj() {
		Game.proj = projSave;
	}
	
	public static void updateGame() {
		Mouse.getUpdate(); // poll mouse movement
		glfwPollEvents(); // poll keypress/click events
		Music.update(); // make sure music is update
		Animator.update(); // update MovingObject animations
		
		switch(mode) {
		case PAUSED: // in play mode, but paused
			
			break;
		case BLANK:
			
			break;
		case PLAY:
			PlayScreen.getInstance().update();
			break;
		case TITLE:
			TitleScreen.getInstance().update();
			break;
		case SETUP:
			SetupScreen.getInstance().update();
			break;
		default:
			break;
		}
	}
	
	public static void renderGame() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		
		switch(mode) {
		case PAUSED:
			
			break;
		case BLANK:
			
			break;
		case PLAY:
			PlayScreen.getInstance().render();
			break;
		case TITLE:
			TitleScreen.getInstance().render();
			break;
		case SETUP:
			SetupScreen.getInstance().render();
			break;
		default:
			break;
		}
	}
	
}