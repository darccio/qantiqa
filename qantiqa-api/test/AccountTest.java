import network.Cast;
import network.Overlay;
import network.services.SessionService;
import network.services.UserService;
import network.utils.QastContent;

import org.junit.Test;

import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.FunctionalTest;
import controllers.Qaccount;

public class AccountTest extends FunctionalTest {

	/**
	 * Tests if API returns an expected 401 HTTP status if our credentials are
	 * wrong.
	 * 
	 * Tested classes/methods: {@link Qaccount}, {@link UserService},
	 * {@link AsyncResult}, {@link Overlay#sendToGluon}, {@link Cast},
	 * {@link QastContent}, {@link QastListener} and {@link SessionService}.
	 */
	@Test
	public void verify_credentials_error() {
		Request rq = QantiqaSupport.getTestRequest();
		rq.password = "wrong_password";

		Response rs = GET(rq, "/account/verify_credentials.xml");
		assertStatus(401, rs);
	}

	/**
	 * Tests if API returns an expected 200 HTTP status if our credentials are
	 * rigth.
	 * 
	 * Tested classes/methods: {@link Qaccount}, {@link UserService},
	 * {@link AsyncResult}, {@link Overlay#sendToGluon} and
	 * {@link SessionService}.
	 */
	@Test
	public void verify_credentials() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq, "/account/verify_credentials.xml");
		assertStatus(200, rs);
	}

	/**
	 * This allows to check if profile_image feature is working, although it is
	 * not testable here because it doesn't get the image back but it works in
	 * browser/twitter client.
	 */
	@Test
	public void profile_image() {
		Response rs = GET("/account/profile_image/test");
		assertTrue(rs.contentType.equals("image/png"));
	}
}
