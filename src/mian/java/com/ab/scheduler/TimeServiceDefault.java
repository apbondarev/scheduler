package com.ab.scheduler;

import java.time.Instant;

/**
 * The default implementation of {@link TimeService}
 * It returns the current time from the system clock.
 * 
 * @author bondarev
 *
 */
class TimeServiceDefault implements TimeService {

    static TimeService instance = new TimeServiceDefault();
    
    @Override
    public Instant now() {
        return Instant.now();
    }

}
