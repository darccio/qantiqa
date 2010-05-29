import play.mvc.Http.Request;
import play.test.FunctionalTest;

public class QantiqaSupport {

	public static Request getTestRequest() {
		Request rq = FunctionalTest.newRequest();
		rq.user = "test";
		rq.password = "test";

		return rq;
	}
}
