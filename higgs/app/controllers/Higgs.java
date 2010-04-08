package controllers;

import im.dario.qantiqa.common.protocol.Protocol;

import java.util.List;

import com.google.protobuf.XmlFormat;

import models.Gluon;
import play.modules.gae.GAE;
import play.mvc.Controller;

public class Higgs extends Controller {

    public static void index() {
        if (GAE.isLoggedIn() && GAE.isAdmin()) {
            Gluons.index();
        }

        render();
    }

    public static void login() {
        GAE.login("Higgins.index");
    }

    public static void logout() {
        GAE.logout("Higgins.index");
    }

    public static void gluons() {
        List<Gluon> gluons = Gluon.active();

        Protocol.gluons.Builder builder = Protocol.gluons.newBuilder();
        for (Gluon g : gluons) {
            builder.setGluon(0, g.host);
        }

        renderXml(XmlFormat.printToString(builder.build()));
    }

    public static void validate() {
        String host = request.remoteAddress;
        Gluon g = Gluon.findById(host);
        if (g == null) {
            forbidden();
        } else {
            g.active = true;
            g.update();
        }
    }
}