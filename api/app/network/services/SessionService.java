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

import java.util.HashMap;

import im.dario.qantiqa.common.higgs.HiggsWS;
import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.Protocol.authentication_response;
import im.dario.qantiqa.common.protocol.Protocol.user;
import im.dario.qantiqa.common.utils.QantiqaException;
import network.Overlay;

/**
 * Session service.
 * 
 * Allows to handle received sessions on the overlay.
 * 
 * @author Dario
 */
public class SessionService extends Service {

    private final HashMap<Long, Protocol.session> sessions = new HashMap<Long, Protocol.session>();

    public SessionService(Overlay overlay) {
        super(overlay);
    }

    public boolean verify(Protocol.session expected) {
        Long userId = expected.getUserId();
        String userAddress = expected.getUserAddress();
        String sessionId = expected.getId();

        Protocol.session session = sessions.get(userId);
        boolean isValid = session.equals(expected);

        if (!isValid) {
            try {
                // TODO validate on bootstrap gluon
                isValid = HiggsWS
                        .verify_session(userId, userAddress, sessionId)
                        .getIsOk();
            } catch (QantiqaException e) {
                e.printStackTrace();
                isValid = false;
            }
        }

        if (isValid) {
            sessions.put(expected.getUserId(), expected);
        }

        return isValid;
    }

    /**
     * Helper method to reduce clutter from repeating code for building
     * sessions.
     * 
     * @param user
     * @param userAddress
     * @param sessionId
     * @return
     */
    public static Protocol.session buildSession(Protocol.user user,
            String userAddress, String sessionId) {
        return Protocol.session.newBuilder().setId(sessionId).setUserId(
                user.getId()).setUserAddress(userAddress).build();
    }
}
