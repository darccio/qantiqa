package network.utils;

import rice.p2p.commonapi.NodeHandle;
import easypastry.cast.CastContent;
import easypastry.cast.CastListener;
import easypastry.sample.AppCastContent;

public abstract class QastListener implements CastListener {

    public final boolean contentAnycasting(CastContent cc) {
        return contentAnycasting((AppCastContent) cc);
    }

    public boolean contentAnycasting(AppCastContent qc) {
        return false;
    }

    public final void contentDelivery(CastContent cc) {
        contentDelivery((AppCastContent) cc);
    }

    public void contentDelivery(AppCastContent qc) {
    }

    public void hostUpdate(NodeHandle nh, boolean joined) {
    }
}
