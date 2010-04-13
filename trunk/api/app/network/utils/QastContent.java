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

package network.utils;

import java.lang.reflect.Method;

import com.google.protobuf.Message;

import easypastry.cast.CastContent;

public class QastContent extends CastContent {

    // private final byte[] msg;

    public QastContent(String subject, Message msg) {
        super(subject);

        // this.msg = msg.toByteArray();
    }

    public static <V extends Message> V getMessage(Class<V> klass, String sMsg) {
        V msg;

        if (klass == null) {
            msg = null;
        } else {
            try {
                Method m = klass.getDeclaredMethod("parseFrom",
                        new Class<?>[] { byte[].class });
                msg = (V) m.invoke(null, sMsg.getBytes());
            } catch (Exception e) {
                msg = null;
            }
        }

        return msg;
    }
}
