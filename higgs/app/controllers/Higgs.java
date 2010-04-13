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
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import java.util.List;

import models.Gluon;
import play.data.validation.Required;
import play.modules.gae.GAE;
import play.mvc.Controller;

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
     * Helper method to render our Protobuf builder objects as Qantiqa
     * interoperable format.
     * 
     * @param builder
     */
    static void renderProtobuf(Builder builder) {
        renderXml(QantiqaFormat.printToString(builder.build()));
    }
}
