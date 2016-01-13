package org.debug.punchclock;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by vishnuhr on 23/11/15.
 */
public class PunchCardTest extends TestCase{

    @Test
    public void testEquality() throws InterruptedException {
        PunchCard firstCard = new PunchCard("luke");
        PunchCard secondCard = new PunchCard("anakin");
        Assert.assertEquals(false, firstCard.equals(secondCard));

        firstCard = new PunchCard("luke");
        Thread.sleep(2);//so that punchIn time is different
        secondCard = new PunchCard("luke");
        Assert.assertEquals(true, firstCard.equals(secondCard));

        firstCard = new PunchCard("LUKE");
        secondCard = new PunchCard("luke");
        Assert.assertEquals(false, firstCard.equals(secondCard));
    }

    @Test
    public void testHashCode() throws InterruptedException {
        PunchCard firstCard = new PunchCard("luke");
        PunchCard secondCard = new PunchCard("anakin");
        Assert.assertFalse(firstCard.hashCode() == secondCard.hashCode());

        firstCard = new PunchCard("luke");
        Thread.sleep(2);//so that punchIn time is different
        secondCard = new PunchCard("luke");
        Assert.assertTrue(firstCard.hashCode() == secondCard.hashCode());

        firstCard = new PunchCard("LUKE");
        secondCard = new PunchCard("luke");
        Assert.assertFalse(firstCard.hashCode() == secondCard.hashCode());
    }
}
