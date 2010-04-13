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

package jobs;

import im.dario.qantiqa.common.higgs.HiggsWS;
import im.dario.qantiqa.common.protocol.Protocol;

import java.io.FileInputStream;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.utils.Properties;

@OnApplicationStart
public class GluonMode extends Job {

    public void doJob() throws Exception {
        String proxyTo = Play.configuration.getProperty("qantiqa.proxyTo");

        if (proxyTo != null) {
            if (proxyTo.toLowerCase().equals("qantiqa")) {
                Boolean isGluon = Boolean.valueOf(Play.configuration
                        .getProperty("qantiqa.isGluon"));

                if (isGluon) {
                    String secret = Play.configuration
                            .getProperty("application.secret");

                    Properties p = new Properties();
                    p.load(new FileInputStream(Play
                            .getFile("conf/bunshin.properties")));

                    Protocol.validation rs = HiggsWS.validate(Integer.valueOf(p
                            .get("BUNSHIN_PORT")), secret);

                    if (!rs.getIsOk()) {
                        play.Logger.error(rs.getMessage());
                        Play.configuration.put("qantiqa.isGluon", "false");
                    }
                }
            }
        }
    }
}
