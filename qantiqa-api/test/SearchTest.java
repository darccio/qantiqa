import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import network.services.SearchService;

import org.junit.Test;

import controllers.Qsearch;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class SearchTest extends FunctionalTest {

	/**
	 * Tested classes: {@link Qsearch} and {@link SearchService}.
	 */
	@Test
	public void search() {
		Response rs = GET("/search.xml?q=planck");

		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		assertTrue(builder.getStatusCount() > 0);
		assertStatus(200, rs);
	}
}
