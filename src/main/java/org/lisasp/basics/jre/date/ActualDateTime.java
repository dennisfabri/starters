package org.lisasp.basics.jre.date;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ActualDateTime implements DateTimeFacade {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }
}
