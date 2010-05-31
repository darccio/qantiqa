import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import java.io.ByteArrayInputStream;
import java.util.Map;

import network.services.RelationshipService;

import org.junit.Test;

import controllers.Qfriendships;

import play.data.parsing.JsonParser;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class FriendshipsTest extends FunctionalTest {

	/**
	 * Tested classes: {@link Qfriendships} and {@link RelationshipService}
	 */
	@Test
	public void create() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = POST(rq, "/friendships/create/test2.xml");

		Protocol.user.Builder builder = Protocol.user.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertEquals("test2", builder.getScreenName());
		assertStatus(200, rs);
	}

	/**
	 * Tested classes: {@link Qfriendships} and {@link RelationshipService}
	 */
	@Test
	public void show() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq,
				"/friendships/show.json?source_screen_name=test&target_screen_name=test2");

		Map<String, String[]> json = new JsonParser()
				.parse(new ByteArrayInputStream(getContent(rs).getBytes()));

		assertEquals("test", json.get("source.screen_name")[0]);
		assertEquals("test2", json.get("target.screen_name")[0]);
		assertStatus(200, rs);
	}
}
