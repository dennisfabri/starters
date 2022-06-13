package org.lisasp.basics.jre.date;

import java.time.LocalDate;

public class ActualDate implements DateFacade {

    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}
