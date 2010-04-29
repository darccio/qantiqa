package network;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.Protocol.session;
import easypastry.util.Utilities;

public class Cast {

    public static final Cast gluon = new Cast("gluon");
    public static final Cast auth = new Cast(Protocol.authentication.class);
    public static final Cast session = new Cast(Protocol.session.class);

    private String id;

    private Cast(String id) {
        this.id = id;
    }

    private Cast(Class<?> id) {
        this(id.getSimpleName());
    }

    public String toString() {
        return id;
    }
}
