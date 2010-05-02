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
import static constants.HttpMethod.DELETE;
import static constants.HttpMethod.GET;
import static constants.HttpMethod.POST;
import im.dario.qantiqa.common.protocol.Protocol;

import java.util.Vector;

import network.services.RelationshipService;
import network.services.UserService;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;
import easypastry.dht.DHTException;

/**
 * REST API methods for friendships.
 * 
 * @author Dario
 */
public class Qfriendships extends QController {

    @Methods( { POST })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void create(String id) {
        UserService usv = new UserService(getOverlay());

        Protocol.user target = usv.getFromUnknown(id);
        if (target == null) {
            notFound();
        }

        RelationshipService rsv = new RelationshipService(getOverlay());
        try {
            Vector<Long> followers = rsv.follow(usv.get(request.user), target);

            Protocol.user.Builder builder = Protocol.user.newBuilder(target);
            builder.setFollowersCount(followers.size());
            target = builder.build();

            usv.set(target);
        } catch (DHTException e) {
            // TODO Return 500 errors in any not handled exception?
            e.printStackTrace();
        }

        renderProtobuf(target);
    }

    @Methods( { POST, DELETE })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void destroy(String id) {
        proxyToTwitter();
    }

    @Methods( { GET })
    @Formats( { XML, JSON })
    public static void show(Long source_id, String source_screen_name,
            Long target_id, String target_screen_name) {
        if (request.user != null) {
            source_screen_name = request.user;
        } else {
            if (source_id == null && source_screen_name == null) {
                forbidden("Could not determine source user.");
            }
        }

        if (target_id == null && target_screen_name == null) {
            forbidden("Could not determine target user.");
        }
    }
}
