import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class UsersTest extends FunctionalTest {

	@Test
	public void search() {
		Response rs = GET("/users/search.xml?q=test");

		Protocol.users.Builder builder = Protocol.users.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertTrue(builder.getUserCount() > 0);
		assertStatus(200, rs);
	}

	@Test
	public void show() {
		Response rs = GET("/users/show/test.xml");

		Protocol.user.Builder builder = Protocol.user.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertEquals("test", builder.getScreenName());
		assertStatus(200, rs);
	}
}
