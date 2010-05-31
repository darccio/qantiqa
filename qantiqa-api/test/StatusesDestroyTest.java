import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class StatusesDestroyTest extends FunctionalTest {

	@Test
	public void destroy() {
		Response rs = GET("/statuses/destroy/2000000001.xml");
		Protocol.status.Builder builder = Protocol.status.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertEquals(2000000001, builder.getId());
		assertStatus(200, rs);
	}
}
