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
import static constants.Format.RAW;
import static constants.Format.XML;
import static constants.HttpMethod.GET;
import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.Protocol.AuthResult;
import network.services.UserService;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

/**
 * REST API methods for accounts.
 * 
 * @author Dario
 */
public class Qaccount extends QController {

    @Methods( { GET })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void verify_credentials() {
        UserService svc = new UserService(getOverlay());

        String md5Passwd = play.libs.Codec.hexMD5(request.password);

        AuthResult result = svc.authenticate(request.user, md5Passwd).get();
        switch (result) {
        case VALID:
            Protocol.user user = svc.get(request.user);
            renderProtobuf(user);
            break;
        case NOT_VALID:
            Protocol.hash.Builder bh = Protocol.hash.newBuilder();
            bh.setRequest(request.path);
            bh.setError("Could not authenticate you.");

            response.current().status = 401;
            renderProtobuf(bh);
        }
    }

    @Methods( { GET })
    @Formats( { RAW })
    public static void profile_image(String screen_name) {
        // TODO Return an static image.
        proxyToTwitter();
    }
}
