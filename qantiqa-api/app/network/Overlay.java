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

package network;

import im.dario.qantiqa.common.higgs.HiggsWS;
import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.Protocol.AuthResult;
import im.dario.qantiqa.common.utils.AsyncResult;
import im.dario.qantiqa.common.utils.QantiqaException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Set;
import java.util.TreeSet;

import network.utils.QastContent;
import network.utils.QastListener;

import org.apache.log4j.Logger;

import play.Play;
import play.utils.Properties;
import rice.p2p.commonapi.NodeHandle;
import rice.pastry.socket.SocketNodeHandle;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import easypastry.cast.CastContent;
import easypastry.cast.CastHandler;
import easypastry.cast.CastListener;
import easypastry.core.PastryConnection;
import easypastry.core.PastryKernel;
import easypastry.dht.DHTException;
import easypastry.dht.DHTHandler;
import easypastry.sample.AppCastContent;

/**
 * Qantiqa overlay
 * 
 * Handles all the request done by peers to other peers and gluons.
 * 
 * @author Dario
 */
public class Overlay {

	private static final Logger log = Logger.getLogger(Overlay.class);

	private final PastryConnection conn;

	private final CastHandler cast;

	/**
	 * Reference to our bootstrap gluon.
	 */
	private final AsyncResult<NodeHandle> gluon;

	public static Overlay init(String configPath) throws QantiqaException {
		if (configPath == null) {
			throw new QantiqaException("Invalid config path");
		} else {
			File configFile = new File(configPath);
			if (!configFile.exists()) {
				throw new QantiqaException("Invalid config path");
			}
		}

		Overlay ov = null;
		String easyPastryConfigPath = configPath + "/easypastry-config.xml";

		boolean isGluon = checkIfGluon();
		if (isGluon) {
			try {
				ov = new Overlay(easyPastryConfigPath, isGluon);
			} catch (Exception e) {
				throw new QantiqaException(e);
			}
		} else {
			// Get the current gluon list and try to connect.
			for (String gluon : HiggsWS.gluons().getGluonList()) {
				String[] data = gluon.split(":");
				try {
					modifyConfig(easyPastryConfigPath, data[0], data[1]);
				} catch (Exception e) {
					throw new QantiqaException(e);
				}

				try {
					ov = new Overlay(easyPastryConfigPath, data[0], data[1],
							isGluon);
				} catch (IOException e) {
					ov = null;
				} catch (Exception e) {
					throw new QantiqaException(e);
				}

				if (ov != null) {
					break;
				}
			}

			if (ov == null) {
				throw new QantiqaException("Unable to join Qantiqa overlay");
			}
		}

		return ov;
	}

	private static boolean checkIfGluon() throws QantiqaException {
		boolean isGluon = Boolean.valueOf(Play.configuration
				.getProperty("qantiqa.isGluon"));

		if (isGluon) {
			String secret = Play.configuration
			// Secret generated with "play secret"
					.getProperty("application.secret");

			Properties p = new Properties();
			try {
				p.load(new FileInputStream(Play
						.getFile("conf/bunshin.properties")));

				// Contacting with Higgs...
				Protocol.validation rs = HiggsWS.validate(Integer.valueOf(p
						.get("BUNSHIN_PORT")), secret);

				if (!rs.getIsOk()) {
					play.Logger.error(rs.getMessage());

					Play.configuration.put("qantiqa.isGluon", "false");
					isGluon = false;
				}
			} catch (IOException e) {
				log.error("We are not a gluon", e);
			}
		}
		return isGluon;
	}

	/**
	 * Initializes the Gluon functionality.
	 */
	private void initGluon() {
		// Register authentication subject/topic.
		subscribe(Cast.auth, new QastListener() {

			@Override
			public void contentDelivery(QastContent qc) {
				NodeHandle nh = qc.getSource();

				Protocol.authentication auth = qc
						.getMessage(Protocol.authentication.class);
				Protocol.authentication_response.Builder rs;
				if (auth == null) {
					rs = Protocol.authentication_response.newBuilder();
					rs.setResult(AuthResult.NOT_VALID);
				} else {
					rs = HiggsWS.authenticate(auth).toBuilder();
				}

				sendToPeer(nh, rs);
			}
		});
	}

	/**
	 * Private constructor.
	 * 
	 * @param configPath
	 *            easypastry-config.xml path
	 * @param host
	 *            Bootstrap gluon IP
	 * @param sPort
	 *            Bootstrap gluon port
	 * @param isGluon
	 * @throws IOException
	 */
	private Overlay(String configPath, final String host, String sPort,
			boolean isGluon) throws IOException {
		this(configPath, isGluon);

		/*
		 * This is a dumb listener just used to get the NodeHandler of our
		 * bootstrap gluon.
		 */
		if (!isGluon) {
			final Integer port = Integer.valueOf(sPort);
			subscribe(Cast.gluon, new QastListener() {
				@Override
				public void hostUpdate(NodeHandle nh, boolean joined) {
					if (joined && nh instanceof SocketNodeHandle) {
						SocketNodeHandle snh = (SocketNodeHandle) nh;
						InetSocketAddress address = snh.getInetSocketAddress();
						if (address.getAddress().getHostAddress().equals(host)
								&& address.getPort() == port) {
							// This is our gluon.
							gluon.set(nh);
						}
					}
				}
			});
		}
	}

	/**
	 * 
	 * @param configPath
	 *            easypastry-config.xml path
	 * @param isGluon
	 * @return
	 */
	private Overlay(String configPath, boolean isGluon) {
		try {
			if (isGluon) {
				// TODO ! Get WAN/LAN IP
				PastryKernel.init("192.168.0.12", configPath);
			} else {
				PastryKernel.init(configPath);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		this.conn = PastryKernel.getPastryConnection();
		this.cast = PastryKernel.getCastHandler();

		this.gluon = new AsyncResult<NodeHandle>();
		if (isGluon) {
			this.gluon.set(conn.getNode().getLocalNodeHandle());
			initGluon();
		}
	}

	/**
	 * Starts the node.
	 * 
	 * @throws QantiqaException
	 */
	public void boot() throws QantiqaException {
		try {
			this.conn.bootNode();
		} catch (Exception e) {
			throw new QantiqaException(e);
		}
	}

	/**
	 * Auxiliary method used to modify the easypastry-config.xml because there
	 * is no way to do this programmatically, so this is a workaround.
	 * 
	 * @param easyPastryConfigPath
	 * @param host
	 *            Bootstrap gluon IP
	 * @param port
	 *            Bootstrap gluon port
	 * @throws InvalidPropertiesFormatException
	 */
	private static void modifyConfig(String easyPastryConfigPath, String host,
			String port) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(easyPastryConfigPath);

			java.util.Properties prop = new java.util.Properties();
			prop.loadFromXML(fis);
			fis.close();

			prop.setProperty("host", host);
			prop.setProperty("port", port);

			fos = new FileOutputStream(easyPastryConfigPath);
			prop.storeToXML(fos, null);
		} finally {
			fis.close();
			fos.close();
		}
	}

	/**
	 * Subscribes to a Cast (anycast, multicast, direct and hopped message).
	 * 
	 * @param cast
	 * @param listener
	 */
	public void subscribe(Cast cast, CastListener listener) {
		String castId = cast.toString();
		if (cast.isAnycast()) {
			this.cast.subscribe(castId);
		}

		subscribe(castId, listener);
	}

	private void subscribe(String castId, CastListener listener) {
		this.cast.addDeliverListener(castId, listener);
	}

	/**
	 * Auxiliary method to send a direct protobuf message to our bootstrap
	 * gluon.
	 * 
	 * This registers a handle for the corresponding answer from gluon (kind of
	 * client/server interaction over Pastry).
	 * 
	 * 
	 * @param builder
	 * @param result
	 *            Async result handler
	 * @param messageClass
	 *            Class of the message built by the builder
	 */
	public void sendToGluon(Builder builder, final AsyncResult<?> result,
			final Class<? extends Message> messageClass) {
		String subject = builder.getDescriptorForType().getName();
		sendToPeer(this.gluon.get(), builder);

		subscribe(subject + "_response", new QastListener() {
			@Override
			public void contentDelivery(QastContent qc) {
				Message msg = qc.getMessage(messageClass);
				result.set(msg);
			}
		});
	}

	/**
	 * Sends a direct protobuf message to a peer/gluon.
	 * 
	 * @param nh
	 * @param builder
	 */
	public void sendToPeer(NodeHandle nh, Builder builder) {
		CastContent cc = buildCastContent(builder);

		cast.sendDirect(nh, cc);
	}

	/**
	 * Anycasts a protobuf message.
	 * 
	 * @param builder
	 */
	public void sendToEverybody(Builder builder) {
		CastContent cc = buildCastContent(builder);

		cast.sendMulticast(cc.getSubject(), cc);
	}

	private AppCastContent buildCastContent(Builder builder) {
		Message msg = builder.build();
		String subject = msg.getDescriptorForType().getName();

		return new AppCastContent(subject, new String(msg.toByteArray()));
	}

	/**
	 * 
	 * @param storage
	 *            DHT where to store the object
	 * @param key
	 *            Identifier of the object in the DHT
	 * @param value
	 * @throws DHTException
	 */
	public <E> void store(final Storage<E> storage, final Object key,
			final E value) throws DHTException {
		final Overlay self = this;
		storage.cacheSet(key, value);

		new Thread(new Runnable() {

			public void run() {
				DHTHandler dht;
				Serializable serialized = storage.marshal(value);

				boolean insertedInOverlay = false;
				while (!insertedInOverlay) {
					try {
						dht = PastryKernel.getDHTHandler(storage.hash());
						dht.put(key.toString(), serialized);

						insertedInOverlay = true;
					} catch (Exception e) {
						log.error("ERR", e);
					}
				}

				Storage<Set<Object>> ix = (Storage<Set<Object>>) storage
						.index();
				if (ix != null) {
					for (String stem : ix.stem(value)) {
						try {
							self.add(ix, stem, key);
						} catch (DHTException e) {
							// Nothing to do...
							log.error("ERR", e);
						}
					}
				}
			}
		}).start();
	}

	/**
	 * 
	 * @param storage
	 *            DHT where to retrieve the object
	 * @param key
	 *            Identifier of the object in the DHT
	 * @return
	 * @throws DHTException
	 */
	public <E> E retrieve(Storage<E> storage, Object key) {
		E value = storage.cacheGet(key, 300);

		if (value == null) {
			try {
				DHTHandler dht = PastryKernel.getDHTHandler(storage.hash());
				value = storage.unmarshal(dht.get(key.toString()));

				storage.cacheSet(key, value);
			} catch (DHTException e) {
				log.error("ERR", e);
				value = null;
			}
		}

		return value;
	}

	/**
	 * 
	 * @param <E>
	 * @param storage
	 *            DHT where to retrieve the object
	 * @param key
	 *            Identifier of the object in the DHT
	 * @return
	 */
	public <E> E remove(Storage<E> storage, Object key) {
		E value = retrieve(storage, key);
		storage.cacheRemove(key);

		try {
			DHTHandler dht = PastryKernel.getDHTHandler(storage.hash());
			dht.remove(key.toString());
		} catch (DHTException e) {
			log.error("ERR", e);
			value = null;
		}

		return value;
	}

	/**
	 * Adds given value to a set stored in storage.
	 * 
	 * @param <E>
	 * @param storage
	 *            DHT where to retrieve the object
	 * @param key
	 *            Identifier of the object in the DHT
	 * @param value
	 * @throws DHTException
	 */
	public <E> void add(Storage<Set<E>> storage, Object key, E value)
			throws DHTException {
		Set<E> data = this.retrieve(storage, key);
		if (data == null) {
			if (storage.hasLimit()) {
				data = new TreeSet<E>();
			} else {
				data = new HashSet<E>();
			}
		}

		data.add(value);
		if (storage.hasLimit()) {
			TreeSet<E> set = (TreeSet<E>) data;
			while (set.size() >= storage.limit()) {
				set.pollFirst();
			}
		}

		this.store(storage, key, data);
	}
}
