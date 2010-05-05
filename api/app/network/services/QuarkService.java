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
import im.dario.qantiqa.common.utils.QantiqaException;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import easypastry.dht.DHTException;

import network.Overlay;
import network.Storage;

/**
 * Search service.
 * 
 * @author Dario
 */
public class QuarkService extends Service {

    public QuarkService(Overlay overlay) {
        super(overlay);
    }

    public Protocol.status update(Protocol.user user, String status,
            Long in_reply_to_status_id, String source) throws QantiqaException,
            DHTException {
        Protocol.status.Builder builder = Protocol.status.newBuilder();

        builder.setText(status);
        if (in_reply_to_status_id != null) {
            builder.setInReplyToStatusId(in_reply_to_status_id);
        }

        if (source != null) {
            builder.setSource(source);
        }

        Long id = getNextId(user);
        builder.setId(id);

        Protocol.status quark = builder.build();
        overlay.store(Storage.quarks, id, quark);

        UserService usv = new UserService(overlay);
        Protocol.user.Builder ub = user.toBuilder();
        ub.setStatusesCount(ub.getStatusesCount() + 1);
        user = ub.build();
        usv.set(user);

        builder = quark.toBuilder();
        builder.setUser(user);

        return builder.build();
    }

    /**
     * 9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 7 (Long.MAX_VALUE)
     * 
     * 9 2 2 3 3 7 2 0 3 5|9 9 9 9 9 9 9 9 9
     * 
     * First ID: 1 0 0 0 0 0 0 0 0 1
     * 
     * @param user
     * @return
     */
    private static Long getNextId(Protocol.user user) {
        return (user.getId() * 1000000000L) + user.getStatusesCount() + 1L;
    }
}
