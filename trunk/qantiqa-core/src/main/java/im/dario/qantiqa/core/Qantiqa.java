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

package im.dario.qantiqa.core;

import java.io.IOException;
import java.net.URL;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import com.sun.akuma.Daemon;

/**
 * Server daemon class.
 * 
 * Handles all daemon functionality with an embedded Jetty on which deploy
 * qa-api-node war (generated with "play war api -o bin/war" command).
 * 
 * @author Dario
 */
public class Qantiqa {

	/**
	 * Path to pid file.
	 * 
	 * TODO Turn it configurable
	 * 
	 * TODO Maybe we should define some standard locations by O.S.? Even null.
	 */
	private static final String pidFile = "/tmp/qantiqa.pid";

	private final Server httpSrv;

	public Qantiqa() {
		httpSrv = new Server();
		initHttpServer();
	}

	private void initHttpServer() {
		Connector connector = new SelectChannelConnector();

		// TODO Configurable HTTP port
		connector.setPort(11576);
		connector.setHost("127.0.0.1");
		httpSrv.addConnector(connector);

		// Deploying qa-api-node war...
		final URL warUrl = this.getClass().getClassLoader().getResource("war");
		httpSrv.addHandler(new WebAppContext(warUrl.toExternalForm(), "/"));

		httpSrv.setStopAtShutdown(true);
	}

	public void start() throws Exception {
		httpSrv.start();
	}

	public static void main(String... args) throws Exception {
		if (args.length > 0) {
			Daemon d = new Daemon();
			if (args[0].equals("-d") && d.isDaemonized()) {
				try {
					// WARN Multiple runs mess up the pid file.
					d.init(pidFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					d.daemonize();
				} catch (IOException e) {
					e.printStackTrace();
				}

				System.exit(0);
			}
		}

		Qantiqa q = new Qantiqa();
		q.start();
	}
}
