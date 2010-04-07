package controllers;

import java.util.List;

import models.Gluon;
import play.mvc.Controller;

public class Higgins extends Controller {

    public static void index() {
        render();
    }

    public static void gluons() {
        List<Gluon> gluons = Gluon.active();

        StringBuilder sb = new StringBuilder("<gluons>");
        for (Gluon g : gluons) {
            sb.append("<gluon>");
            sb.append(g.host);
            sb.append("</gluon>");
        }
        sb.append("</gluons>");

        renderXml(sb.toString());
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