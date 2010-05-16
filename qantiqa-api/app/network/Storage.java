package network;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import utils.TimeCapsule;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import easypastry.util.Utilities;

public class Storage<E> {
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

	public static final Storage<Protocol.user> users = new Storage<Protocol.user>(
			"users", Protocol.user.newBuilder());
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
			});
	public static final Storage<Set<Long>> followers = new Storage<Set<Long>>(
			"followers");
	public static final Storage<Set<Long>> following = new Storage<Set<Long>>(
			"following");
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
			});
	public static final Storage<Set<Long>> requarks = new Storage<Set<Long>>(
			"requarks");
	public static final Storage<Set<Long>> favorites = new Storage<Set<Long>>(
			"favorites");
	public static final Storage<Set<Long>> requarksByUser = new Storage<Set<Long>>(
			"requarksByUser");
	public static final Storage<Set<TimeCapsule<Long>>> recentQuarks = new Storage<Set<TimeCapsule<Long>>>(
			"recentQuarks").limit(200L);

	private final String id;
	private final String hash;
	private Message.Builder protobufBuilder = null;
	private Storage<Set<Object>> index;
	private StorageCallback callback;
	private Long limit = Long.MIN_VALUE;

	private Storage(String id) {
		this.id = id;
		this.hash = Utilities.generateStringHash("p2p://" + id);
	}

	private Storage(String id, Message.Builder builder) {
		this(id);
		this.protobufBuilder = builder;
	}

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

	private <P> Storage<E> indexed(Class<P> klass, StorageCallback callback) {
		Storage<Set<Object>> ix = new Storage<Set<Object>>(id + "_ix");
		ix.callback(callback);

		this.index = ix;

		return this;
	}

	public Storage<Set<Object>> index() {
		return this.index;
	}

	public void callback(StorageCallback callback) {
		this.callback = callback;
	}

	public Set<String> stem(Object o) {
		if (this.callback == null) {
			return new HashSet<String>();
		}

		return this.callback.yield(o);
	}

	private Storage<E> limit(Long value) {
		this.limit = value;

		return this;
	}

	public boolean hasLimit() {
		return (this.limit > 0);
	}

	public Long limit() {
		return this.limit;
	}

	public String hash() {
		return this.hash;
	}

	public String toString() {
		return id;
	}
}
