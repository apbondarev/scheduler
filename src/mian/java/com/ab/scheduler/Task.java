package com.ab.scheduler;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class Task<V> {

    private LocalDateTime when;
    
    private Callable<V> callable;
    
    
}
