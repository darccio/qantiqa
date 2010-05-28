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

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.Protocol.session;
import im.dario.qantiqa.common.protocol.format.JsonFormat;
import im.dario.qantiqa.common.protocol.format.XmlFormat;
import im.dario.qantiqa.common.utils.QantiqaException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import network.Overlay;
import network.services.QuarkService;
import network.services.SessionService;
import network.services.UserService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import play.Play;
import play.cache.Cache;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Response;
import utils.NotAcceptable;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import constants.Format;
import constants.HttpMethod;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Main class for all the REST API controllers.
 * 
 * @author Dario
 */
public abstract class QController extends Controller {

	private static final Logger log = Logger.getLogger(QController.class);

	/**
	 * Sessions started from this node.
	 */
	private static Map<String, Protocol.session> sessions = new HashMap<String, session>();

	/**
	 * Aspect that checks everything before processing the request by any API
	 * method.
	 */
	@Before
	static void checkRequest() {
		Method m = request.invokedMethod;

		log.warn(request.path);

		checkRequestedFormat(m);
		checkOverlay();
		checkIfAuthenticationIsRequired(m);
		checkRequestHttpMethod(m);
	}

	/**
	 * Clean cache up aspect.
	 */
	@After
	static void cleanup() {
		Cache.delete(getFormatKey());
	}

	/**
	 * Obtains the overlay object from global configuration hash.
	 * 
	 * Yes, this is a dirty hack ;) but it is not serializable so I need to hold
	 * it somewhere and I don't feel sure having it as an static field of this
	 * class (yet).
	 * 
	 * @return
	 */
	protected static Overlay getOverlay() {
		return (Overlay) Play.configuration.get("qantiqa._overlay");
	}

	/**
	 * Checks if the overlay is active. If not, it creates it and store it.
	 */
	private synchronized static void checkOverlay() {
		Overlay ov = getOverlay();
		if (ov == null) {
			try {
				ov = Overlay.init(Play.conf.getRealFile().getParent());

				Play.configuration.put("qantiqa._overlay", ov);
				ov.boot();
			} catch (QantiqaException e) {
				renderError(e);
			}
		}
	}

	/**
	 * Builds cache key for format data stored in it.
	 * 
	 * @return
	 */
	protected static String getFormatKey() {
		return session.getId() + "-format";
	}

	/**
	 * Send an error response in the format of the request (request-level
	 * available).
	 * 
	 * @param status
	 *            HTTP status code
	 * @param error
	 *            Message
	 */
	protected static void renderError(Integer status, String error) {
		Protocol.hash.Builder bh = Protocol.hash.newBuilder();
		bh.setRequest(request.path);
		bh.setError(error);

		Response.current().status = status;
		renderProtobuf(bh);
	}

	/**
	 * Send an error response in the format requested, from an exception and
	 * assuming HTTP status code 500.
	 * 
	 * If given exception is a {@link QantiqaException}, it takes the status
	 * from it.
	 * 
	 * @param e
	 */
	protected static void renderError(Exception e) {
		int status = 500;
		if (e instanceof QantiqaException) {
			status = ((QantiqaException) e).getStatus();
		}

		log.error("ERR", e);
		renderError(status, e.getMessage());
	}

	/**
	 * Send a 400 Bad request response
	 */
	protected static void badRequest(String why) {
		renderError(400, why);
	}

	/**
	 * Send a 401 Unauthorized response
	 */
	protected static void unauthorized() {
		response.setHeader("WWW-Authenticate", "Basic realm=\"Qantiqa API\"");
		renderError(401, "Could not authenticate you.");
	}

	/**
	 * Send a 403 Unauthorized response
	 */
	protected static void forbidden(String why) {
		renderError(401, why);
	}

	/**
	 * Send a 404 Not found response
	 */
	protected static void notFound() {
		notFound("Not found");
	}

	protected static void notFound(String why) {
		renderError(404, why);
	}

	/**
	 * Send a 406 Not acceptable response
	 */
	protected static void notAcceptable() {
		throw new NotAcceptable();
	}

	/**
	 * Check all the valid formats supported by the requested API method.
	 * 
	 * @param m
	 *            Reflected method obtained from Play.
	 */
	private static void checkRequestedFormat(Method m) {
		Formats ann = m.getAnnotation(Formats.class);

		Format format = null;
		if (ann == null) {
			// Assumes raw format if no one configured in class.
			format = Format.RAW;
		} else {
			try {
				// All URLs end in .format
				String sFormat = request.path.substring(request.path
						.lastIndexOf(".") + 1);
				format = Format.valueOf(sFormat.toUpperCase(Locale.US));
			} catch (IllegalArgumentException e) {
				notAcceptable();
			}

			int pos = Arrays.binarySearch(ann.value(), format);
			if (pos < 0) {
				notAcceptable();
			}
		}

		Cache.set(getFormatKey(), format);
	}

	/**
	 * Check all the valid HTTP methods supported by the requested API method.
	 * 
	 * @param m
	 *            Reflected method obtained from Play.
	 */
	private static void checkRequestHttpMethod(Method m) {
		Methods ann = m.getAnnotation(Methods.class);

		HttpMethod method = null;
		if (ann != null) {
			try {
				method = HttpMethod.valueOf(request.method);
			} catch (IllegalArgumentException e) {
				badRequest("Unknown HTTP method.");
			}

			int pos = Arrays.binarySearch(ann.value(), method);
			if (pos < 0) {
				badRequest("This method requires a "
						+ StringUtils.join(ann.value(), " or ") + ".");
			}
		}
	}

	/**
	 * Check if authentication is required by the requested API method.
	 * 
	 * It renders the errors in an independent way because it needs to throw an
	 * unauthorized response (Play works with Exceptions as renderers).
	 * 
	 * @param m
	 *            Reflected method obtained from Play.
	 */
	private static void checkIfAuthenticationIsRequired(Method m) {
		Annotation ann = m.getAnnotation(RequiresAuthentication.class);

		if (ann != null) {
			if (request.user == null || request.password == null) {
				// We don't have all the required authentication info.
				unauthorized();
			} else {
				// Only if we are not verifying our credentials...
				if (!request.actionMethod.equals("verify_credentials")) {
					switch (authenticate().getResult()) {
					case NOT_VALID:
						unauthorized();
						break;
					case ERROR:
						renderError(500, "Could not authenticate you.");
					}
				}
			}
		}
	}

	/**
	 * Helper method to render our Protobuf builders.
	 * 
	 * It is not the used in Higgs because the different treatment of output
	 * format (requested by the REST API caller).
	 * 
	 * @param builder
	 */
	protected static void renderProtobuf(Builder builder) {
		Message msg = builder.build();
		renderProtobuf(msg);
	}

	/**
	 * Helper method to render Protobuf message according the request format.
	 * 
	 * @param msg
	 */
	protected static void renderProtobuf(Message msg) {
		Format format = Cache.get(getFormatKey(), Format.class);

		String out = null;
		switch (format) {
		case ATOM:
		case RSS:
		case XML:
			// We currently don't support ATOM and RSS but they are XML formats,
			// so we return XML.
			out = XmlFormat.printToString(msg);
			log.warn(out);
			renderXml(out);
			break;
		case JSON:
			if (msg instanceof Protocol.statuses
					|| msg instanceof Protocol.direct_messages
					|| msg instanceof Protocol.errors
					|| msg instanceof Protocol.users) {
				out = JsonFormat.printCollectionToString(msg);
			} else {
				out = JsonFormat.printToString(msg);
			}
			log.warn(out);
			renderText(out);
			break;
		}

	}

	/**
	 * Common method to get users from unknown data.
	 * 
	 * Possible issue with users with numeric nicknames, e.g. 101010.
	 * 
	 * @param unknown
	 * @param kind
	 * @return
	 */
	protected static Protocol.user getUser(String unknown, String kind) {
		Long nId;
		try {
			nId = Long.valueOf(unknown);
		} catch (NumberFormatException e) {
			nId = null;
		}

		Protocol.user user = null;
		if (nId == null) {
			user = getUser(null, unknown, kind);
		} else {
			user = getUser(nId, null, kind);
		}

		return user;
	}

	/**
	 * Enhanced version to handle different possible parameters.
	 * 
	 * @param id
	 * @param screen_name
	 * @param kind
	 * @return
	 */
	protected static Protocol.user getUser(Long id, String screen_name,
			String kind) {
		if (id == null && screen_name == null) {
			forbidden("Could not determine " + kind + " user.");
		}

		Protocol.user user;
		UserService usv = new UserService(getOverlay());

		if (screen_name == null) {
			user = usv.get(id);
		} else {
			user = usv.get(screen_name);
		}

		if (user == null) {
			notFound("Could not find " + kind + " user");
		}

		return user;
	}

	/**
	 * Return the user associated to the authenticated request.
	 * 
	 * @return
	 */
	protected static Protocol.user getRequestUser() {
		return getUser(null, request.user, "source");
	}

	/**
	 * Basic session support. Not used currently.
	 * 
	 * @param auth
	 * @param user
	 */
	protected static void startSession(Protocol.authentication_response auth,
			Protocol.user user) {
		sessions.put(user.getScreenName(), SessionService.buildSession(user,
				auth.getUserIp(), auth.getSessionId()));
	}

	protected static Protocol.session currentSession() {
		return sessions.get(request.user);
	}

	/**
	 * Authenticate HTTP credentials against Higgs.
	 * 
	 * @return
	 */
	protected static Protocol.authentication_response authenticate() {
		UserService usv = new UserService(getOverlay());

		String md5Passwd = play.libs.Codec.hexMD5(request.password);
		Protocol.authentication_response auth = usv.authenticate(request.user,
				md5Passwd).get();

		return auth;
	}

	/**
	 * Send an error response (only for retweets).
	 * 
	 * @param e
	 */
	protected static void renderRetweetError(Exception e) {
		int status = 500;
		if (e instanceof QantiqaException) {
			status = ((QantiqaException) e).getStatus();
		}

		log.error("ERR", e);
		renderRetweetError(status, e.getMessage());
	}

	/**
	 * Send an error response (only for retweets).
	 * 
	 * @param status
	 * @param message
	 */
	private static void renderRetweetError(int status, String message) {
		Protocol.errors.Builder bh = Protocol.errors.newBuilder();
		bh.addError(message);

		Response.current().status = status;
		renderProtobuf(bh);
	}

	/**
	 * Add user data to an status.
	 * 
	 * It gets the user from quark id (see {@link QuarkService#getNextId}).
	 * 
	 * @param msg
	 * @param quarkId
	 * @return
	 */
	protected static Protocol.status appendUser(Protocol.status msg,
			Long quarkId) {
		Protocol.status.Builder builder = msg.toBuilder();
		builder.setUser(getUser(QuarkService.getUserIdFromQuarkId(quarkId),
				null, "source"));

		return builder.build();
	}
}
