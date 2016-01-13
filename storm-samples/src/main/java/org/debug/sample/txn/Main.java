package org.debug.sample.txn;

/**
 * Created by vishnuhr on 13/1/16.
 */

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.transactional.TransactionalTopologyBuilder;

/**
 * Created by vishnuhr on 9/1/16.
 * Runs a transactional topology in local cluster.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("-------------------------------------------------------");
        System.out.println("To use PunchClock, Make sure jmx options are set in jvm.");
        System.out.println("-Dcom.sun.management.jmxremote " +
                           " -Dcom.sun.management.jmxremote.port=9010" +
                           " -Dcom.sun.management.jmxremote.local.only=false " +
                           " -Dcom.sun.management.jmxremote.authenticate=false" +
                           " -Dcom.sun.management.jmxremote.ssl=false");
        System.out.println("-------------------------------------------------------");


        final TransactionalTopologyBuilder builder =
                new TransactionalTopologyBuilder(
                        "testing_punchclock",
                        "spout",
                        new TxnSpout(),
                        2);

        builder.setBolt("bolt",
                new BatchBolt(),
                1)
                .shuffleGrouping("spout");

        Config conf = new Config();
        conf.setDebug(false);
        conf.setNumWorkers(3);

        final LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("testing_punchclock", conf, builder.buildTopology());

        Thread.sleep(600000);

        cluster.killTopology("testing_punchclock");
        cluster.shutdown();
    }
}

