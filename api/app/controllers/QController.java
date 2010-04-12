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

package controllers;

import im.dario.qantiqa.common.protocol.Protocol.hash;
import im.dario.qantiqa.common.protocol.format.XmlFormat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import network.Overlay;
import network.requests.ProxyRequest;
import network.requests.QantiqaRequest;

import org.apache.commons.httpclient.Header;

import play.Play;
import play.cache.Cache;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;
import utils.NotAcceptable;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

import com.google.protobuf.JsonFormat;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import constants.Format;
import constants.HttpMethod;
import edu.emory.mathcs.backport.java.util.Arrays;

/* TODO Pending headers
 * 
 < ETag: "ab28e246529faf2077c973fceef6e7b8"
 < Last-Modified: Thu, 25 Mar 2010 23:24:19 GMT
 < Cache-Control: no-cache, no-store, must-revalidate, pre-check=0, post-check=0
 < Server: hi
 < Content-Type: application/xml; charset=utf-8
 < Cache-Control: no-cache, max-age=1800
 */
public abstract class QController extends Controller {

    @Before
    static void checkRequest() {
        Method m = request.invokedMethod;

        checkOverlay();
        checkFormat(m);
        checkAuthentication(m);
        checkMethod(m);
    }

    static Overlay getOverlay() {
        return (Overlay) Play.configuration.get("qantiqa._overlay");
    }

    private synchronized static void checkOverlay() {
        if (getProxyTo().toLowerCase().equals("qantiqa")) {
            Overlay ov = getOverlay();
            if (ov == null) {
                ov = Overlay.init(Play.conf.getRealFile().getParent());
                Play.configuration.put("qantiqa._overlay", ov);
            }
        }
    }

    @After
    static void cleanup() {
        Cache.delete(getFormatKey());
    }

    private static String getFormatKey() {
        return session.getId() + "-format";
    }

    /**
     * Send a 406 Not Acceptable response
     */
    protected static void notAcceptable() {
        throw new NotAcceptable();
    }

    private static void checkFormat(Method m) {
        Formats ann = m.getAnnotation(Formats.class);

        Format format = null;
        if (ann == null) {
            format = Format.RAW;
        } else {
            try {
                String sFormat = request.path.substring(request.path
                        .lastIndexOf(".") + 1);
                format = Format.valueOf(sFormat.toUpperCase());
            } catch (IllegalArgumentException e) {
                notAcceptable();
            }

            int pos = Arrays.binarySearch(ann.value(), format);
            if (pos < 0) {
                notAcceptable();
            }

            Cache.set(getFormatKey(), format);
        }
    }

    private static void checkMethod(Method m) {
        Methods ann = m.getAnnotation(Methods.class);

        HttpMethod method = null;
        if (ann != null) {
            try {
                method = HttpMethod.valueOf(request.method);
            } catch (IllegalArgumentException e) {
                notAcceptable();
            }

            int pos = Arrays.binarySearch(ann.value(), method);
            if (pos < 0) {
                notAcceptable();
            }
        }
    }

    private static void checkAuthentication(Method m) {
        Annotation ann = m.getAnnotation(RequiresAuthentication.class);

        if (ann != null) {
            if (request.user == null || request.password == null) {
                // TODO Send patch to protobuf-java-format to override root tag.
                hash h = hash.newBuilder().setError(
                        "Could not authenticate you.").setRequest(request.path)
                        .build();

                String content = null;
                switch (Cache.get(getFormatKey(), Format.class)) {
                case XML:
                    content = XmlFormat.printToString(h);
                    break;
                case JSON:
                    content = JsonFormat.printToString(h);
                    break;
                }

                response.print(content);
                unauthorized("Qantiqa API");
            }
        }
    }

    protected static void proxy() {
        Format format = Cache.get(getFormatKey(), Format.class);

        String proxyTo = getProxyTo();

        ProxyRequest pr;
        try {
            Class<?> prClass = Class.forName("network.requests." + proxyTo
                    + "Request");
            pr = (ProxyRequest) prClass.newInstance();
        } catch (Exception e) {
            // TODO Mimic Twitter error response
            throw new IllegalStateException(
                    "qantiqa.proxyTo not properly configured");
        }

        if (proxyTo.toLowerCase().equals("qantiqa")) {
            QantiqaRequest qr = (QantiqaRequest) pr;
            qr.setOverlay(getOverlay());
        }

        pr.proxy(request);

        response.current().status = pr.getStatus();
        response.current().contentType = pr.getContentType();

        Header[] headers = pr.getHeaders();
        for (Header header : headers) {
            String name = header.getName();

            if (!name.equals("Content-Length") && !name.equals("Content-Type")) {
                response.current().setHeader(header.getName(),
                        header.getValue());
            }
        }

        switch (format) {
        case ATOM:
        case RSS:
        case XML:
            renderXml(pr.getXml());
            break;
        case JSON:
            renderText(pr.getJson());
            break;
        case RAW:
            renderBinary(pr.getStream());
            break;
        }
    }

    private static String getProxyTo() {
        String proxyTo = Play.configuration.getProperty("qantiqa.proxyTo");

        if (proxyTo == null) {
            // TODO Mimic Twitter error response
            throw new IllegalStateException("qantiqa.proxyTo not configured");
        }

        return proxyTo;
    }

    protected static void renderProtobuf(Builder builder) {
        Message msg = builder.build();

        renderProtobuf(msg);
    }

    protected static void renderProtobuf(Message msg) {
        Format format = Cache.get(getFormatKey(), Format.class);

        switch (format) {
        case ATOM:
        case RSS:
        case XML:
            renderXml(XmlFormat.printToString(msg));
            break;
        case JSON:
            renderText(JsonFormat.printToString(msg));
            break;
        }
    }
}
