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

package im.dario.qantiqa.mock.gluon;

import network.Overlay;

/**
 * Auxiliary class intended to simulate/mock a gluon, without webservice
 * interaction nor qa-api-node initialization.
 * 
 * It will help to a faster debugging, without requiring an active webservice
 * due gluons are not used as normal peers to send quarks.
 * 
 * Anyway, all the code and conf are developed inside qa-node-api project
 * classes, making this a lightweight class.
 * 
 * @author Dario
 */
public class GluonMock {

    /**
     * Initialize the gluon, using the Overlay class.
     */
    public void start() {
        // Path relative to $workspace/qantiqa/gluon-mock/
        String cfgPath = "../api/conf/easypastry-config.xml";

        Overlay overlay = Overlay.initGluon(cfgPath);
        overlay.bootGluon();
    }

    public static void main(String[] args) {
        GluonMock app = new GluonMock();
        app.start();
    }
}
