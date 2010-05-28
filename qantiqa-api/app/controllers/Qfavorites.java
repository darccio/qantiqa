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

import static constants.Format.ATOM;
import static constants.Format.JSON;
import static constants.Format.RSS;
import static constants.Format.XML;
import static constants.HttpMethod.GET;
import static constants.HttpMethod.POST;
import im.dario.qantiqa.common.protocol.Protocol;
import network.services.FavoriteService;
import network.services.QuarkService;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

/**
 * REST API methods for favorites.
 * 
 * @author Dario
 */
public class Qfavorites extends QController {

	/**
	 * From Twitter official doc {@linkplain http
	 * ://dev.twitter.com/doc/get/favorites}
	 * 
	 * Returns the 20 most recent favorite statuses for the authenticating user
	 * or user specified by the ID parameter in the requested format.
	 */
	@Methods( { GET })
	@Formats( { XML, JSON, RSS, ATOM })
	@RequiresAuthentication
	public static void index() {
		FavoriteService fsv = new FavoriteService(getOverlay());

		renderProtobuf(fsv.get(getUser(request.user, "source")));
	}

	/**
	 * From Twitter official doc {@linkplain http
	 * ://dev.twitter.com/doc/post/favorites/:id/create}
	 * 
	 * Favorites the status specified in the ID parameter as the authenticating
	 * user. Returns the favorite status when successful.
	 * 
	 * @param id
	 *            The numerical ID of the desired status.
	 */
	@Methods( { POST })
	@Formats( { XML, JSON })
	@RequiresAuthentication
	public static void create(Long id) {
		FavoriteService fsv = new FavoriteService(getOverlay());

		try {
			Protocol.status.Builder builder = fsv.create(getRequestUser(), id)
					.toBuilder();
			builder.setUser(getUser(QuarkService.getUserIdFromQuarkId(id),
					null, "source"));

			renderProtobuf(builder);
		} catch (Exception e) {
			renderError(e);
		}
	}
}
