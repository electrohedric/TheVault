package constants;

import gl.Shader;

public class Shaders {
	
	public static Shader TEXTURE, COLOR;
	
	public static void init() {
		TEXTURE = new Shader("texture.shader", "u_Texture", "u_MVP", "u_BrightScale");
		COLOR = new Shader("color.shader", "u_Color", "u_MVP");
	}
	
	public static void destroy() {
		TEXTURE.delete();
		COLOR.delete();
	}
}
