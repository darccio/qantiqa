package network;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.Protocol.session;
import easypastry.util.Utilities;

public class Cast {

    public static final Cast gluon = new Cast("gluon");
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
