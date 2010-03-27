package controllers;

import static constants.Format.JSON;
import static constants.Format.XML;
import static constants.HttpMethod.GET;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;
import play.mvc.Controller;

public class Qusers extends QController {

    @Methods( { GET })
    @Formats( { XML, JSON })
    public static void show(String id) {
        proxyToTwitter();
    }

    @Methods( { GET })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void search(String q) {
        proxyToTwitter();
    }
}
