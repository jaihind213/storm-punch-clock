package org.debug.aggserver.conf;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by vishnuhr on 12/1/16.
 */
@Setter @Getter
public class Config {
    /** The list of jmx urls for punch clocks for which we should do an aggregation **/
    private List<String> punchClockJmxUrls;
}
