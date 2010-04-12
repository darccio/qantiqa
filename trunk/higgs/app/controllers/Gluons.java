package controllers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    static Gluon checkIfExistsGluon(Long id) {
        Gluon g = Gluon.findById(id);
        if (g == null) {
            flash.error("Gluon with id %d not found", id);
            index();
        }

        return g;
    }

    public static void index() {
        List<Gluon> gluons = Gluon.all().fetch();
        render(gluons);
    }

    public static void blank() {
        render();
    }

    public static void create(@Required String host, @Required Integer port,
            @Required String secret) {
        if (validation.hasErrors()) {
            flash.error("Some fields are missing");
            blank();
        }

        Gluon g = Gluon.findByEndpoint(host, port);
        if (g != null) {
            flash.error("Gluon already created");
            index();
        }

        flash.success("Gluon successfully created");
        new Gluon(host, port, play.libs.Codec.hexMD5(secret)).insert();

        index();
    }

    public static void enable(Long id) {
        Gluon g = checkIfExistsGluon(id);
        g.active = Boolean.TRUE;
        g.update();

        flash.success("Gluon successfully enabled");

        index();
    }

    public static void disable(Long id) {
        Gluon g = checkIfExistsGluon(id);
        g.active = Boolean.FALSE;
        g.update();

        flash.success("Gluon successfully disabled");

        index();
    }

    public static void delete(Long id) {
        Gluon g = checkIfExistsGluon(id);
        g.delete();

        flash.success("Gluon successfully deleted");

        index();
    }
}
