package org.debug.punchclock;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Created by vishnuhr on 23/11/15.
 * https://en.wikipedia.org/wiki/Time_clock
 */
@Slf4j
public class PunchClock {

    private Map<String,PunchCard> register = new HashMap<String, PunchCard>();

    private volatile boolean deactivated = true;//off by default. man must activate if he wants to use it. (Done On Purpose!)

    private PunchClock() {
    }

    private static PunchClock punchClock = new PunchClock();

    public static PunchClock getInstance() {
        return punchClock;
    }

    public void punchIn(String puncheeId){

        if(deactivated){
            log.info("Cant Punch In, as Punch Clock is not active now.");
            return;
        }
        if(puncheeId == null || "".equals(puncheeId) || puncheeId.trim().equalsIgnoreCase("")){
            return;
        }
        if(register.get(puncheeId) == null){
            PunchCard card = new PunchCard(puncheeId);
            register.put(puncheeId, card);
            log.info("Punching IN. Created Card for Punchee: {}",card.toString());
        }
    }

    public PunchCard punchOut(String puncheeId){

        if(deactivated){
            log.info("Cant Punch Out, as Punch Clock is not active now.");
            return null;
        }

        if(puncheeId == null || "".equals(puncheeId) || puncheeId.trim().equalsIgnoreCase("")){
            return null;
        }

        PunchCard card = register.remove(puncheeId);
        if(card != null){
            card.setPunchOutTime(System.currentTimeMillis());
            log.info("Punching OUT: {}",card.toString());
        } else {
            if(log.isDebugEnabled()) {
                log.warn("Punchee {} never punched IN , but wants to Punch OUT.", puncheeId);
            }
        }
        return card;

    }

    public List<PunchCard> getPunchCards(){
        List<PunchCard> cards= new ArrayList<PunchCard>();
        cards.addAll(this.register.values());
        return cards;
    }

    public void clear(){
        register.clear();
        log.warn("PunchClock has been cleared.");
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
        log.info("PunchClock deactivation flag set to: {}",deactivated);
    }

}
