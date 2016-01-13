package org.debug.aggserver.util;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishnuhr on 12/1/16.
 */
public class JmxUtil {

    public static JMXConnector getJmxConnector(String jmxServiceUrl, long timeoutMs) throws IOException {
        JMXServiceURL serviceUrl = new JMXServiceURL(jmxServiceUrl);
        Map<String,Object> env = new HashMap<>();
        env.put("jmx.remote.x.request.waiting.timeout", new Long(timeoutMs));
        return JMXConnectorFactory.connect(serviceUrl, env);
    }

    public static JMXConnector getJmxConnector(String jmxServiceUrl) throws IOException {
        return getJmxConnector(jmxServiceUrl, 1500);
    }
}
