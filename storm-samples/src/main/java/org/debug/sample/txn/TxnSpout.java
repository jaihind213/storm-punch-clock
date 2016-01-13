package org.debug.sample.txn;

/**
 * Created by vishnuhr on 13/1/16.
 */

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BasePartitionedTransactionalSpout;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.transactional.partitioned.IPartitionedTransactionalSpout;
import backtype.storm.tuple.Fields;
import org.debug.punchclock.PunchClock;
import org.debug.punchclock.jmx.PunchClockJmx;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vishnuhr on 9/1/16.
 */
public class TxnSpout  extends BasePartitionedTransactionalSpout<TxnSpout.Meta> {


    static {
        PunchClock.getInstance().setDeactivated(false); //turn on punch clock
        PunchClockJmx.registerMBean();
    }

    public Coordinator getCoordinator(Map conf, TopologyContext context) {
        return new Coordinator();
    }

    public Emitter<Meta> getEmitter(Map conf, TopologyContext context) {
        return new MyEmitter();
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tx","key"));
    }

    class Meta{}

    class Coordinator implements IPartitionedTransactionalSpout.Coordinator{

        public int numPartitions() {
            return 1;
        }

        public boolean isReady() {
            try {Thread.sleep(1000);} catch (InterruptedException ignore) {}
            return true;
        }

        public void close() {
        }
    }

    class MyEmitter implements Emitter<Meta>{

        public Meta emitPartitionBatchNew(TransactionAttempt tx, BatchOutputCollector collector, int partition, Meta lastPartitionMeta) {

            //Create your Punch ID - Recommend to have "<SPOUT/BOLT name>" + delimiter + "HostName" + delimiter + "ThreadId".
            //Thread id is a must, so that we dont have contention in the punch clock as it uses hashmap as its register
            String punchCardId;
            try {
                punchCardId = "SPOUT__"+ InetAddress.getLocalHost().getHostAddress()+Thread.currentThread().getId()+"__"+System.currentTimeMillis();
            } catch (UnknownHostException e) {
                punchCardId = "SPOUT__"+Thread.currentThread().getId()+"__"+System.currentTimeMillis();
            }

            //Punch In
            PunchClock.getInstance().punchIn(punchCardId);
            //Emit tuple(s)
            collector.emit(createSampleTuple(tx));
            //Punch Out
            PunchClock.getInstance().punchOut(punchCardId);

            return null; // for this sample topology, we dont care about meta.
        }

        public void emitPartitionBatch(TransactionAttempt tx, BatchOutputCollector collector, int partition, Meta partitionMeta) {
            collector.emit(createSampleTuple(tx));
            //// for this sample topology, we dont care about meta.
        }

        public void close() {
        }

        private List<Object> createSampleTuple(TransactionAttempt tx){
            final List<Object> l = new ArrayList();
            l.add(0, tx);
            l.add(1, "Key_1" + System.currentTimeMillis());
            return l;
        }
    }
}
