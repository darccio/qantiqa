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

import java.io.File;

import network.services.UserService;

import org.apache.log4j.Logger;

import play.Play;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;
import easypastry.dht.DHTException;

/**
 * REST API methods for accounts.
 * 
 * @author Dario
 */
public class Qaccount extends QController {

	private static final Logger log = Logger.getLogger(Qaccount.class);

	/**
	 * From Twitter official doc {@linkplain http 
	 * ://apiwiki.twitter.com/Twitter-
	 * REST-API-Method:-account%C2%A0verify_credentials}
	 * 
	 * Returns an HTTP 200 OK response code and a representation of the
	 * requesting user if authentication was successful; returns a 401 status
	 * code and an error message if not.
	 */
	@Methods( { GET })
	@Formats( { XML, JSON })
	@RequiresAuthentication
	public static void verify_credentials() {
		UserService usv = new UserService(getOverlay());

		boolean notValid = false;
		Protocol.authentication_response auth = authenticate();
		switch (auth.getResult()) {
		case VALID:
			Protocol.user user;
			try {
				user = usv.getAndInit(request.user, auth.getUserId());
				startSession(auth, user);

				renderProtobuf(user);
			} catch (DHTException e) {
				log.error("ERR", e);
				notValid = true;
			}
			break;
		case NOT_VALID:
			notValid = true;
			break;
		case ERROR:
			renderError(500, "Could not authenticate you.");
		}

		if (notValid) {
			unauthorized();
		}
	}

	/**
	 * Out of official Twitter REST API.
	 * 
	 * Get avatar for given user name.
	 */
	@Methods( { GET })
	public static void profile_image(String screen_name) {
		// TODO Pending of updating profile (screen_name -> dht)
		File avatar = new File(Play.applicationPath
				+ "/public/images/avatar.png");

		response.setHeader("Content-Length", String.valueOf(avatar.length()));
		response.contentType = "image/png";

		renderBinary(avatar);
	}
}
