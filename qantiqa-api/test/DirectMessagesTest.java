import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import org.junit.Test;

import controllers.Qdirect_messages;

import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class DirectMessagesTest extends FunctionalTest {

	/**
	 * Although not implemented, it tests the mock API for direct messages.
	 * 
	 * Tested classses: {@link Qdirect_messages}
	 */
	@Test
	public void direct_messages() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq, "/direct_messages.xml");

		Protocol.direct_messages.Builder builder = Protocol.direct_messages
				.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		Protocol.direct_messages dms = builder.build();
		assertEquals(dms.getDirectMessageCount(), 0);
		assertStatus(200, rs);
	}

	/**
	 * Although not implemented, it tests the mock API for direct messages.
	 * 
	 * Tested classses: {@link Qdirect_messages}
	 */
	@Test
	public void sent_direct_messages() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq, "/direct_messages/sent.xml");

		Protocol.direct_messages.Builder builder = Protocol.direct_messages
				.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		Protocol.direct_messages dms = builder.build();
		assertEquals(dms.getDirectMessageCount(), 0);
		assertStatus(200, rs);
	}
}
