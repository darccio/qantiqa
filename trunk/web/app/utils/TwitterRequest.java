package utils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.w3c.dom.Document;

import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Http.Request;
import constants.HttpMethod;

public class TwitterRequest {

    private static final String target = "http://twitter.com";
    private HttpResponse response;
    private Header[] headers;

    public void proxy(Request request) {
        WSRequest proxied = play.libs.WS.url(target + request.url);

        if (request.user != null) {
            play.libs.WS.authenticate(request.user, request.password);
        }

        switch (HttpMethod.valueOf(request.method)) {
        case GET:
            response = proxied.get();
            break;
        case POST:
            response = proxied.post();
            break;
        case PUT:
            response = proxied.put();
            break;
        case DELETE:
            response = proxied.delete();
            break;
        }

        // TODO Send patch to Play! Framework to add getHeaders() method.
        try {
            Field methodField = HttpResponse.class.getDeclaredField("method");
            methodField.setAccessible(true);

            org.apache.commons.httpclient.HttpMethod method = (org.apache.commons.httpclient.HttpMethod) methodField
                    .get(response);

            headers = method.getResponseHeaders();
        } catch (Exception e) {
            headers = new Header[] {};
        }
    }

    public Header[] getHeaders() {
        return headers;
    }

    public Integer getStatus() {
        return response.getStatus();
    }

    public InputStream getStream() {
        return response.getStream();
    }

    public String getContentType() {
        return response.getContentType();
    }

    public String getJson() {
        return response.getString();
    }

    public Document getXml() {
        return response.getXml();
    }
}
