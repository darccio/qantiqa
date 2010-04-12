package network;

import im.dario.qantiqa.common.higgs.HiggsWS;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import rice.environment.Environment;
import rice.p2p.commonapi.NodeHandle;
import rice.pastry.PastryNode;
import rice.pastry.socket.SocketNodeHandle;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import utils.Reference;

import com.google.protobuf.XmlFormat.ParseException;

import easypastry.cast.CastContent;
import easypastry.cast.CastHandler;
import easypastry.cast.CastListener;
import easypastry.core.PastryConnection;
import easypastry.core.PastryKernel;
import easypastry.sample.AppCastContent;

public class Overlay {

    private final PastryConnection conn;
    private final NodeHandle gluon;

    public static Overlay init(String configPath) {
        if (configPath == null) {
            throw new RuntimeException("Invalid config path");
        }

        Overlay ov = null;
        String easyPastryConfigPath = configPath + "/easypastry-config.xml";
        try {
            for (String gluon : HiggsWS.gluons().getGluonList()) {
                String[] data = gluon.split(":");
                try {
                    modifyConfig(easyPastryConfigPath, data[0], data[1]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                try {
                    ov = new Overlay(easyPastryConfigPath, data[0], data[1]);
                } catch (IOException e) {
                    ov = null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if (ov != null) {
                    break;
                }
            }

            if (ov == null) {
                throw new RuntimeException("Unable to join Qantiqa overlay");
            }
        } catch (ParseException e) {
            throw new RuntimeException("Exception while getting gluon list", e);
        }

        return ov;
    }

    private Overlay(String configPath, final String host, String sPort)
            throws IOException, Exception {
        try {
            PastryKernel.init(configPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.conn = PastryKernel.getPastryConnection();
        CastHandler cast = PastryKernel.getCastHandler();

        final Integer port = Integer.valueOf(sPort);
        final Reference<NodeHandle> gluon = new Reference<NodeHandle>();
        cast.addDeliverListener("gluons", new CastListener() {

            @Override
            public void hostUpdate(rice.p2p.commonapi.NodeHandle nh,
                    boolean joined) {
                if (joined) {
                    if (nh instanceof SocketNodeHandle) {
                        SocketNodeHandle snh = (SocketNodeHandle) nh;
                        InetSocketAddress address = snh.getInetSocketAddress();
                        if (address.getHostName().equals(host)) {
                            if (address.getPort() == port) {
                                // This is our gluon.
                                gluon.set(nh);
                                gluon.notifyAll();
                            }
                        }
                    }
                }
            }

            @Override
            public void contentDelivery(CastContent content) {
            }

            @Override
            public boolean contentAnycasting(CastContent content) {
                return true;
            }
        });

        gluon.wait();
        this.gluon = gluon.get();

        this.conn.bootNode();
    }

    private static void modifyConfig(String easyPastryConfigPath, String host,
            String port) throws FileNotFoundException, IOException,
            InvalidPropertiesFormatException {
        FileInputStream fis = new FileInputStream(easyPastryConfigPath);
        Properties prop = new Properties();
        prop.loadFromXML(fis);
        fis.close();

        prop.setProperty("host", host);
        prop.setProperty("port", port);

        FileOutputStream fos = new FileOutputStream(easyPastryConfigPath);
        prop.storeToXML(fos, null);
        fos.close();
    }

    public void sendToGluon() {
        CastHandler cast = PastryKernel.getCastHandler();
        PastryNode node = (PastryNode) conn.getNode();
        Environment env = node.getEnvironment();
        try {
            SocketPastryNodeFactory factory = new SocketPastryNodeFactory(
                    new RandomNodeIdFactory(env), 5009, env);
            NodeHandle nh = factory.getNodeHandle(new InetSocketAddress(
                    "192.168.0.12", 5009));
            cast.sendDirect(nh, new AppCastContent("test", "test1"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
