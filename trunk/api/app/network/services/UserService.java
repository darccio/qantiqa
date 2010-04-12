package network.services;

import im.dario.qantiqa.common.protocol.Protocol;
import network.Overlay;

public class UserService extends Service {

    public static enum AuthResult {
        VALID, NOT_VALID
    }

    public UserService(Overlay overlay) {
        super(overlay);
    }

    public AuthResult authenticate(String username, String password) {
        overlay.sendToGluon();

        return AuthResult.NOT_VALID;
    }

    public Protocol.user get(String key) {
        return null;
    }
}
