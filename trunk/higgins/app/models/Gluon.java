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

    static Query<Gluon> all() {
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
