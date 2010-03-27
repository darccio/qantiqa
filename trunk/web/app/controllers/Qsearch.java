package controllers;

import static constants.Format.ATOM;
import static constants.Format.JSON;
import static constants.HttpMethod.GET;
import annotations.Formats;
import annotations.Methods;

public class Qsearch extends QController {

    @Methods( { GET })
    @Formats( { ATOM, JSON })
    public static void index(String q) {
        proxyToTwitter();
    }
}
