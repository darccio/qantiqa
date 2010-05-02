package network;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.Protocol.session.Builder;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import java.io.Serializable;
import java.util.Vector;

import com.google.protobuf.Message;

import easypastry.util.Utilities;

public class Storage<E> {

    public static final Storage<Protocol.user> users = new Storage("users",
            Protocol.user.newBuilder());
    public static final Storage<Protocol.user> usersById = new Storage(
            "usersById", Protocol.user.newBuilder());
    public static final Storage<Vector<Long>> followers = new Storage(
            "followers");

    private final String id;
    private final String hash;
    private Message.Builder protobufBuilder;

    private Storage(String id) {
        this.id = id;
        this.hash = Utilities.generateStringHash("p2p://" + id);
    }

    private Storage(String id, Message.Builder builder) {
        this(id);
        this.protobufBuilder = builder;
    }

    protected Serializable marshal(E value) {
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
                QantiqaFormat.merge((String) value, protobufBuilder);
                msg = (E) protobufBuilder.build();
            }

            return msg;
        } else {
            return (E) value;
        }
    }

    public String getHash() {
        return this.hash;
    }

    public String toString() {
        return id;
    }
}
