# storm-punch-clock
A Punch clock for debugging Apache Storm. (https://en.wikipedia.org/wiki/Time_clock)

Punch-Clock
-----------
You have a card per person.

The person punches IN with the card when he/she enters the office.

The person punches OUT with the card when he/she leaves the office.

The punch clock records the time of entry/exit on the card

Punch-Clock Motivation
----------------------
When did the Person enter / exit the office ?

Who is still in office ?

Storm-Punch-Clock-Motivation
----------------------------

In a Transaction Storm topology,

Spout emits a batch of data(tuples) which forms a transaction. Every Bolt in the topology processes that batch of data (tuples).

Our intent is to debug Storm Transactional Topologies based on the notion that, Apache Storm Tuples go In & Out of Spouts/Bolts.

i.e answer these questions

1) When did the batch enter/exit the Spout/Bolt ? 

2) Which batch is still in the Spout/Bolt?  i.e.  are any batches STUCK ?

   On which host are they stuck ? 
   
   In which Spout/Bolt are they stuck ?

i.e.

Batch of Tuples Punch In and Punch Out in a bolt / spout.


Presentation:
-------------
https://docs.google.com/presentation/d/1-Jczsvds2D1WsFTupOfuVozMdRn3d9OauEvbvz8pYV0/edit#slide=id.g1053dcd014_0_131

Storm-Punch-Clocks-Implementation
---------------------------------
Spouts / Bolts housed in a storm worker jvm, which punch in and out of a punch clock.

Punch In = insert a card into the punch clock

Punch Out = remove a card from the punch clock

One Punch Clock per JVM. All punch cards which are currently punched in are exposed via JMX.

We have multiple JVM ,=> we have multiple Punch Clocks.

Batches move across storm workers & we have multiple JVM, so we need to aggregate the data across Punch Clocks.

We have a aggregator server, we which queries all the jvms using jmx to get the current punch cards and exposed this data via REST.

Refer to storm-samples/Readme & aggregator-server/Readme on how to access the api(s) and a demo.


How does the code look like?
----------------------------

In the emitBatch method of Partitioned Transactional Spout:

     PunchClock.getInstance().punchIn(punchCardId);  // Punch In 

     collector.emit(tuples);  // Emit tuple(s)

     PunchClock.getInstance().punchOut(punchCardId);  // Punch Out


Prepare method of Transactional Bolt:
```
       punchCardId ="Bolt__"+Thread.currentThread().getId()+"__"+System.currentTimeMillis();  //Create Punch Card for txn 
```
Execute method of Transactional Bolt:
```
       PunchClock.getInstance().punchIn(punchCardId);      // Punch In
```
In the finishBatch method of Transactional Bolt:
```
       PunchClock.getInstance().punchOut(punchCardId);  // Punch Out
```

Underlying implementation of Punch In / Out
-------------------------------------------
Punch In:
```
       hashmap.put(puncheeId, card); //using thread id as part of puncheeId , makes sure there are no conflicts
```

Punch Out:
```
       hashmap.remove(puncheeId);
```

Is is Intrusive ?
------------------
Yes, but looking at the above code, its an simple put/remove call on a hashmap.


Sample Output of GET /PunchCards from the aggregator Server:
------------------------------------------------------------

Here we have the cards from host 10.0.0.66 and we have a failed host 10.0.0.68 where we could not get cards from.

```json
{
  "hostSummaryList": [
    {
      "hostUrl": "service:jmx:rmi:///jndi/rmi://10.0.0.66:9010/jmxrmi",
      "cards": [
        {
          "punchInTime": 1452679995953,
          "punchOutTime": -1,
          "puncheeId": "Bolt__10.0.0.66__114__1452679995953"
        }
      ]
    }
  ],
  "failureHosts": [
    "service:jmx:rmi:///jndi\/rmi://10.0.0.68:9011/jmxrmi"
  ]
}
```
Demo:
-----
Refer to storm-samples/Readme & aggregator-server/Readme on how to access the api(s) and a demo.

PS: if there are no punch cards available anywhere and topology is stuck, then the problem is probably not your bolts/spout.
