package im.dario.qantiqa.common.higgs;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.XmlFormat;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import play.Play;
import play.libs.WS.HttpResponse;

import com.google.protobuf.XmlFormat.ParseException;

public class HiggsWS {

    public static Protocol.validation validate(Integer port, String secret)
            throws ParseException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("secret", play.libs.Codec.hexMD5(secret));
        params.put("port", port);

        HttpResponse rs = play.libs.WS.url(getHiggsURL() + "/validate").params(
                params).post();

        Protocol.validation.Builder builder = Protocol.validation.newBuilder();
        XmlFormat.merge(new ByteArrayInputStream(rs.getString().getBytes()),
                builder);

        return builder.build();
    }

    public static Protocol.gluons gluons() throws ParseException {
        HttpResponse rs = play.libs.WS.url(getHiggsURL() + "/gluons").get();

        Protocol.gluons.Builder builder = Protocol.gluons.newBuilder();
        XmlFormat.merge(new ByteArrayInputStream(rs.getString().getBytes()),
                builder);

        return builder.build();
    }

    private static String getHiggsURL() {
        return Play.configuration.getProperty("qantiqa.higgs.url");
    }
}
