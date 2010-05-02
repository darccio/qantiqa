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

import java.util.HashMap;
import java.util.Vector;

import network.Storage;
import network.services.RelationshipService;
import network.services.UserService;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

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

        Protocol.user target = getUser(id, "target");
        Protocol.user source = getUser(null, request.user, "source");

        RelationshipService rsv = new RelationshipService(getOverlay());
        try {
            HashMap<Storage, Vector<Long>> data = rsv.follow(source, target);
            Vector<Long> followers = data.get(Storage.followers);

            Protocol.user.Builder builder = Protocol.user.newBuilder(target);
            builder.setFollowersCount(followers.size());

            Protocol.user.Builder definitive = builder.clone();
            target = builder.build();

            usv.set(target);

            definitive.setFollowing(true);
            target = definitive.build();

            Vector<Long> following = data.get(Storage.following);
            builder = Protocol.user.newBuilder(source);
            builder.setFriendsCount(following.size());

            usv.set(builder.build());
        } catch (Exception e) {
            renderError(e);
        }

        renderProtobuf(target);
    }

    @Methods( { POST, DELETE })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void destroy(String id) {
        UserService usv = new UserService(getOverlay());

        Protocol.user target = getUser(id, "target");
        Protocol.user source = getUser(null, request.user, "source");

        RelationshipService rsv = new RelationshipService(getOverlay());
        try {
            HashMap<Storage, Vector<Long>> data = rsv.unfollow(source, target);
            Vector<Long> followers = data.get(Storage.followers);

            Protocol.user.Builder builder = Protocol.user.newBuilder(target);
            builder.setFollowersCount(followers.size());
            target = builder.build();

            usv.set(target);

            Vector<Long> following = data.get(Storage.following);
            builder = Protocol.user.newBuilder(source);
            builder.setFriendsCount(following.size());

            usv.set(builder.build());
        } catch (Exception e) {
            renderError(e);
        }

        renderProtobuf(target);
    }

    @Methods( { GET })
    @Formats( { XML, JSON })
    public static void show(Long source_id, String source_screen_name,
            Long target_id, String target_screen_name) {
        if (request.user != null) {
            source_screen_name = request.user;
        }

        Protocol.user source = getUser(source_id, source_screen_name, "source");
        Protocol.user target = getUser(target_id, target_screen_name, "target");

        Protocol.relationship.Builder builder = Protocol.relationship
                .newBuilder();
        builder
                .setTarget(initRelationshipMember(builder, target, source,
                        false));
        builder
                .setSource(initRelationshipMember(builder, source, target, true));

        renderProtobuf(builder);
    }

    private static Protocol.relationship_member.Builder initRelationshipMember(
            Protocol.relationship.Builder relationship, Protocol.user member,
            Protocol.user other, boolean isSource) {
        Protocol.relationship_member.Builder builder = Protocol.relationship_member
                .newBuilder();
        builder.setScreenName(member.getScreenName());
        builder.setId(member.getId());

        RelationshipService rsv = new RelationshipService(getOverlay());
        Vector<Long> followersMember = rsv.followers(member);
        Vector<Long> followersOther = rsv.followers(other);

        if (isSource) {
            builder.setNotificationsEnabled(false);
            // TODO Future spam service
            builder.setBlocking(false);
        }

        boolean followedBy = false;
        if (followersMember.contains(other.getId())) {
            followedBy = true;
        }
        builder.setFollowedBy(followedBy);

        boolean following = false;
        if (followersOther.contains(member.getId())) {
            following = true;
        }
        builder.setFollowing(following);

        return builder;
    }
}
