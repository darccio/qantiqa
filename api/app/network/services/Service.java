package network.services;

import network.Overlay;


public abstract class Service {

    protected final Overlay overlay;

    public Service(Overlay overlay) {
        this.overlay = overlay;
    }
}
