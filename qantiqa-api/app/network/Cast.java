package network;

import im.dario.qantiqa.common.protocol.Protocol;

/**
 * Cast handler. Used for methods that involve sending messages through the
 * overlay.
 * 
 * @author Dario
 * 
 */
public class Cast {

	// Available casts
	/**
	 * Dumb cast used to get data from our gluon.
	 */
	public static final Cast gluon = new Cast("gluon");
	/**
	 * Cast for authentication.
	 */
	public static final Cast auth = new Cast(Protocol.authentication.class);

	private String id;
	private boolean isAnycast = false;

	private Cast(String id) {
		this.id = id;
	}

	private Cast(Class<?> id) {
		this(id.getSimpleName());
	}

	public boolean isAnycast() {
		return isAnycast;
	}

	public Cast anycast(boolean value) {
		this.isAnycast = value;

		return this;
	}

	public String toString() {
		return id;
	}
}
