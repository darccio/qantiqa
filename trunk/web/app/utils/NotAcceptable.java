package utils;

import play.exceptions.UnexpectedException;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.results.Result;

/**
 * 406 Not Acceptable
 */
public class NotAcceptable extends Result {

    public NotAcceptable() {
        super();
    }

    @Override
    public void apply(Request request, Response response) {
        response.status = 406;
    }

}
