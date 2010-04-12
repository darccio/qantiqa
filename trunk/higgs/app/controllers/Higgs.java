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
        GAE.login("Higgs.index");
    }

    public static void logout() {
        GAE.logout("Higgs.index");
    }

    public static void gluons() {
        List<Gluon> gluons = Gluon.active();

        int i = 0;
        Protocol.gluons.Builder builder = Protocol.gluons.newBuilder();
        for (Gluon g : gluons) {
            builder.setGluon(i++, g.host);
        }

        renderXml(XmlFormat.printToString(builder.build()));
    }

    public static void validate() {
        String host = request.remoteAddress;
        Gluon g = Gluon.findByHost(host);
        if (g == null) {
            forbidden();
        } else {
            g.active = true;
            g.update();
        }
    }
}