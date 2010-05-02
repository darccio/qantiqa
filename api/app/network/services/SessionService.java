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

import im.dario.qantiqa.common.higgs.HiggsWS;
import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.Protocol.authentication_response;
import im.dario.qantiqa.common.protocol.Protocol.user;
import network.Overlay;

/**
 * Session service.
 * 
 * Allows to handle user's session around the overlay.
 * 
 * @author Dario
 */
public class SessionService extends Service {

    public SessionService(Overlay overlay) {
        super(overlay);
    }

    /**
     * Replicates (anycast) the session of the current user.
     * 
     * @param user
     * @param userAddress
     *            (captured on Higgs)
     * @param sessionId
     */
    public void replicate(Protocol.user user, String userAddress,
            String sessionId) {
        Protocol.session.Builder builder = Protocol.session.newBuilder();

        builder.setId(sessionId);
        builder.setUserId(user.getId());
        builder.setUserAddress(userAddress);

        overlay.sendToEverybody(builder);
    }

    public boolean verify(Protocol.user user, String userAddress,
            String sessionId) {
        return verify(user.getId(), userAddress, sessionId);
    }

    public boolean verify(Long userId, String userAddress, String sessionId) {
        return HiggsWS.verify_session(userId, userAddress, sessionId).getIsOk();
    }
}
