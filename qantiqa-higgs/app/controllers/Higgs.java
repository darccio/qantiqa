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
import im.dario.qantiqa.common.protocol.Protocol.AuthResult;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import java.util.List;

import models.Gluon;
import models.User;
import play.data.validation.Required;
import play.modules.gae.GAE;
import play.mvc.Controller;
import play.mvc.Scope.Session;

import com.google.protobuf.AbstractMessage.Builder;

/**
 * Main class.
 * 
 * Publishes Gluon API for gluons and peers.
 * 
 * @author Dario
 */
public class Higgs extends Controller {

    public static void index() {
        if (GAE.isLoggedIn() && GAE.isAdmin()) {
            Gluons.index();
        }

        render();
    }

    public static void login() {
        GAE.login("Higgs.index");
    }

    public static void logout() {
        GAE.logout("Higgs.index");
    }

    /**
     * Return official gluon list.
     */
    public static void gluons() {
        List<Gluon> gluons = Gluon.active();

        Protocol.gluons.Builder builder = Protocol.gluons.newBuilder();
        for (Gluon g : gluons) {
            builder.addGluon(g.toString());
        }

        renderProtobuf(builder);
    }

    /**
     * Validate a gluon against official list.
     * 
     * @param port
     *            Gluon's published port.
     * @param secret
     *            Gluon's secret in MD5 ("play secret" command and hashed on
     *            fly).
     */
    public static void validate(@Required Integer port, @Required String secret) {
        String host = request.remoteAddress;

        Protocol.validation.Builder builder = Protocol.validation.newBuilder();
        builder.setIsOk(false);

        Gluon g = Gluon.findByEndpoint(host, port);
        if (g == null) {
            response.current.get().status = 401;
            builder.setMessage("Unauthorized");
        } else {
            if (g.secret.equals(secret)) {
                g.active = true;
                g.update();

                response.current.get().status = 200;
                builder.setIsOk(true);
            } else {
                response.current.get().status = 401;
                builder.setMessage("Unauthorized");
            }
        }

        renderProtobuf(builder);
    }

    /**
     * Authenticate given user credentials.
     * 
     * Nowadays it creates the credentials and authenticate them as valid if
     * they are missing in the datastore.
     * 
     * @param username
     *            Username.
     * @param password
     *            Password in MD5.
     */
    public static void authenticate(@Required String username,
            @Required String password) {
        boolean isValid = false;
        Protocol.authentication_response.Builder builder = Protocol.authentication_response
                .newBuilder();

        User u = User.findByName(username);
        if (u == null) {
            (u = new User(username, password)).insert();
            isValid = true;
        } else {
            if (u.password.equals(password)) {
                isValid = true;
            } else {
                response.current.get().status = 401;
                builder.setResult(AuthResult.NOT_VALID);
            }
        }

        if (isValid) {
            String session = Session.current().getId();
            response.current.get().status = 200;

            builder.setResult(AuthResult.VALID);
            builder.setUserId(u.id);
            builder.setUserIp(request.remoteAddress);
            builder.setSessionId(session);

            models.Session.expireByUser(u.id);
            new models.Session(u.id, request.remoteAddress, session).insert();
        }

        renderProtobuf(builder);
    }

    public static void verify_session(@Required Long user_id,
            @Required String user_address, @Required String session_id) {
        Protocol.validation.Builder builder = Protocol.validation.newBuilder();
        builder.setIsOk(false);

        models.Session expected = new models.Session(user_id, user_address,
                session_id);

        models.Session actual = models.Session.find(session_id);
        if (expected.equals(actual)) {
            builder.setIsOk(true);
        }

        renderProtobuf(builder);
    }

    /**
     * Helper method to render our Protobuf builder objects as Qantiqa
     * interoperable format.
     * 
     * @param builder
     */
    static void renderProtobuf(Builder builder) {
        renderXml(QantiqaFormat.printToString(builder.build()));
    }
}
