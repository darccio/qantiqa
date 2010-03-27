package controllers;

import static constants.Format.ATOM;
import static constants.Format.JSON;
import static constants.Format.RSS;
import static constants.Format.XML;
import static constants.HttpMethod.DELETE;
import static constants.HttpMethod.GET;
import static constants.HttpMethod.POST;
import static constants.HttpMethod.PUT;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

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
        proxyToTwitter();
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
