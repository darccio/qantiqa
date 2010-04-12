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
