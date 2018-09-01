package objects;

import gl.IndexBuffer;
import gl.VertexArray;
import gl.VertexBuffer;
import gl.VertexBufferFormat;

public class Line {
	
	private static VertexArray vao;
	private static VertexBufferFormat vboFormat;
	private static float[] positions = {
			0.0f, 0.0f,
		    1.0f, 0.0f
	};
	private static int indices[] = { 
			0, 1
	};
	public static IndexBuffer ibo;
	public static VertexBuffer vbo;
	
	public static void init() {
		vao = new VertexArray();
		vboFormat = new VertexBufferFormat();
		vboFormat.pushFloat(2); // x, y
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
