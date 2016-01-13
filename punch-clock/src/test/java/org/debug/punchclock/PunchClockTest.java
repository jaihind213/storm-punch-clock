package org.debug.punchclock;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by vishnuhr on 23/11/15.
 */
public class PunchClockTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        PunchClock.getInstance().clear();
        PunchClock.getInstance().setDeactivated(false);//turn on
        Assert.assertEquals(0, PunchClock.getInstance().getPunchCards().size());
    }
    
    @Test
    public void testPunchInOut(){
        PunchClock.getInstance().clear();
        String punchee = "hans-solo";
        Assert.assertEquals(0, PunchClock.getInstance().getPunchCards().size());
        PunchClock.getInstance().punchIn(punchee);
        Assert.assertEquals(1, PunchClock.getInstance().getPunchCards().size());
        PunchClock.getInstance().punchOut(punchee);
        Assert.assertEquals(0, PunchClock.getInstance().getPunchCards().size());
    }

    @Test
    public void testPunchInOutDifferentPeopleWithSameName(){
        String punchee1 = "chewbacca".toUpperCase();
        String punchee2 = "chewbacca".toLowerCase();
        PunchClock.getInstance().punchIn(punchee1);
        Assert.assertEquals(1, PunchClock.getInstance().getPunchCards().size());
        PunchClock.getInstance().punchIn(punchee2);
        Assert.assertEquals(2, PunchClock.getInstance().getPunchCards().size());
        PunchClock.getInstance().punchOut(punchee2);
        PunchClock.getInstance().punchOut(punchee1);
        Assert.assertEquals(0, PunchClock.getInstance().getPunchCards().size());
    }

    @Test
    public void testPutNullEmptyPunchee(){
        PunchClock.getInstance().punchIn("");
        PunchClock.getInstance().punchIn(null);
        PunchClock.getInstance().punchIn("      ");
        Assert.assertEquals(0, PunchClock.getInstance().getPunchCards().size());
    }

    @Test
    public void testDeactivation(){
        PunchClock.getInstance().punchIn("vader");
        Assert.assertEquals(1, PunchClock.getInstance().getPunchCards().size());
        PunchClock.getInstance().setDeactivated(true);
        PunchClock.getInstance().punchIn("yoda");
        Assert.assertEquals(1, PunchClock.getInstance().getPunchCards().size());
        PunchClock.getInstance().punchOut("vader");
        Assert.assertEquals(1, PunchClock.getInstance().getPunchCards().size());

        PunchClock.getInstance().setDeactivated(false);
        PunchClock.getInstance().punchOut("vader");
        Assert.assertEquals(0, PunchClock.getInstance().getPunchCards().size());
    }
}
