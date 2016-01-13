package org.debug.punchclock.jmx;

import org.debug.punchclock.PunchCard;

import java.util.List;

/**
 * Created by vishnuhr on 24/11/15.
 */
public interface PunchClockJmxMBean {

    List<PunchCard> getPunchCards();

    void deactivate(boolean turnOff);

    void clear();
}
