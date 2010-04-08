package controllers;

import im.dario.qantiqa.common.protocol.Protocol;

import java.util.List;

import com.google.protobuf.XmlFormat;

import models.Gluon;
import play.mvc.Controller;

public class Higgins extends Controller {

    public static void index() {
        render();
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