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
import im.dario.qantiqa.common.protocol.Protocol.status;
import im.dario.qantiqa.common.utils.QantiqaException;
import network.Overlay;
import network.Storage;
import easypastry.dht.DHTException;

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
        if (status.length() > 140) {
            throw new QantiqaException("Status is over 140 characters.")
                    .status(403);
        }

        // TODO Add creation date
        Long id = getNextId(user);
        if (user.getStatusesCount() > 0) {
            Protocol.status current = get(id - 1);
            if (current.getText().equals(status)) {
                throw new QantiqaException("Status is a duplicate.")
                        .status(403);
            }
        }

        Protocol.status.Builder builder = Protocol.status.newBuilder();

        builder.setId(id);
        builder.setText(status);
        if (in_reply_to_status_id != null) {
            builder.setInReplyToStatusId(in_reply_to_status_id);
        }

        if (source != null) {
            builder.setSource(source);
        }

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
    private Long getNextId(Protocol.user user) {
        return (user.getId() * 1000000000L) + user.getStatusesCount() + 1L;
    }

    public static Long getUserIdFromQuarkId(Long id) {
        return id / 1000000000L;
    }

    public Protocol.status destroy(Long id) throws QantiqaException {
        return overlay.remove(Storage.quarks, id);
    }

    public Protocol.status show(Long id) throws QantiqaException {
        return get(id);
    }

    private Protocol.status get(Long id) throws QantiqaException {
        Protocol.status status = overlay.retrieve(Storage.quarks, id);
        if (status == null) {
            throw new QantiqaException("No status found with that ID.")
                    .status(404);
        }

        return status;
    }

    public Protocol.status requark(Protocol.user requarker,
            Protocol.status requarked, String source) throws QantiqaException,
            DHTException {

        return update(requarker, requarked.getText(), null, source);
    }
}
