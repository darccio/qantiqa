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
import im.dario.qantiqa.common.protocol.Protocol.user;
import im.dario.qantiqa.common.protocol.Protocol.user.Builder;
import im.dario.qantiqa.common.utils.AsyncResult;
import im.dario.qantiqa.common.utils.TwitterDate;
import network.Overlay;
import network.Storage;
import easypastry.dht.DHTException;

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
	public AsyncResult<Protocol.authentication_response> authenticate(
			String username, String password) {
		Protocol.authentication.Builder auth = Protocol.authentication
				.newBuilder();

		auth.setUsername(username);
		auth.setPassword(password);

		AsyncResult<Protocol.authentication_response> result = new AsyncResult<Protocol.authentication_response>();
		overlay.sendToGluon(auth, result,
				Protocol.authentication_response.class);

		return result;
	}

	/**
	 * Queries an user and creates it if it doesn't exist, with Id from Higgs.
	 * 
	 * @param username
	 * @param id
	 * @return
	 * @throws DHTException
	 */
	public user getAndInit(String username, long id) throws DHTException {
		Protocol.user user = get(username);

		if (user == null) {
			Protocol.user.Builder builder = Protocol.user.newBuilder();
			initUser(builder, username, id);

			user = builder.build();
			set(user);
		}

		return user;
	}

	/**
	 * Queries an user.
	 * 
	 * @param username
	 * @return
	 */
	public Protocol.user get(String username) {
		return overlay.retrieve(Storage.users, username);
	}

	/**
	 * Queries an user by numeric id.
	 * 
	 * @param id
	 * @return
	 */
	public Protocol.user get(Long id) {
		return overlay.retrieve(Storage.usersById, id);
	}

	/**
	 * Initialize a new user.
	 * 
	 * @param builder
	 * @param username
	 *            Screen name.
	 * @throws DHTException
	 */
	private void initUser(Builder builder, String username, long id) {
		builder.setId(id);
		builder.setScreenName(username);
		// TODO Improve
		builder
				.setProfileImageUrl("http://127.0.0.1:9000/account/profile_image/"
						+ username);
		builder.setProtected(false);
		builder.setFollowersCount(0);
		builder.setFriendsCount(0);
		builder.setCreatedAt(new TwitterDate().toString());
		builder.setFavouritesCount(0);
		builder.setNotifications(false);
		builder.setGeoEnabled(false);
		builder.setVerified(false);
		builder.setFollowing(false);
		builder.setStatusesCount(0);
	}

	/**
	 * Stores an user.
	 * 
	 * @param username
	 * @return
	 * @throws DHTException
	 */
	public void set(Protocol.user user) throws DHTException {
		overlay.store(Storage.users, user.getScreenName(), user);
		overlay.store(Storage.usersById, user.getId(), user);
	}
}
