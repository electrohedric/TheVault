package gl;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import constants.Sizeof;

public class VertexBufferFormat {
	
	private int nextIndex;
	private int stride; // is offset until done, the final offset becomes the stride
	private List<VertexBufferElement> elements;
	
	public VertexBufferFormat() {
		nextIndex = 0;
		stride = 0;
		elements = new ArrayList<>();
	}
	
	public void pushFloat(int count) {
		elements.add(new VertexBufferElement(nextIndex++, GL_FLOAT, count, false, stride));
		stride += Sizeof.FLOAT * count;
	}
	
	public void pushInt(int count) {
		elements.add(new VertexBufferElement(nextIndex++, GL_UNSIGNED_INT, count, false, stride));
		stride += Sizeof.INT * count;
	}
	
	public void pushChar(int count) {
		elements.add(new VertexBufferElement(nextIndex++, GL_UNSIGNED_BYTE, count, true, stride));
		stride += Sizeof.CHAR * count;
	}
	
	public List<VertexBufferElement> getElements() {
		return elements;
	}

	protected void finalize() {
		for(VertexBufferElement element : elements)
			element.finalize(stride);
	}
	
	public void enable() {
		for(VertexBufferElement element : elements)
			element.enable();
	}
	
}
