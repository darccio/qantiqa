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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	/**
	 * Creates a following relationship (source follows target).
	 * 
	 * @param source
	 *            Follower
	 * @param target
	 *            User followed by source
	 * @return
	 * @throws DHTException
	 * @throws QantiqaException
	 */
	public Map<Storage<Set<Long>>, Set<Long>> follow(Protocol.user source,
			Protocol.user target) throws DHTException, QantiqaException {
		Map<Storage<Set<Long>>, Set<Long>> data = new HashMap<Storage<Set<Long>>, Set<Long>>();
		data.put(Storage.followers, addTo(Storage.followers, source, target));
		data.put(Storage.following, addTo(Storage.following, target, source));

		return data;
	}

	/**
	 * Deletes a following relationship.
	 * 
	 * @param source
	 *            Ex-follower (quitter)
	 * @param target
	 *            User unfollowed by source
	 * @return
	 * @throws DHTException
	 * @throws QantiqaException
	 */
	public Map<Storage<Set<Long>>, Set<Long>> unfollow(Protocol.user source,
			Protocol.user target) throws DHTException, QantiqaException {
		Map<Storage<Set<Long>>, Set<Long>> data = new HashMap<Storage<Set<Long>>, Set<Long>>();
		data.put(Storage.followers, removeFrom(Storage.followers, source,
				target));
		data.put(Storage.following, removeFrom(Storage.following, target,
				source));

		return data;
	}

	/**
	 * Generic method to add somebody in one of both storage for followers
	 * (followers and following storages).
	 * 
	 * @param followers
	 *            Storage
	 * @param source
	 *            User that updates the relationship
	 * @param target
	 *            User aimed by relationship
	 * @return
	 * @throws DHTException
	 * @throws QantiqaException
	 */
	private Set<Long> addTo(Storage<Set<Long>> followers, Protocol.user source,
			Protocol.user target) throws DHTException, QantiqaException {
		Set<Long> data = getData(followers, target);
		data.add(source.getId());

		overlay.store(followers, target.getId(), data);

		return data;
	}

	/**
	 * Generic method to remove somebody in one of both storage for followers
	 * (followers and following storages).
	 * 
	 * @param followers
	 *            Storage
	 * @param source
	 *            User that updates the relationship
	 * @param target
	 *            User aimed by relationship
	 * @return
	 * @throws QantiqaException
	 * @throws DHTException
	 */
	private Set<Long> removeFrom(Storage<Set<Long>> followers,
			Protocol.user source, Protocol.user target)
			throws QantiqaException, DHTException {
		Set<Long> data = getData(followers, target);
		data.remove(source.getId());

		overlay.store(followers, target.getId(), data);

		return data;
	}

	/**
	 * Retrieves data from one of two storages (followers and following) by
	 * target user.
	 * 
	 * @param storage
	 * @param target
	 *            User used as storage key
	 * @return
	 * @throws QantiqaException
	 */
	private Set<Long> getData(Storage<Set<Long>> storage, Protocol.user target)
			throws QantiqaException {
		Set<Long> data = null;
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

	/**
	 * Returns followers of given user.
	 * 
	 * @param user
	 * @return
	 */
	public Set<Long> followers(Protocol.user user) {
		Set<Long> followers = overlay.retrieve(Storage.followers, user.getId());

		if (followers == null) {
			followers = new HashSet<Long>();
		}

		return followers;
	}

	/**
	 * Returns users followed by user.
	 * 
	 * @param user
	 * @return
	 */
	public Set<Long> following(Protocol.user user) {
		Set<Long> following = overlay.retrieve(Storage.following, user.getId());

		if (following == null) {
			following = new HashSet<Long>();
		}

		return following;
	}

	/**
	 * Check if a source user follows target user.
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public boolean isFollower(Protocol.user source, Protocol.user target) {
		Set<Long> followers = this.followers(target);

		return followers.contains(source.getId());
	}
}
