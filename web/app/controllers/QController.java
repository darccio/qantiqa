package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.httpclient.Header;

import play.cache.Cache;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;
import proto.Error.hash;
import utils.NotAcceptable;
import utils.TwitterRequest;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

import com.google.protobuf.JsonFormat;
import com.google.protobuf.XmlFormat;

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
        checkFormat(m);
        checkAuthentication(m);
        checkMethod(m);
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

    protected static void proxyToTwitter() {
        Format format = Cache.get(getFormatKey(), Format.class);

        TwitterRequest twitter = new TwitterRequest();
        twitter.proxy(request);

        response.current().status = twitter.getStatus();
        response.current().contentType = twitter.getContentType();

        Header[] headers = twitter.getHeaders();
        for (Header header : headers) {
            response.current().setHeader(header.getName(), header.getValue());
        }

        switch (format) {
        case ATOM:
        case RSS:
        case XML:
            renderXml(twitter.getXml());
            break;
        case JSON:
            renderText(twitter.getJson());
            break;
        case RAW:
            renderBinary(twitter.getStream());
            break;
        }
    }
}
