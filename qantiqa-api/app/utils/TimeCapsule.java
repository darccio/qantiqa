package utils;

import im.dario.qantiqa.common.utils.TwitterDate;

import java.io.Serializable;

/**
 * Wrapper class to add time-order in ordered sets like TreeSet.
 * 
 * @author Dario
 * 
 * @param <E>
 *            Class stored in our capsule.
 */
public class TimeCapsule<E extends Serializable> implements Serializable,
		Comparable<TwitterDate> {

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

	@Override
	public int compareTo(TwitterDate o) {
		/*
		 * We switch the actual comparison result in order to get a
		 * newest-to-oldest order by default.
		 */
		return (-1) * date.compareTo(o);
	}
}
