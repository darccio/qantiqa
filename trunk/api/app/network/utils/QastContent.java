package network.utils;

import java.lang.reflect.Method;

import com.google.protobuf.Message;

import easypastry.cast.CastContent;

public class QastContent extends CastContent {

    // private final byte[] msg;

    public QastContent(String subject, Message msg) {
        super(subject);

        // this.msg = msg.toByteArray();
    }

    public static <V extends Message> V getMessage(Class<V> klass, String sMsg) {
        V msg;

        if (klass == null) {
            msg = null;
        } else {
            try {
                Method m = klass.getDeclaredMethod("parseFrom",
                        new Class<?>[] { byte[].class });
                msg = (V) m.invoke(null, sMsg.getBytes());
            } catch (Exception e) {
                msg = null;
            }
        }

        return msg;
    }
}
