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

package im.dario.qantiqa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import com.sun.akuma.Daemon;

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

        final URL warUrl = this.getClass().getClassLoader().getResource("war");
        httpSrv.addHandler(new WebAppContext(warUrl.toExternalForm(), "/"));

        httpSrv.setStopAtShutdown(true);
    }

    private void start() throws Exception {
        httpSrv.start();

        try {
            ServerSocket ss = new ServerSocket(11575);

            for (;;) {
                Socket s = ss.accept();

                InputStream is = s.getInputStream();
                OutputStream os = s.getOutputStream();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                PrintStream ps = new PrintStream(os);

                String buffer;
                while ((buffer = br.readLine()) != null) {
                    ps.println(buffer);
                }

                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String... args) throws Exception {

        if (args.length > 0) {
            if (args[0].equals("-d")) {
                Daemon d = new Daemon();
                if (d.isDaemonized()) {
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
        }

        Qantiqa q = new Qantiqa();
        q.start();
    }
}
