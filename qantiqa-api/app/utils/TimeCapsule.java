package utils;

import im.dario.qantiqa.common.utils.TwitterDate;

import java.io.Serializable;

/**
 * Wrapper class to allow time-order in ordered sets like TreeSet.
 * 
 * @author Dario
 * 
 * @param <E>
 *            Class stored in our capsule.
 */
public class TimeCapsule<E extends Serializable> implements Serializable,
		Comparable<TimeCapsule<E>> {

	private static final long serialVersionUID = -4075519777552002384L;

	private final E value;
	private final TwitterDate date;

	public TimeCapsule(E value) {
		this.value = value;
		this.date = new TwitterDate();
	}

	public TwitterDate getCreationTime() {
		return this.date;
	}

	public E getValue() {
		return this.value;
	}

	public int compareTo(TimeCapsule<E> o) {
		return (-1) * date.compareTo(o.date);
	}
}
