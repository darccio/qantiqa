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

import siena.Id;
import siena.Index;
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
public class User extends Model {

	@Id
	public Long id;

	@Index("ix_user")
	@NotNull
	public String username;

	@NotNull
	public String password;

	@NotNull
	public Boolean active;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * 
	 * @return All users.
	 */
	public static Query<User> all() {
		return Model.all(User.class);
	}

	/**
	 * 
	 * @param username
	 *            User name.
	 * @return Matching user with provided name.
	 */
	public static User findByName(String username) {
		return all().filter("username", username).get();
	}

	public String toString() {
		return username;
	}
}
