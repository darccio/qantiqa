package controllers;

import static constants.Format.JSON;
import static constants.Format.RAW;
import static constants.Format.XML;
import static constants.HttpMethod.GET;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

public class Qaccount extends QController {

    @Methods( { GET })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void verify_credentials() {
        proxyToTwitter();
    }

    @Methods( { GET })
    @Formats( { RAW })
    public static void profile_image(String screen_name) {
        proxyToTwitter();
    }
}
