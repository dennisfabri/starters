package org.lisasp.basics.jre.id;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator {
    @Override
    public String nextId() {
        return UUID.randomUUID().toString();
    }
}
