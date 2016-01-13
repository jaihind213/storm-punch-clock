package org.debug.sample.txn;

/**
 * Created by vishnuhr on 13/1/16.
 */

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseTransactionalBolt;
import backtype.storm.transactional.ICommitter;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.tuple.Tuple;
import org.debug.punchclock.PunchClock;
import org.debug.punchclock.jmx.PunchClockJmx;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;


/**
 * Created by vishnuhr on 9/1/16.
 */
public class BatchBolt extends BaseTransactionalBolt implements ICommitter {

    static {
        PunchClock.getInstance().setDeactivated(false); //turn on punch clock
        PunchClockJmx.registerMBean();
    }

    private String punchCardId;

    public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, TransactionAttempt id) {
        //Create your Punch ID - Recommend to have "<SPOUT/BOLT name>" + delimiter + "HostName" + delimiter + "ThreadId"
        //Thread id is a must, so that we dont have contention in the punch clock as it uses hashmap as its register
        try {
            this.punchCardId ="Bolt__"+ InetAddress.getLocalHost().getHostAddress()+"__"+Thread.currentThread().getId()+"__"+System.currentTimeMillis();
        } catch (UnknownHostException e) {
            this.punchCardId ="Bolt__"+Thread.currentThread().getId()+"__"+System.currentTimeMillis();
        }
    }

    public void execute(Tuple tuple) {
        //execute can be called multiple times, but we punch in only once as punchCardId remains same.
        PunchClock.getInstance().punchIn(punchCardId);
        if(tuple != null){
            System.out.println("---Execute Tuple called----"+tuple.getValueByField("key"));
        }
    }

    public void finishBatch() {
        try {
            Thread.sleep(20000);//sleep so that you can see punchcards via jmx.
        } catch (InterruptedException e) {}
        //once you do your thing. then punchout.
        PunchClock.getInstance().punchOut(punchCardId);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}

