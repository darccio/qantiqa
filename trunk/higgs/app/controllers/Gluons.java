package controllers;

import java.util.List;

import models.Gluon;
import play.data.validation.Required;
import play.modules.gae.GAE;
import play.mvc.Before;
import play.mvc.Controller;

public class Gluons extends Controller {

    @Before
    static void checkConnected() {
        if (GAE.getUser() == null) {
            Higgs.login();
        } else {
            if (!GAE.isAdmin()) {
                Higgs.login();
            }

            renderArgs.put("user", GAE.getUser().getEmail());
        }
    }

    static void checkGluon(String host) {
        Gluon g = Gluon.findById(host);
        if (g != null) {
            flash.error("Gluon already created");
            index();
        }
    }

    public static void index() {
        List<Gluon> gluons = Gluon.all().fetch();
        render(gluons);
    }

    public static void blank() {
        render();
    }

    public static void create(@Required String host, @Required String secret) {
        if (validation.hasErrors()) {
            flash.error("Gluon host/secret missing");
            blank();
        }

        checkGluon(host);

        flash.success("Gluon successfully created");
        new Gluon(host, secret).insert();
        index();
    }

    public static void enable(@Required Gluon g) {
        checkGluon(g.host);

        g.active = Boolean.TRUE;
        g.update();
    }

    public static void disable(@Required Gluon g) {
        checkGluon(g.host);

        g.active = Boolean.FALSE;
        g.update();
    }
}
