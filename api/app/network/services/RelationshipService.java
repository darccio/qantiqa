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

import java.util.Vector;

import easypastry.dht.DHTException;

import network.Overlay;
import network.Storage;

/**
 * Relationship service.
 * 
 * Handles relationships between users (follower/friend).
 * 
 * @author Dario
 */
public class RelationshipService extends Service {

    public RelationshipService(Overlay overlay) {
        super(overlay);
    }

    public Vector<Long> follow(Protocol.user source, Protocol.user target)
            throws DHTException {
        Vector<Long> followers = followers(target);

        if (followers == null) {
            followers = new Vector();
        }

        followers.add(source.getId());
        overlay.store(Storage.followers, target.getId(), followers);

        return followers;
    }

    public Vector<Long> unfollow(Protocol.user source, Protocol.user target)
            throws DHTException {
        Vector<Long> followers = followers(target);

        if (followers != null) {
            followers.remove(source.getId());
            overlay.store(Storage.followers, target.getId(), followers);
        }

        return followers;
    }

    public Vector<Long> followers(Protocol.user user) {
        return overlay.retrieve(Storage.followers, user.getId());
    }
}
