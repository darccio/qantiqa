package utils;

import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.results.Result;

public class NotAcceptable extends Result {

	private static final long serialVersionUID = 574222625388495978L;

	@Override
	public void apply(Request request, Response response) {
		Response.current().status = 406;
	}

}
