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

import org.apache.commons.lang.NotImplementedException;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.Protocol.AuthResult;
import im.dario.qantiqa.common.utils.AsyncResult;
import network.Overlay;

/**
 * User service.
 * 
 * Allows to authenticate user against the user database stored in gluons and
 * query them.
 * 
 * @author Dario
 */
public class UserService extends Service {

    public UserService(Overlay overlay) {
        super(overlay);
    }

    /**
     * Authenticates an user.
     * 
     * @param username
     * @param password
     *            Password in MD5
     * @return If the authentication is successful or not.
     */
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

    /**
     * Queries an user.
     * 
     * @param username
     * @return
     */
    public Protocol.user get(String username) {
        throw new NotImplementedException();
    }
}
