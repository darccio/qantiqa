package network;

import easypastry.util.Utilities;

public class Storage {

    public static final Storage users = new Storage("users");
    public static final Storage usersById = new Storage("usersById");

    private String id;
    private String hash;

    private Storage(String id) {
        this.id = id;
        this.hash = Utilities.generateStringHash("p2p://" + id);
    }

    public String getHash() {
        return this.hash;
    }

    public String toString() {
        return id;
    }
}
