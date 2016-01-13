package org.debug.punchclock.jmx;

import lombok.extern.slf4j.Slf4j;
import org.debug.punchclock.PunchCard;
import org.debug.punchclock.PunchClock;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * Created by vishnuhr on 24/11/15.
 */
@Slf4j
public class PunchClockJmx implements PunchClockJmxMBean{

    public final static String JMX_OBJECT_NAME = "org.debug.punchclock.jmx:type=PunchClockJmx";

    public List<PunchCard> getPunchCards() {
        return PunchClock.getInstance().getPunchCards();
    }

    public void deactivate(boolean turnOff) {
        PunchClock.getInstance().setDeactivated(turnOff);
    }

    public void clear() {
        PunchClock.getInstance().clear();
    }

    public static void registerMBean() {
        //Get the MBean server
        try {
            log.info("Registering PunchClock MBEAN with object Name: {} ....",JMX_OBJECT_NAME);
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            //register the MBean

            PunchClockJmxMBean mBean = new PunchClockJmx();
            ObjectName name = new ObjectName(JMX_OBJECT_NAME);
            mbs.registerMBean(mBean, name);
            log.info("Registered PunchClock MBEAN with object Name: {}", JMX_OBJECT_NAME);
        } catch (MalformedObjectNameException e) {
            log.error("Failed to register Mbean For PunchClockJmx object",e);
        } catch (InstanceAlreadyExistsException ignore) {
        } catch (MBeanRegistrationException e) {
            log.error("Failed to register Mbean For PunchClockJmx object", e);
        } catch (NotCompliantMBeanException e) {
            log.error("Failed to register Mbean For PunchClockJmx object",e);
        }
    }
}
