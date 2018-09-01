package gl;

import static org.lwjgl.opengl.GL20.*;

public class VertexBufferElement {
	
	private int index;
	private int type;
	private int count;
	private boolean normalized;
	private int stride;
	private int offset;
	
	public VertexBufferElement(int index, int type, int count, boolean normalized, int offset) {
		this.index = index;
		this.type = type;
		this.count = count;
		this.normalized = normalized;
		this.offset = offset;
	}
	
	/**
	 * Finalize this VertexBufferElement for use.
	 * Must be called before {@link #enable()}
	 */
	public void finalize(int stride) {
		this.stride = stride;
	}
	
	public void enable() {
		glEnableVertexAttribArray(index);
		glVertexAttribPointer(index, count, type, normalized, stride, offset);
	}
	
	public int getIndex() {
		return index;
	}

	public int getType() {
		return type;
	}

	public int getCount() {
		return count;
	}

	public boolean isNormalized() {
		return normalized;
	}

	public int getStride() {
		return stride;
	}

	public int getOffset() {
		return offset;
	}
	
}
