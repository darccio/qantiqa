package models;

import java.util.List;

import play.data.validation.Required;
import siena.Id;
import siena.Model;
import siena.Query;

public class Gluon extends Model {

    @Id
    @Required
    public String host;

    @Required
    public String secret;

    public Boolean active;

    public Gluon(String host, String secret) {
        this.host = host;
        // TODO MD5
        this.secret = secret;
        this.active = Boolean.TRUE;
    }

    public static Query<Gluon> all() {
        return Model.all(Gluon.class);
    }

    public static Gluon findById(String id) {
        return all().filter("host", id).get();
    }

    public static List<Gluon> active() {
        return all().filter("active", Boolean.TRUE).fetch();
    }

    public String toString() {
        return host;
    }
}
