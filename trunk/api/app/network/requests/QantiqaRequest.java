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

package network.requests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import network.Overlay;

import org.apache.commons.httpclient.Header;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import play.Logger;
import play.mvc.Http.Request;

public class QantiqaRequest implements ProxyRequest {

    private Overlay overlay;

    public String getContentType() {
        return "text";
    }

    public Header[] getHeaders() {
        return new Header[] {};
    }

    public String getJson() {
        return null;
    }

    public Integer getStatus() {
        return 200;
    }

    public InputStream getStream() {
        return new ByteArrayInputStream("<test />".getBytes());
    }

    public Document getXml() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            return dbf.newDocumentBuilder().parse(getStream());
        } catch (SAXException e) {
            Logger.warn(
                    "Parsing error when building Document object from stream.",
                    e);
        } catch (IOException e) {
            Logger.warn(
                    "Parsing error when building Document object from stream.",
                    e);
        } catch (ParserConfigurationException e) {
            Logger.warn(
                    "Parsing error when building Document object from stream.",
                    e);
        }

        return null;
    }

    public void proxy(Request request) {
        System.out.println(request.action);
        System.out.println(request.actionMethod);
    }

    public void setOverlay(Overlay overlay) {
        this.overlay = overlay;
    }
}
