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
import im.dario.qantiqa.common.utils.TwitterDate;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import network.Overlay;
import network.Storage;
import utils.TimeCapsule;
import easypastry.dht.DHTException;

/**
 * Search service.
 * 
 * @author Dario
 */
public class QuarkService extends Service {

	public QuarkService(Overlay overlay) {
		super(overlay);
	}

	public Protocol.status update(Protocol.user user, String status,
			Long in_reply_to_status_id, String source) throws QantiqaException,
			DHTException {
		if (status.length() > 140) {
			throw new QantiqaException("Status is over 140 characters.")
					.status(403);
		}

		Long id = getNextId(user);
		if (user.getStatusesCount() > 0) {
			Protocol.status current = get(id - 1);
			if (current.getText().equals(status)) {
				throw new QantiqaException("Status is a duplicate.")
						.status(403);
			}
		}

		Protocol.status.Builder builder = Protocol.status.newBuilder();

		builder.setId(id);
		builder.setText(status);
		if (in_reply_to_status_id != null) {
			builder.setInReplyToStatusId(in_reply_to_status_id);
		}

		if (source != null) {
			builder.setSource(source);
		}

		TimeCapsule<Long> hoipoi = new TimeCapsule<Long>(builder.getId());
		builder.setCreatedAt(hoipoi.getCreationTime().toString());

		Protocol.status quark = builder.build();
		overlay.store(Storage.quarks, id, quark);
		overlay.add(Storage.recentQuarks, user.getId(), hoipoi);

		UserService usv = new UserService(overlay);
		Protocol.user.Builder ub = user.toBuilder();
		ub.setStatusesCount(ub.getStatusesCount() + 1);
		user = ub.build();
		usv.set(user);

		builder = quark.toBuilder();
		builder.setUser(user);

		return builder.build();
	}

	/**
	 * 9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 7 (Long.MAX_VALUE)
	 * 
	 * 9 2 2 3 3 7 2 0 3 5|9 9 9 9 9 9 9 9 9
	 * 
	 * First ID: 1 0 0 0 0 0 0 0 0 1
	 * 
	 * @param user
	 * @return
	 */
	private Long getNextId(Protocol.user user) {
		return (user.getId() * 1000000000L) + user.getStatusesCount() + 1L;
	}

	public static Long getUserIdFromQuarkId(Long id) {
		return id / 1000000000L;
	}

	public Protocol.status destroy(Long id) throws QantiqaException,
			DHTException {
		UserService usv = new UserService(overlay);
		Protocol.user.Builder ub = usv.get(getUserIdFromQuarkId(id))
				.toBuilder();
		ub.setStatusesCount(ub.getStatusesCount() - 1);
		usv.set(ub.build());

		return overlay.remove(Storage.quarks, id);
	}

	public Protocol.status show(Long id) throws QantiqaException {
		return get(id);
	}

	private Protocol.status get(Long id) throws QantiqaException {
		Protocol.status status = overlay.retrieve(Storage.quarks, id);
		if (status == null) {
			throw new QantiqaException("No status found with that ID.")
					.status(404);
		}

		return status;
	}

	public Protocol.status requark(Protocol.user requarker, Long requarkedId,
			String source) throws QantiqaException, DHTException {
		UserService usv = new UserService(overlay);

		Protocol.user requarkee = usv.get(getUserIdFromQuarkId(requarkedId));
		if (requarker.getId() == requarkee.getId()) {
			throw new QantiqaException(
					"Share sharing is not permissable for this status (Share validations failed)")
					.status(403);
		}

		Protocol.status requarked = get(requarkedId);

		Long id = getNextId(requarker);
		if (requarker.getStatusesCount() > 0) {
			Protocol.status current = get(id - 1);
			if (current.getText().equals(requarked.getText())) {
				throw new QantiqaException(
						"Share sharing is not permissable for this status (Share validations failed)")
						.status(403);
			}
		}

		Protocol.status.Builder requark = update(requarker,
				requarked.getText(), null, source).toBuilder();

		overlay.add(Storage.requarks, requarked.getId(), requark.getId());
		overlay.add(Storage.requarksByUser, requarker.getScreenName(), requark
				.getId());

		Protocol.status.Builder builder = requark.getRetweetedStatus()
				.toBuilder();
		builder.setUser(requarkee);
		requark.setRetweetedStatus(builder);

		return requark.build();
	}

	public Protocol.statuses requarks(String user) throws QantiqaException {
		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();

		Set<Long> requarks = overlay.retrieve(Storage.requarksByUser, user);

		UserService usv = new UserService(overlay);
		for (Long id : requarks) {
			Protocol.status.Builder quark = this.show(id).toBuilder();
			quark.setUser(usv.get(QuarkService.getUserIdFromQuarkId(id)));

			builder.addStatus(quark);
		}

		return builder.build();
	}

	public Protocol.statuses timelines(Set<Long> users, Integer limit,
			Long sinceId) {
		TreeSet<TimeCapsule<Long>> full = new TreeSet<TimeCapsule<Long>>();

		for (Long user : users) {
			TreeSet<TimeCapsule<Long>> timeline = (TreeSet<TimeCapsule<Long>>) overlay
					.retrieve(Storage.recentQuarks, user);
			full.addAll(timeline);
		}

		return buildStream(full, limit, sinceId);
	}

	public Protocol.statuses timeline(Long user, Integer limit) {
		TreeSet<TimeCapsule<Long>> timeline = (TreeSet<TimeCapsule<Long>>) overlay
				.retrieve(Storage.recentQuarks, user);

		return buildStream(timeline, limit, null);
	}

	private Protocol.statuses buildStream(TreeSet<TimeCapsule<Long>> quarks,
			Integer limit, Long sinceId) {
		TwitterDate since = null;
		if (sinceId != null) {
			try {
				since = TwitterDate.parse(this.show(sinceId).getCreatedAt());
			} catch (Exception e) {
				since = null;
			}
		}

		LinkedHashSet<Long> ids = new LinkedHashSet<Long>();
		for (Integer total = 0; total < limit; total++) {
			TimeCapsule<Long> tc = quarks.pollFirst();
			if (tc == null) {
				break;
			}

			if (since == null) {
				ids.add(tc.getValue());
			} else {
				if (tc.getCreationTime().compareTo(since) > 0) {
					ids.add(tc.getValue());
				}
			}
		}

		Protocol.statuses.Builder builder = Protocol.statuses.newBuilder();
		for (Long id : ids) {
			try {
				builder.addStatus(this.show(id));
			} catch (QantiqaException e) {
				// Nothing to do, really.
			}
		}

		return builder.build();
	}
}
