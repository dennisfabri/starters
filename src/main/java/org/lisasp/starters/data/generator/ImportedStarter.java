package org.lisasp.starters.data.generator;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.lisasp.basics.jre.id.IdGenerator;
import org.lisasp.starters.data.entity.Starter;

import java.util.UUID;

@Data
public class ImportedStarter {

    @CsvBindByName(column = "S#")
    private String startnumber;
    @CsvBindByName(column = "Vorname")
    private String firstName;
    @CsvBindByName(column = "Nachname")
    private String lastName;
    @CsvBindByName(column = "Jg")
    private int yearOfBirth;
    @CsvBindByName(column = "Geschlecht")
    private String gender;

    @CsvBindByName(column = "Gliederung")
    private String organization;

    public Starter toEntity(IdGenerator idGenerator) {
        Starter starter = new Starter(fixSN(startnumber), firstName, lastName, yearOfBirth, gender, organization);
        starter.setId(UUID.fromString(idGenerator.nextId()));
        if (starter.getYearOfBirth() <= 1900) {
            starter.setYearOfBirth(2000 + starter.getYearOfBirth());
        }
        return starter;
    }

    private String fixSN(String startnumber) {
        String[] parts = startnumber.split("-");
        if (parts.length != 2) {
            return startnumber;
        }
        String pre = parts[0];
        String post = parts[1];
        return String.format("%s-%s%s", pre, post.length() == 1 ? "0" : "", post);
    }
}
