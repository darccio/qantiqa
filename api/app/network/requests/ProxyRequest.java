package network.requests;

import java.io.InputStream;

import org.apache.commons.httpclient.Header;
import org.w3c.dom.Document;

import play.mvc.Http.Request;

public interface ProxyRequest {

    public void proxy(Request request);

    public Header[] getHeaders();

    public Integer getStatus();

    public InputStream getStream();

    public String getContentType();

    public String getJson();

    public Document getXml();
}
