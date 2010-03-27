package controllers;

import static constants.Format.JSON;
import static constants.Format.XML;
import static constants.HttpMethod.DELETE;
import static constants.HttpMethod.GET;
import static constants.HttpMethod.POST;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

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
