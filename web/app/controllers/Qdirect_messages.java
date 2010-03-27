package controllers;

import static constants.Format.ATOM;
import static constants.Format.JSON;
import static constants.Format.RSS;
import static constants.Format.XML;
import static constants.HttpMethod.GET;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

public class Qdirect_messages extends QController {

    @Methods( { GET })
    @Formats( { XML, JSON, RSS, ATOM })
    @RequiresAuthentication
    public static void index(Integer count) {
        proxyToTwitter();
    }
}
