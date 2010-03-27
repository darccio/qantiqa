package controllers;

import static constants.Format.JSON;
import static constants.Format.XML;
import static constants.HttpMethod.POST;
import annotations.Formats;
import annotations.Methods;
import annotations.RequiresAuthentication;

public class Qfavorites extends QController {

    @Methods( { POST })
    @Formats( { XML, JSON })
    @RequiresAuthentication
    public static void create(Long id) {
        proxyToTwitter();
    }
}
