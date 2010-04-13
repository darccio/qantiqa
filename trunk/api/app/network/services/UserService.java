package network.services;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.Protocol.AuthResult;
import im.dario.qantiqa.common.utils.AsyncResult;
import network.Overlay;

public class UserService extends Service {

    public UserService(Overlay overlay) {
        super(overlay);
    }

    public AsyncResult<AuthResult> authenticate(String username, String password) {
        Protocol.authentication.Builder auth = Protocol.authentication
                .newBuilder();

        auth.setUsername(username);
        auth.setPassword(password);

        AsyncResult<AuthResult> result = new AsyncResult<AuthResult>();
        overlay.sendToGluon(auth, result,
                Protocol.authentication_response.class);

        return result;
    }

    public Protocol.user get(String key) {
        return null;
    }
}
