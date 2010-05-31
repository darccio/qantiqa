import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import java.util.Random;

import network.Overlay;
import network.Storage;
import network.services.QuarkService;
import network.services.RelationshipService;
import network.services.SearchService;
import network.services.UserService;

import org.junit.Test;

import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.FunctionalTest;
import utils.TimeCapsule;

public class StatusesTest extends FunctionalTest {

	/**
	 * Tested classes/methods: {@link QuarkService}, {@link UserService},
	 * {@link Overlay}, {@link TimeCapsule} and {@link Storage}
	 */
	@Test
	public void update() {
		String status = "/statuses/update?status=Planck! "
				+ new Random().nextInt();

		Request rq = QantiqaSupport.getTestRequest();
		Response rs = POST(rq, status);
		assertStatus(200, rs);

		rs = POST(rq, status);
		assertStatus(403, rs);
	}

	/**
	 * Tested classes/methods: {@link QuarkService}, {@link UserService},
	 * {@link Overlay} and {@link Storage}
	 */
	@Test
	public void show() {
		Response rs = GET("/statuses/show/2000000001.xml");
		Protocol.status.Builder builder = Protocol.status.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertEquals(2000000001, builder.getId());
		assertStatus(200, rs);
	}

	/**
	 * Tested classes/methods: {@link QuarkService}, {@link UserService},
	 * {@link Overlay}, {@link TimeCapsule} and {@link Storage}
	 */
	@Test
	public void retweet() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq, "/statuses/retweet/2000000001.xml");
		Protocol.status.Builder builder = Protocol.status.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertEquals(2000000002, builder.getId());
		assertStatus(200, rs);
	}

	/**
	 * Not fully implemented.
	 */
	@Test
	public void replies() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq, "/statuses/replies.xml");

		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertStatus(200, rs);
	}

	/**
	 * Tested classes/methods: {@link QuarkService}, {@link UserService},
	 * {@link Overlay} and {@link Storage}
	 */
	@Test
	public void retweeted_by_me() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq, "/statuses/retweeted_by_me.xml");

		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertTrue(builder.getStatusCount() > 0);
		assertStatus(200, rs);
	}

	/**
	 * Tested classes/methods: {@link QuarkService}, {@link UserService},
	 * {@link Overlay}, {@link RelationshipService} and {@link Storage}
	 */
	@Test
	public void friends_timeline() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq, "/statuses/friends_timeline.xml");

		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertTrue(builder.getStatusCount() > 0);
		assertStatus(200, rs);
	}

	/**
	 * Tested classes/methods: {@link QuarkService}, {@link UserService},
	 * {@link Overlay} and {@link Storage}
	 */
	@Test
	public void user_timeline() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq, "/statuses/user_timeline.xml");

		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertTrue(builder.getStatusCount() > 0);
		assertStatus(200, rs);
	}

	/**
	 * Tested classes/methods: {@link QuarkService}, {@link UserService},
	 * {@link Overlay}, {@link RelationshipService} and {@link Storage}
	 */
	@Test
	public void home_timeline() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq, "/statuses/home_timeline.xml");

		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertTrue(builder.getStatusCount() > 0);
		assertStatus(200, rs);
	}

	/**
	 * Tested classes/methods: {@link SearchService}
	 */
	@Test
	public void mentions() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq, "/statuses/mentions.xml");

		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertStatus(200, rs);
	}
}
