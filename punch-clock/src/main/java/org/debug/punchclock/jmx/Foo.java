package org.debug.punchclock.jmx;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Created by vishnuhr on 27/11/15.
 * just a sample test jmx program
 * TODO: remove this class.
 */
public class Foo {

    public static void main(String[] args) throws Exception {
        String host = "localhost";  // or some A.B.C.D
        int port = 9010;//Integer.parseInt(9010);

        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";
        JMXServiceURL serviceUrl = new JMXServiceURL(url);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);
        try {
            MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();
            // now query to get the beans or whatever
            System.out.println("mbean count is " + mbeanConn.getMBeanCount());
            mbeanConn.getMBeanInfo(new ObjectName("org.debug.punchclock.jmx:type=PunchClockJmx"));
            int a =10;

        } finally {
            jmxConnector.close();
        }

    }
}
