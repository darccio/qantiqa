/*******************************************************************************
 * Qantiqa : Decentralized microblogging platform
 * Copyright (C) 2010 Dario (i@dario.im) 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/

package network.utils;

import rice.p2p.commonapi.NodeHandle;
import easypastry.cast.CastContent;
import easypastry.cast.CastListener;

/**
 * Wrapper class for {@link CastListener} to minimize {@link CastContent}
 * casting.
 * 
 * @author Dario
 */
public abstract class QastListener implements CastListener {

    public final boolean contentAnycasting(CastContent cc) {
        return contentAnycasting(new QastContent(cc));
    }

    public boolean contentAnycasting(QastContent qc) {
        return false;
    }

    public final void contentDelivery(CastContent cc) {
        contentDelivery(new QastContent(cc));
    }

    public void contentDelivery(QastContent qc) {
    }

    public void hostUpdate(NodeHandle nh, boolean joined) {
    }
}
