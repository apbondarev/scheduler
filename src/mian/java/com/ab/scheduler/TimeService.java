package com.ab.scheduler;

import java.time.Instant;

/**
 * Time service. 
 * It facilitate making unit tests.
 * 
 * @author bondarev
 *
 */
interface TimeService {
    /**
     * @return The current time.
     */
    Instant now();
}
