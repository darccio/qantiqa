package im.dario.qantiqa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

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

    private void start() {
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

    public static void main(String... args) {

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