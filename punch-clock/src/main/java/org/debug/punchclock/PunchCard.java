package org.debug.punchclock;

import lombok.*;

import java.io.Serializable;

/**
 * Created by vishnuhr on 23/11/15.
 * A card to punch into the punch clock.
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(exclude = {"punchInTime", "punchOutTime"})
@ToString
public class PunchCard implements Serializable{

    @Setter private long punchInTime = System.currentTimeMillis();

    @Setter private long punchOutTime = -1; //optional, you can set it. or discard this object itself.

    private final String puncheeId; // the id of the person punching into the punch clock
}
