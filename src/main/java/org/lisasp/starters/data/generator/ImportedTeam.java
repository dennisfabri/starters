package org.lisasp.starters.data.generator;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.lisasp.basics.jre.id.IdGenerator;
import org.lisasp.starters.data.entity.Starter;
import org.lisasp.starters.data.entity.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ImportedTeam {

    @CsvBindByName(column="S#")
    private String startnumber;
    @CsvBindByName(column="Geschlecht")
    private String gender;

    @CsvBindByName(column="Gliederung")
    private String organization;
    @CsvBindByName(column="D1")
    private String d1;
    @CsvBindByName(column="D2")
    private String d2;
    @CsvBindByName(column="D3")
    private String d3;
    @CsvBindByName(column="D4")
    private String d4;

    public List<Team> toEntities(IdGenerator idGenerator) {
        ArrayList<Team> teams = new ArrayList<Team>();
        extracted(d1, idGenerator, teams);
        extracted(d2, idGenerator, teams);
        extracted(d3, idGenerator, teams);
        extracted(d4, idGenerator, teams);
        return teams;
    }

    private void extracted(String discipline, IdGenerator idGenerator, ArrayList<Team> teams) {
        if (discipline != null && !discipline.isBlank()) {
            Team starter = new Team(startnumber, discipline.trim(), gender, "" ,"" ,"" ,"", organization);
            starter.setId(UUID.fromString(idGenerator.nextId()));
            teams.add(starter);
        }
    }

    private String fixSN(String startnumber) {
        String[] parts = startnumber.split("-");
        if (parts.length != 2) {
            return startnumber;
        }
        String pre = parts[0];
        String post = parts[1];
        return String.format("%s-%s%s", pre, post.length() == 1 ?"0": "", post);
    }
}
