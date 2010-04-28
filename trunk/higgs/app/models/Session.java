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

package models;

import im.dario.qantiqa.common.protocol.Protocol.user;

import java.util.List;

import siena.Column;
import siena.Id;
import siena.Index;
import siena.Max;
import siena.Model;
import siena.NotNull;
import siena.Query;
import siena.Table;

/**
 * Gluon model class.
 * 
 * Used by Siena to persist our gluons data in the GAE Datastore.
 * 
 * @author Dario
 */
@Table("gluon")
public class Session extends Model {

    @Id
    public Long id;

    @Index("ix_session")
    @NotNull
    public String sessionId;

    @Index("ix_user_session")
    public Long userId;

    public String userAddress;

    public Session(Long userId, String userAddress, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.userAddress = userAddress;
    }

    /**
     * 
     * @return All sessions.
     */
    public static Query<Session> all() {
        return Model.all(Session.class);
    }

    /**
     * 
     * @param id
     *            Session id.
     * @return Matching session with provided id.
     */
    public static Session find(String id) {
        return all().filter("sessionId", id).get();
    }

    public static void expireByUser(Long userId) {
        List<Session> sessions = all().filter("userId", userId).fetch();
        for (Session s : sessions) {
            s.delete();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((sessionId == null) ? 0 : sessionId.hashCode());
        result = prime * result
                + ((userAddress == null) ? 0 : userAddress.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Session)) {
            return false;
        }
        Session other = (Session) obj;
        if (sessionId == null) {
            if (other.sessionId != null) {
                return false;
            }
        } else if (!sessionId.equals(other.sessionId)) {
            return false;
        }
        if (userAddress == null) {
            if (other.userAddress != null) {
                return false;
            }
        } else if (!userAddress.equals(other.userAddress)) {
            return false;
        }
        if (userId == null) {
            if (other.userId != null) {
                return false;
            }
        } else if (!userId.equals(other.userId)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return sessionId + ": " + userId;
    }
}
