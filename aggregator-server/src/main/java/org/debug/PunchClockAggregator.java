package org.debug;

/**
 * Hello world!
 *
 */

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;
import org.debug.aggserver.conf.Config;
import org.debug.aggserver.util.JmxUtil;
import org.debug.aggserver.util.JsonUtil;
import org.debug.model.PunchCardsSummary;
import org.debug.punchclock.PunchCard;
import org.debug.punchclock.jmx.PunchClockJmx;

import javax.management.*;
import javax.management.remote.JMXConnector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

@Slf4j
public class PunchClockAggregator
{
    public static void main(String[] args) throws IOException, JsonMappingException {

        final String configFilePath = args[0];
        Config config = readConfigs(configFilePath);

        get("/hello/:name", (request, response) -> {
            return "Hello: " + request.params(":name");
        });

        get("/PunchCards", (request, response) -> {
            response.type("application/json");
            List<PunchCardsSummary.HostSummary> hostSummaryList = new ArrayList<>();
            List<String> failureHosts = new ArrayList<>();
            PunchCardsSummary punchCardsSummary = new PunchCardsSummary();

            for(String jmxServiceUrl : config.getPunchClockJmxUrls()){
                try {
                    PunchCardsSummary.HostSummary hostSummary= new PunchCardsSummary.HostSummary();
                    hostSummary.setHostUrl(jmxServiceUrl);
                    hostSummary.setCards((getCards(jmxServiceUrl)));
                    hostSummaryList.add(hostSummary);
                } catch (Exception e) {
                    log.error("Error in getting punch cards for {}", jmxServiceUrl);
                    response.status(409);
                    failureHosts.add(jmxServiceUrl);
                }
            }
            if(failureHosts.size() == config.getPunchClockJmxUrls().size()){
                response.status(404);
            }

            punchCardsSummary.setHostSummaryList(hostSummaryList);
            punchCardsSummary.setFailureHosts(failureHosts);
            return JsonUtil.Objectmapper.writeValueAsString(punchCardsSummary);
        });

        post("/clear", (request, response) -> {
            Map<String,List<String>> failures = new HashMap<String, List<String>>();
            List<String> failureHosts = new ArrayList<>();
            for(String jmxServiceUrl : config.getPunchClockJmxUrls()){
                JMXConnector connector = null;
                try {
                    connector = JmxUtil.getJmxConnector(jmxServiceUrl);
                    connector.getMBeanServerConnection().invoke(new ObjectName(PunchClockJmx.JMX_OBJECT_NAME),"clear",null,null);
                    log.info("Cleared cards on {}", jmxServiceUrl);
                } catch (Exception e) {
                    failureHosts.add(jmxServiceUrl);
                    log.error("Error in clearing punch cards for {}",jmxServiceUrl,e);
                }finally {
                    if(connector != null){
                        connector.close();
                    }
                }
            }
            if(failureHosts.size() > 0){
                response.status(409);
            }
            failures.put("failureHosts",failureHosts);
            return JsonUtil.Objectmapper.writeValueAsString(failures);
        });

        post("/deactivate", (request, response) -> {
            Map<String,List<String>> failures = new HashMap<String, List<String>>();
            List failureHosts = deactivate(true,config.getPunchClockJmxUrls());
            if(failureHosts.size() > 0){
                response.status(409);
            }
            failures.put("failureHosts",failureHosts);
            return JsonUtil.Objectmapper.writeValueAsString(failures);
        });

        post("/activate", (request, response) -> {
            Map<String,List<String>> failures = new HashMap<String, List<String>>();

            List failureHosts = deactivate(false,config.getPunchClockJmxUrls());
            if(failureHosts.size() > 0){
                response.status(409);
            }
            failures.put("failureHosts",failureHosts);
            return JsonUtil.Objectmapper.writeValueAsString(failures);
        });;

    }

    private static Config readConfigs(String filePath) throws IOException, JsonMappingException {
        File file = new File(filePath);
        if(!file.exists()){
            throw new FileNotFoundException("Could not file config file at "+ filePath);
        }
        return JsonUtil.yamlObjectmapper.readValue(file, Config.class);
    }

    private static List<PunchCard> getCards(String jmxServiceUrl) throws IOException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        log.info("Getting punch cards for {}", jmxServiceUrl);
        List<PunchCard> cards = null;
        JMXConnector jmxConnector = null;
        try {
            jmxConnector = JmxUtil.getJmxConnector(jmxServiceUrl);
            MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();
            cards = ((List)mbeanConn.getAttribute(new ObjectName(PunchClockJmx.JMX_OBJECT_NAME),"PunchCards"));
            if(cards == null){
                cards = new ArrayList<>(); //emptylist
            }
            log.info("Got {} punch cards for {}",cards.size() ,jmxServiceUrl);
        } finally {
            if(jmxConnector != null){
                jmxConnector.close();
            }
        }

        return cards;
    }

    private static List<String> deactivate(boolean flag, List<String> jmxServiceUrls) throws IOException {
        List<String> failureHosts = new ArrayList<>();
        for(String jmxServiceUrl : jmxServiceUrls){
            JMXConnector connector = null;
            try {
                connector = JmxUtil.getJmxConnector(jmxServiceUrl);
                connector.getMBeanServerConnection().invoke(new ObjectName(PunchClockJmx.JMX_OBJECT_NAME),"deactivate",new Object[]{flag},deactivate_signature);
                log.info("Deactivated flag set to {} . Punchclock on {}",flag, jmxServiceUrl);
            } catch (Exception e) {
                failureHosts.add(jmxServiceUrl);
                log.error("Error in setting deactivation flag {} in Punchclock for {}",flag, jmxServiceUrl,e);
            }finally {
                if(connector != null){
                    connector.close();
                }
            }
        }
        return failureHosts;
    }

    private static String[] deactivate_signature = new String[] {"boolean"};
}
