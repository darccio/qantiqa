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

import java.util.HashMap;
import java.util.Vector;

import network.Overlay;
import network.Storage;
import easypastry.dht.DHTException;

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

    public HashMap<Storage, Vector<Long>> follow(Protocol.user source,
            Protocol.user target) throws DHTException, QantiqaException {
        HashMap<Storage, Vector<Long>> data = new HashMap<Storage, Vector<Long>>();
        data.put(Storage.followers, addTo(Storage.followers, source, target));
        data.put(Storage.following, addTo(Storage.following, target, source));

        return data;
    }

    public HashMap<Storage, Vector<Long>> unfollow(Protocol.user source,
            Protocol.user target) throws DHTException, QantiqaException {
        HashMap<Storage, Vector<Long>> data = new HashMap<Storage, Vector<Long>>();
        data.put(Storage.followers, removeFrom(Storage.followers, source,
                target));
        data.put(Storage.following, removeFrom(Storage.following, target,
                source));

        return data;
    }

    private Vector<Long> addTo(Storage storage, Protocol.user source,
            Protocol.user target) throws DHTException, QantiqaException {
        Vector<Long> data = getData(storage, target);
        data.add(source.getId());

        overlay.store(storage, target.getId(), data);

        return data;
    }

    private Vector<Long> removeFrom(Storage storage, Protocol.user source,
            Protocol.user target) throws QantiqaException, DHTException {
        Vector<Long> data = getData(storage, target);
        data.remove(source.getId());

        overlay.store(storage, target.getId(), data);

        return data;
    }

    private Vector<Long> getData(Storage storage, Protocol.user target)
            throws QantiqaException {
        Vector<Long> data = null;
        if (storage == Storage.followers) {
            data = followers(target);
        }

        if (storage == Storage.following) {
            data = following(target);
        }

        if (data == null) {
            throw new QantiqaException("Invalid storage");
        }

        return data;
    }

    public Vector<Long> followers(Protocol.user user) {
        Vector<Long> followers = overlay.retrieve(Storage.followers, user
                .getId());

        if (followers == null) {
            followers = new Vector();
        }

        return followers;
    }

    public Vector<Long> following(Protocol.user user) {
        Vector<Long> following = overlay.retrieve(Storage.following, user
                .getId());

        if (following == null) {
            following = new Vector();
        }

        return following;
    }

    public boolean isFollower(Protocol.user source, Protocol.user target) {
        Vector<Long> followers = this.followers(target);

        return followers.contains(source.getId());
    }
}
