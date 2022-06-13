package org.lisasp.basics.jre.id;

import lombok.Synchronized;

public class CountingIdGenerator implements IdGenerator {

    private volatile int key = 0;

    @Synchronized
    @Override
    public String nextId() {
        key++;
        return "" + key;
    }
}
