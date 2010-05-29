import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;
import network.Overlay;
import network.Storage;
import network.services.FavoriteService;
import network.services.QuarkService;

import org.junit.Test;

import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.FunctionalTest;
import controllers.Qfavorites;
import controllers.Qstatuses;

public class FavoritesTest extends FunctionalTest {

	/**
	 * Lists user's favorites.
	 * 
	 * Tested classes/methods: {@link Qfavorites}, {@link FavoriteService},
	 * {@link QuarkService#getUserIdFromQuarkId(Long)} and {@link Overlay}
	 */
	@Test
	public void favorites() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = GET(rq, "/favorites.xml");
		assertStatus(200, rs);

		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();
		QantiqaFormat.merge(getContent(rs), builder);

		Protocol.statuses fv = builder.build();
		assertNotNull(fv);
	}

	/**
	 * Creates a favorite. It is possible it doesn't exist, so it tests both
	 * possibilities: quark exists or not.
	 * 
	 * Used ID 2000000001 is for test user. There aren't users with odd IDs due
	 * a bug in Higgs.
	 * 
	 * Tested classes/methods: {@link Qfavorites}, {@link Qstatuses},
	 * {@link FavoriteService}, {@link QuarkService}, {@link UserService},
	 * {@link Storage} and {@link Overlay}
	 */
	@Test
	public void create() {
		Request rq = QantiqaSupport.getTestRequest();
		Response rs = POST(rq, "/favorites/create/2000000001.xml");
		if (rs.status == 404) {
			rs = GET(rq, "/statuses/show/2000000001.xml");
			assertStatus(400, rs);
		} else {
			assertStatus(200, rs);

			Protocol.status.Builder builder = Protocol.status.newBuilder();
			QantiqaFormat.merge(getContent(rs), builder);

			Protocol.status fv = builder.build();
			assertEquals(fv.getId(), 2000000001);
		}
	}
}
