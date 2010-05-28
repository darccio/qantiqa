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
import im.dario.qantiqa.common.utils.QantiqaException;

import java.util.Set;

import network.Overlay;
import network.Storage;
import easypastry.dht.DHTException;

/**
 * Favorite service.
 * 
 * @author Dario
 */
public class FavoriteService extends Service {

	public FavoriteService(Overlay overlay) {
		super(overlay);
	}

	public Protocol.status create(Protocol.user user, Long id)
			throws DHTException, QantiqaException {
		overlay.add(Storage.favorites, user.getId(), id);

		QuarkService qsv = new QuarkService(overlay);
		Protocol.status quark = qsv.show(id);

		return quark;
	}

	public Protocol.statuses get(user user) {
		QuarkService qsv = new QuarkService(overlay);

		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();
		Set<Long> favorites = overlay.retrieve(Storage.favorites, user.getId());
		if (favorites != null) {
			for (Long id : favorites) {
				try {
					builder.addStatus(qsv.show(id));
				} catch (QantiqaException e) {
					e.printStackTrace();
				}
			}
		}

		return builder.build();
	}
}
