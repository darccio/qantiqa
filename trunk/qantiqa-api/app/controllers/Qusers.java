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

package controllers;

import static constants.Format.JSON;
import static constants.Format.XML;
import static constants.HttpMethod.GET;
import im.dario.qantiqa.common.protocol.Protocol;
import network.services.RelationshipService;
import network.services.SearchService;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

/**
 * REST API methods for users.
 * 
 * @author Dario
 */
public class Qusers extends QController {

	@Methods( { GET })
	@Formats( { XML, JSON })
	public static void show(String id) {
		Protocol.user target = getUser(id, "target");
		Protocol.user source = getUser(request.user, "source");

		RelationshipService rsv = new RelationshipService(getOverlay());
		boolean isFollower = rsv.isFollower(source, target);
		if (isFollower) {
			Protocol.user.Builder builder = target.toBuilder();

			builder.setFollowing(isFollower);
			target = builder.build();
		}

		renderProtobuf(target);
	}

	@Methods( { GET })
	@Formats( { XML, JSON })
	@RequiresAuthentication
	public static void search(String q) {
		SearchService ssv = new SearchService(getOverlay());

		try {
			renderProtobuf(ssv.searchUsers(q));
		} catch (Exception e) {
			renderError(e);
		}
	}
}
