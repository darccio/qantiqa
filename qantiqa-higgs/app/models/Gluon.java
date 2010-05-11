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

import java.util.List;

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
public class Gluon extends Model {

	@Id
	public Long id;

	@Index("ix_host")
	@NotNull
	public String host;

	@Index("ix_host")
	@NotNull
	public Integer port;

	@NotNull
	public String secret;

	@NotNull
	public Boolean active;

	public Gluon(String host, Integer port, String secret) {
		this.host = host;
		this.port = port;
		this.secret = secret;
		this.active = Boolean.TRUE;
	}

	/**
	 * 
	 * @return All gluons..
	 */
	public static Query<Gluon> all() {
		return Model.all(Gluon.class);
	}

	/**
	 * 
	 * @param id
	 *            Gluon's id
	 * @return Matching gluon with provided id.
	 */
	public static Gluon findById(Long id) {
		return all().filter("id", id).get();
	}

	/**
	 * 
	 * @param host
	 *            Gluon's WAN IP
	 * @param port
	 *            Gluon's published port
	 * @return Matching gluon.
	 */
	public static Gluon findByEndpoint(String host, Integer port) {
		return all().filter("host", host).filter("port", port).get();
	}

	/**
	 * 
	 * @return Only active gluons.
	 */
	public static List<Gluon> active() {
		return all().filter("active", Boolean.TRUE).fetch();
	}

	public String toString() {
		return host + ":" + port;
	}
}
