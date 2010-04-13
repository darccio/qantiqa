package network;

import im.dario.qantiqa.common.higgs.HiggsWS;
import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.Protocol.AuthResult;
import im.dario.qantiqa.common.protocol.Protocol.authentication;
import im.dario.qantiqa.common.utils.AsyncResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import network.utils.QastContent;
import network.utils.QastListener;
import rice.p2p.commonapi.NodeHandle;
import rice.pastry.socket.SocketNodeHandle;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import easypastry.cast.CastHandler;
import easypastry.core.PastryConnection;
import easypastry.core.PastryKernel;
import easypastry.sample.AppCastContent;

public class Overlay {

    private final PastryConnection conn;
    private final AsyncResult<NodeHandle> gluon;

    public static Overlay init(String configPath) {
        if (configPath == null) {
            throw new RuntimeException("Invalid config path");
        }

        Overlay ov = null;
        String easyPastryConfigPath = configPath + "/easypastry-config.xml";

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

        return ov;
    }

    public static Overlay initGluon(String configPath) {
        Overlay overlay = new Overlay(configPath);

        final CastHandler cast = PastryKernel.getCastHandler();
        cast.addDeliverListener(Protocol.authentication.class.getSimpleName(),
                new QastListener() {

                    @Override
                    public void hostUpdate(NodeHandle nh, boolean joined) {
                        if (joined) {
                            System.out.println(nh + " joined.");
                        }
                    }

                    @Override
                    public void contentDelivery(AppCastContent qc) {
                        NodeHandle nh = qc.getSource();

                        // Message msg = qc.getMessage(type);
                        Message msg;
                        try {
                            Field field = qc.getClass().getDeclaredField("txt");
                            field.setAccessible(true);

                            msg = QastContent.getMessage(
                                    Protocol.authentication.class,
                                    (String) field.get(qc));

                            Protocol.authentication auth = (authentication) msg;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // TODO check authentication

                        Protocol.authentication_response.Builder rs = Protocol.authentication_response
                                .newBuilder();
                        rs.setResult(AuthResult.VALID);

                        System.out.println("Delivering...");
                        sendToPeer(nh, rs);
                        System.out.println("Delivered.");
                    }
                });

        System.out.println("All loaded...");

        return overlay;
    }

    private Overlay(String configPath, final String host, String sPort)
            throws IOException, Exception {
        this(configPath);

        final Integer port = Integer.valueOf(sPort);
        CastHandler cast = PastryKernel.getCastHandler();

        cast.addDeliverListener("gluon", new QastListener() {
            @Override
            public void hostUpdate(rice.p2p.commonapi.NodeHandle nh,
                    boolean joined) {
                if (joined) {
                    if (nh instanceof SocketNodeHandle) {
                        SocketNodeHandle snh = (SocketNodeHandle) nh;
                        InetSocketAddress address = snh.getInetSocketAddress();
                        if (address.getAddress().getHostAddress().equals(host)) {
                            if (address.getPort() == port) {
                                // This is our gluon.
                                gluon.set(nh);
                            }
                        }
                    }
                }
            }
        });

        this.conn.bootNode();
    }

    public Overlay(String configPath) {
        try {
            PastryKernel.init(configPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.conn = PastryKernel.getPastryConnection();
        this.gluon = new AsyncResult<NodeHandle>();
        this.gluon.set(conn.getNode().getLocalNodeHandle());
    }

    public void bootGluon() {
        try {
            this.conn.bootNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public void sendToGluon(Builder builder, final AsyncResult<?> result,
            final Class<? extends Message> messageClass) {
        Message msg = builder.build();
        String subject = msg.getDescriptorForType().getName();

        CastHandler cast = PastryKernel.getCastHandler();
        cast.sendDirect(this.gluon.get(), new AppCastContent(subject,
                new String(msg.toByteArray())));

        cast.addDeliverListener(subject + "_response", new QastListener() {

            @Override
            public void contentDelivery(AppCastContent qc) {
                try {
                    Field field = qc.getClass().getDeclaredField("txt");
                    field.setAccessible(true);

                    // Message msg = qc.getMessage(type);
                    Message msg = QastContent.getMessage(messageClass,
                            (String) field.get(qc));
                    Method m = msg.getClass().getMethod("getResult");

                    result.set(m.invoke(msg));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void sendToPeer(NodeHandle nh, Builder builder) {
        Message msg = builder.build();
        String subject = msg.getDescriptorForType().getName();

        CastHandler cast = PastryKernel.getCastHandler();
        cast.sendDirect(nh, new AppCastContent(subject, new String(msg
                .toByteArray())));
    }
}
