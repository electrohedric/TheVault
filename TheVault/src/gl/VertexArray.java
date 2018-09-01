package gl;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray {
	
	private int id;
	
	public VertexArray() {
		id = glGenVertexArrays();
	}
	
	public void bind() {
		glBindVertexArray(id);
	}
	
	public void unbind() {
		glBindVertexArray(0);
	}
	
	public void addBuffer(VertexBuffer vbo) {
		bind();
		vbo.bind();
		vbo.getFormat().enable();
	}
	
	public void delete() {
		glDeleteVertexArrays(id);
	}
	
}
