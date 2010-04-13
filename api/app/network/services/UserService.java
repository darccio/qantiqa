/*******************************************************************************
 * Qantiqa : Decentralized microblogging platform
 * Copyright (C) 2010 Dario (i@dario.im) 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/

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
