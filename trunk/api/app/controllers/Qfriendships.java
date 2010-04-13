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

import static constants.Format.JSON;
import static constants.Format.XML;
import static constants.HttpMethod.DELETE;
import static constants.HttpMethod.GET;
import static constants.HttpMethod.POST;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

/**
 * REST API methods for friendships.
 * 
 * @author Dario
 */
public class Qfriendships extends QController {

    @Methods( { POST })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void create(String id) {
        proxyToTwitter();
    }

    @Methods( { POST, DELETE })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void destroy(String id) {
        proxyToTwitter();
    }

    @Methods( { GET })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void show(Long source_id, String source_screen_name,
            Long target_id, String target_screen_name) {
        proxyToTwitter();
    }
}
