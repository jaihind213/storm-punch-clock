package org.debug.model;

import lombok.Getter;
import lombok.Setter;
import org.debug.punchclock.PunchCard;

import java.util.List;

/**
 * Created by vishnuhr on 12/1/16.
 */
@Getter @Setter
public class PunchCardsSummary {

    private List<HostSummary> hostSummaryList;

    /** list of hosts for whom we could not get PunchCards**/
    List<String> failureHosts;

    @Getter @Setter
    public static class HostSummary{
        private String hostUrl;
        private List<PunchCard> cards;
    }
}
