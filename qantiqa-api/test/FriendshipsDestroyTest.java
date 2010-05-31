import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import network.services.RelationshipService;

import org.junit.Test;

import controllers.Qfriendships;

import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class FriendshipsDestroyTest extends FunctionalTest {

	/**
	 * Separated due random execution order of {@link FriendshipsTest}.
	 * 
	 * Tested classes: {@link Qfriendships} and {@link RelationshipService}
	 */
	@Test
	public void destroy() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = POST(rq, "/friendships/destroy/test2.xml");

		Protocol.user.Builder builder = Protocol.user.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertEquals("test2", builder.getScreenName());
		assertStatus(200, rs);
	}
}
