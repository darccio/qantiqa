package network;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import utils.TimeCapsule;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import easypastry.util.Utilities;

/**
 * Storage handler. Used for methods that store date in the overlay.
 * 
 * @author Dario
 * 
 * @param <E>
 *            Class of stored data
 */
public class Storage<E> {
	/**
	 * Callback used to index stored data.
	 * 
	 * @author Dario
	 * 
	 */
	private static abstract class StorageCallback {
		private Set<String> stems = new HashSet<String>();

		protected abstract void stem(Object o);

		protected void add(String value) {
			stems.add(value);
			StringTokenizer tk = new StringTokenizer(value, " _.,;");
			while (tk.hasMoreElements()) {
				String next = tk.nextToken().trim();
				if (!next.equals("")) {
					stems.add(next);
				}
			}
		}

		public Set<String> yield(Object o) {
			stem(o);
			return stems;
		}
	}

	// Available storages
	/*
	 * How storage works?
	 * 
	 * Each storage stores data of its generic type (<E>), identified by
	 * whatever the programmer wants to use.
	 */
	/**
	 * User storage.
	 */
	public static final Storage<Protocol.user> users = new Storage<Protocol.user>(
			"users", Protocol.user.newBuilder()).caching(true);
	/**
	 * User storage, by ID and indexed by ID.
	 */
	public static final Storage<Protocol.user> usersById = new Storage<Protocol.user>(
			"usersById", Protocol.user.newBuilder()).indexed(Long.class,
			new StorageCallback() {

				@Override
				protected void stem(Object o) {
					Protocol.user user = (Protocol.user) o;

					add(user.getScreenName());
					add(user.getName());
					add(user.getLocation());
					add(user.getDescription());
				}
			}).caching(true);
	/**
	 * Followers storage. Identified by user ID.
	 */
	public static final Storage<Set<Long>> followers = new Storage<Set<Long>>(
			"followers").caching(true);
	/**
	 * "Users followed by each user" storage. Identified by user ID.
	 */
	public static final Storage<Set<Long>> following = new Storage<Set<Long>>(
			"following").caching(true);
	/**
	 * Quark storage, indexed by ID.
	 */
	public static final Storage<Protocol.status> quarks = new Storage<Protocol.status>(
			"quarks", Protocol.status.newBuilder()).indexed(Long.class,
			new StorageCallback() {

				@Override
				protected void stem(Object o) {
					Protocol.status quark = (Protocol.status) o;

					add(quark.getText());
					add(quark.getSource());
					add(quark.getInReplyToScreenName());
				}
			}).caching(true);
	/**
	 * Requark/retweet storage. For each quark requarked the stored set contains
	 * all the quark IDs that are requarks from the identifier.
	 */
	public static final Storage<Set<Long>> requarks = new Storage<Set<Long>>(
			"requarks");
	/**
	 * Favorite storage. Each stored set is identified by its owner user ID.
	 */
	public static final Storage<Set<Long>> favorites = new Storage<Set<Long>>(
			"favorites");
	/**
	 * "Requarks by user" storage. Identified by user ID.
	 */
	public static final Storage<Set<Long>> requarksByUser = new Storage<Set<Long>>(
			"requarksByUser");
	/**
	 * "Time-ordered quarks" storage. Identified by user ID.
	 * 
	 * It uses TimeCapsule class, which give the ordering algorithm for our set.
	 */
	public static final Storage<Set<TimeCapsule<Long>>> recentQuarks = new Storage<Set<TimeCapsule<Long>>>(
			"recentQuarks").limit(200L);

	private final String id;
	private final String hash;
	private Message.Builder protobufBuilder = null;
	private Storage<Set<Object>> index;
	private StorageCallback callback;
	private Long limit = Long.MIN_VALUE;
	private final HashMap<Object, TimeCapsule<E>> cache = new HashMap<Object, TimeCapsule<E>>();
	private boolean caching = false;

	/**
	 * 
	 * @param id
	 *            Free text storage ID.
	 */
	private Storage(String id) {
		this.id = id;
		this.hash = Utilities.generateStringHash("p2p://" + id);
	}

	/**
	 * 
	 * @param id
	 *            Free text storage ID.
	 * @param builder
	 *            Protobuf builder for data stored.
	 */
	private Storage(String id, Message.Builder builder) {
		this(id);
		this.protobufBuilder = builder;
	}

	/**
	 * Serializes given value for storage.
	 * 
	 * @param value
	 * @return Value serialized.
	 */
	protected Serializable marshal(E value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Message) {
			return QantiqaFormat.printToString((Message) value);
		} else {
			if (!(value instanceof Serializable)) {
				throw new IllegalArgumentException(
						"Argument must be Serializable or com.google.protobuf.Message");
			}

			return (Serializable) value;
		}
	}

	/**
	 * Deserializes given value.
	 * 
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected E unmarshal(Serializable value) {
		if (protobufBuilder != null) {
			E msg = null;
			if (value != null) {
				Builder clean = protobufBuilder.clone();

				QantiqaFormat.merge((String) value, protobufBuilder);
				msg = (E) protobufBuilder.build();

				protobufBuilder = clean.clone();
			}

			return msg;
		} else {
			return (E) value;
		}
	}

	/**
	 * Sets an storage as indexed, based on given callback.
	 * 
	 * @param <P>
	 * @param klass
	 *            Class used to index.
	 * @param callback
	 *            Callback called every time some value is stored.
	 * @return Itself.
	 */
	private <P> Storage<E> indexed(Class<P> klass, StorageCallback callback) {
		Storage<Set<Object>> ix = new Storage<Set<Object>>(id + "_ix");
		ix.callback(callback);

		this.index = ix;

		return this;
	}

	/**
	 * 
	 * @return Index (storage) used.
	 */
	public Storage<Set<Object>> index() {
		return this.index;
	}

	/**
	 * 
	 * @param callback
	 *            Callback used to index.
	 */
	public void callback(StorageCallback callback) {
		this.callback = callback;
	}

	/**
	 * Extracts info for index based on the callback provided in
	 * {@link Storage#callback}.
	 * 
	 * @param o
	 * @return
	 */
	public Set<String> stem(Object o) {
		if (this.callback == null) {
			return new HashSet<String>();
		}

		return this.callback.yield(o);
	}

	/**
	 * Sets limit of values to store (only for stored collections).
	 * 
	 * @param limit
	 * @return
	 */
	private Storage<E> limit(Long limit) {
		this.limit = limit;

		return this;
	}

	public boolean hasLimit() {
		return (this.limit > 0);
	}

	public Long limit() {
		return this.limit;
	}

	/**
	 * 
	 * @return Hash calculated with EasyPastry tools based on given storage ID.
	 */
	public String hash() {
		return this.hash;
	}

	protected Storage<E> caching(boolean caching) {
		this.caching = caching;

		return this;
	}

	private boolean isCaching() {
		return this.caching;
	}

	public void cacheSet(Object key, E value) {
		if (isCaching()) {
			this.cache.put(key, new TimeCapsule<E>(value));
		}
	}

	public void cacheRemove(Object key) {
		if (isCaching()) {
			this.cache.remove(key);
		}
	}

	public E cacheGet(Object key, int timeout) {
		E value = null;

		if (isCaching()) {
			TimeCapsule<E> tc = this.cache.get(key);
			if (tc != null) {
				if (tc.getCreationTime().hasExpired(timeout)) {
					cacheRemove(key);
					tc = null;
				} else {
					value = tc.getValue();
				}
			}
		}

		return value;
	}

	public String toString() {
		return id;
	}
}
