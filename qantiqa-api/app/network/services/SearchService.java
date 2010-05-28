/*******************************************************************************
 * Qantiqa : Decentralized microblogging platform
 * Copyright (C) 2010 Dario (i@dario.im) 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/

package network.services;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.utils.QantiqaException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import network.Overlay;
import network.Storage;

/**
 * Search service.
 * 
 * @author Dario
 */
public class SearchService extends Service {

	public SearchService(Overlay overlay) {
		super(overlay);
	}

	/**
	 * 
	 * @param q
	 *            Query string.
	 * @return
	 * @throws QantiqaException
	 */
	public Protocol.users searchUsers(String q) throws QantiqaException {
		Storage<Set<Object>> ix = Storage.usersById.index();

		Set<Object> results = search(q, ix);

		UserService usv = new UserService(overlay);
		Protocol.users.Builder builder = Protocol.users.newBuilder();
		for (Object id : results) {
			builder.addUser(usv.get((Long) id));
		}

		return builder.build();
	}

	/**
	 * 
	 * @param q
	 *            Query string.
	 * @return
	 * @throws QantiqaException
	 */
	public Protocol.statuses searchQuarks(String q) throws QantiqaException {
		Storage<Set<Object>> ix = (Storage<Set<Object>>) Storage.quarks.index();

		Set<Object> results = search(q, ix);

		UserService usv = new UserService(overlay);
		QuarkService qsv = new QuarkService(overlay);
		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();
		for (Object id : results) {
			Long lId = (Long) id;
			Protocol.status.Builder quark = qsv.show(lId).toBuilder();
			quark.setUser(usv.get(QuarkService.getUserIdFromQuarkId(lId)));

			builder.addStatus(quark);
		}

		return builder.build();
	}

	/**
	 * 
	 * @param <F>
	 * @param <E>
	 * @param q
	 *            Query string
	 * @param ix
	 *            Index to search
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <F, E extends Collection<F>> E search(String q, Storage<E> ix) {
		E results = overlay.retrieve(ix, q);
		if (results == null) {
			results = (E) new HashSet<F>();
		}

		StringTokenizer tk = new StringTokenizer(q, " _.,;");
		if (tk.countTokens() > 1) {
			while (tk.hasMoreElements()) {
				String next = tk.nextToken().trim();
				if (!next.equals("")) {
					E value = overlay.retrieve(ix, next);
					if (value != null) {
						results.addAll(value);
					}
				}
			}
		}

		return results;
	}
}
