package controllers;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.XmlFormat;

import java.util.List;

import models.Gluon;
import play.data.validation.Required;
import play.modules.gae.GAE;
import play.mvc.Controller;

import com.google.protobuf.AbstractMessage.Builder;

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

    public static void gluons() {
        List<Gluon> gluons = Gluon.active();

        Protocol.gluons.Builder builder = Protocol.gluons.newBuilder();
        for (Gluon g : gluons) {
            builder.addGluon(g.toString());
        }

        renderProtobuf(builder);
    }

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

    static void renderProtobuf(Builder builder) {
        renderXml(XmlFormat.printToString(builder.build()));
    }
}
