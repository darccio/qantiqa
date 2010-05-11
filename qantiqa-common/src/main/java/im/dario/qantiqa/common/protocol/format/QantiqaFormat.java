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

package im.dario.qantiqa.common.protocol.format;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

/**
 * Wrapper class around {@link XmlFormat} to separate the specific format
 * serializer to ease future changes.
 * 
 * @author Dario
 */
public class QantiqaFormat {

    public static void merge(InputStream input, Builder builder) {
        XmlFormat.merge(input, builder);
    }

    public static void merge(String data, Builder builder) {
        ByteArrayInputStream bais;
        try {
            bais = new ByteArrayInputStream(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        QantiqaFormat.merge(bais, builder);
    }

    public static String printToString(Message message) {
        return XmlFormat.printToString(message);
    }
}