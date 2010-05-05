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

import static constants.Format.ATOM;
import static constants.Format.JSON;
import static constants.Format.RSS;
import static constants.Format.XML;
import static constants.HttpMethod.DELETE;
import static constants.HttpMethod.GET;
import static constants.HttpMethod.POST;
import static constants.HttpMethod.PUT;
import network.services.QuarkService;
import im.dario.qantiqa.common.protocol.Protocol;
import im.dario.qantiqa.common.utils.QantiqaException;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

/**
 * REST API methods for statuses/tweets/quarks.
 * 
 * @author Dario
 */
public class Qstatuses extends QController {

    @Methods( { GET })
    @Formats( { XML, JSON, ATOM })
    @RequiresAuthentication
    public static void home_timeline(Integer count, Long since_id) {
        proxyToTwitter();
    }

    @Methods( { GET })
    @Formats( { XML, JSON, RSS, ATOM })
    @RequiresAuthentication
    public static void mentions(Integer count) {
        proxyToTwitter();
    }

    @Methods( { GET })
    @Formats( { XML, JSON, RSS, ATOM })
    public static void user_timeline(String id, Integer count) {
        proxyToTwitter();
    }

    @Methods( { GET })
    @Formats( { XML, JSON, ATOM })
    @RequiresAuthentication
    public static void retweeted_by_me(Integer count) {
        proxyToTwitter();
    }

    @Methods( { GET })
    @Formats( { XML, JSON })
    public static void show(Long id) {
        proxyToTwitter();
    }

    @Methods( { POST })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void update(String status, Long in_reply_to_status_id,
            String source) {
        Protocol.user user = getRequestUser();
        QuarkService qsv = new QuarkService(getOverlay());

        try {
            renderProtobuf(qsv.update(user, status, in_reply_to_status_id,
                    source));
        } catch (Exception e) {
            renderError(e);
        }
    }

    @Methods( { POST, DELETE })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void destroy(Long id) {
        proxyToTwitter();
    }

    @Methods( { POST, PUT })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void retweet(Long id, String source) {
        proxyToTwitter();
    }
}
