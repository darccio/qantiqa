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

package im.dario.qantiqa.common.higgs;

import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.protocol.format.QantiqaFormat;

import java.util.HashMap;
import java.util.Map;

import play.Play;
import play.libs.WS.HttpResponse;

import com.google.protobuf.Message.Builder;

/**
 * Higgs webservice client.
 * 
 * Allows to interact with Higgs public API for gluons. It depends on Play
 * because our webapps depend too.
 * 
 * @author Dario
 */
public class HiggsWS {

    /**
     * Validates a gluon against our official list.
     * 
     * @see {@link HiggsWS#gluons()}
     * 
     * @param port
     *            Port published by the gluon.
     * @param secret
     *            Secret used to validate the gluon against the list.
     * @return Validation result message
     */
    public static Protocol.validation validate(Integer port, String secret) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("secret", play.libs.Codec.hexMD5(secret));
        params.put("port", port);

        HttpResponse rs = play.libs.WS.url(getHiggsURL() + "/validate").params(
                params).post();

        return getMessageFromXML(rs, Protocol.validation.newBuilder()).build();
    }

    /**
     * Get the official gluon (supernode) list from Higgs.
     * 
     * @return Gluon list message
     */
    public static Protocol.gluons gluons() {
        HttpResponse rs = play.libs.WS.url(getHiggsURL() + "/gluons").get();

        return getMessageFromXML(rs, Protocol.gluons.newBuilder()).build();
    }

    /**
     * Authenticate given user credentials auth.
     * 
     * @param auth
     * @return Authentication response (AuthResult)
     */
    public static Protocol.authentication_response authenticate(
            Protocol.authentication auth) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("password", auth.getPassword());
        params.put("user", auth.getUsername());

        HttpResponse rs = play.libs.WS.url(getHiggsURL() + "/authenticate")
                .params(params).post();

        return getMessageFromXML(rs,
                Protocol.authentication_response.newBuilder()).build();
    }

    /**
     * Auxiliary method to retrieve Protobuf messages from an HTTP XML stream.
     * 
     * @param rs
     *            HTTP request response
     * @param builder
     *            Protobuf message builder
     * @return Builder after merging with XML stream.
     */
    private static <V extends Builder> V getMessageFromXML(HttpResponse rs,
            V builder) {
        QantiqaFormat.merge(rs.getStream(), builder);

        return builder;
    }

    /**
     * Auxiliary method to get the Higgs URL from Play configuration.
     * 
     * @return Higgs webservice URL
     */
    private static String getHiggsURL() {
        return Play.configuration.getProperty("qantiqa.higgs.url");
    }
}
