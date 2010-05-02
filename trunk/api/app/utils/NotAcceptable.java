package utils;

import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.results.Result;

public class NotAcceptable extends Result {

    @Override
    public void apply(Request request, Response response) {
        response.current().status = 406;
    }

}
