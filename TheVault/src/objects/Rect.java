package objects;

import gl.IndexBuffer;
import gl.VertexArray;
import gl.VertexBuffer;
import gl.VertexBufferFormat;

/**
 * Rect VAO intended to be rendered using GL_TRIANGLES
 */
public class Rect {
	
	private static VertexArray vao;
	private static VertexBufferFormat vboFormat;
	private static float[] positions = {
		    -0.5f, -0.5f, 0.0f, 1.0f,
		     0.5f, -0.5f, 1.0f, 1.0f,
			 0.5f,  0.5f, 1.0f, 0.0f,
		    -0.5f,  0.5f, 0.0f, 0.0f
	};
	private static int indices[] = { 
			0, 1, 2,
			2, 3, 0
	};
	public static IndexBuffer ibo;
	public static VertexBuffer vbo;
	
	public static void init() {
		vao = new VertexArray();
		vboFormat = new VertexBufferFormat();
		vboFormat.pushFloat(2); // x, y
		vboFormat.pushFloat(2); // u, v
		vbo  = new VertexBuffer(positions, vboFormat);
		ibo = new IndexBuffer(indices);
		vao.addBuffer(vbo);
	}
	
	public static void bind() {
		vao.bind();
		ibo.bind();
	}
	
	public static void unbind() {
		vao.unbind();
		ibo.unbind();
	}
	
	public static void destroy() {
		vao.delete();
		vbo.delete();
		ibo.delete();
	}
}
