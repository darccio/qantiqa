package network;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import java.io.Serializable;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import easypastry.util.Utilities;

public class Storage<E> {
    private static abstract class StorageCallback {
        private HashSet<String> stems = new HashSet<String>();

        protected abstract void stem(Object o);

        protected void add(String value) {
            stems.add(value);
            StringTokenizer tk = new StringTokenizer(value, " _.,;");
            while (tk.hasMoreElements()) {
                String next = tk.nextToken().trim();
                if (!next.equals("")) {
                    stems.add(tk.nextToken());
                }
            }
        }

        public HashSet<String> yield(Object o) {
            stem(o);
            return stems;
        }
    }

    public static final Storage<Protocol.user> users = new Storage("users",
            Protocol.user.newBuilder());
    public static final Storage<Protocol.user> usersById = new Storage(
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
    public static final Storage<Vector<Long>> followers = new Storage(
            "followers");
    public static final Storage<Vector<Long>> following = new Storage(
            "following");
    public static final Storage<Protocol.status> quarks = new Storage("quarks",
            Protocol.status.newBuilder()).indexed(Long.class,
            new StorageCallback() {

                @Override
                protected void stem(Object o) {
                    Protocol.status quark = (Protocol.status) o;

                    add(quark.getText());
                    add(quark.getSource());
                    add(quark.getInReplyToScreenName());
                }
            });

    private final String id;
    private final String hash;
    private Message.Builder protobufBuilder = null;
    private Object index;
    private StorageCallback callback;

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
        Storage<HashSet<P>> ix = new Storage<HashSet<P>>(id + "_ix");
        ix.setCallback(callback);

        this.index = ix;

        return this;
    }

    public Object getIndex() {
        return this.index;
    }

    public void setCallback(StorageCallback callback) {
        this.callback = callback;
    }

    public HashSet<String> stem(Object o) {
        if (this.callback == null) {
            return new HashSet<String>();
        }

        return this.callback.yield(o);
    }

    public String getHash() {
        return this.hash;
    }

    public String toString() {
        return id;
    }
}
