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

    @CsvBindByName(column = "S#")
    private String startnumber;
    @CsvBindByName(column = "Geschlecht")
    private String gender;

    @CsvBindByName(column = "Gliederung")
    private String organization;
    @CsvBindByName(column = "Disziplin")
    private String discipline;
    @CsvBindByName(column = "Id1")
    private String id1;
    @CsvBindByName(column = "Id2")
    private String id2;
    @CsvBindByName(column = "Id3")
    private String id3;
    @CsvBindByName(column = "Id4")
    private String id4;
    @CsvBindByName(column = "round")
    private int round;

    public List<Team> toEntities(IdGenerator idGenerator) {
        ArrayList<Team> teams = new ArrayList<>();
        extracted(discipline, idGenerator, teams);
        return teams;
    }

    private void extracted(String discipline, IdGenerator idGenerator, ArrayList<Team> teams) {
        if (discipline != null && !discipline.isBlank()) {
            Team starter = new Team(startnumber, discipline.trim(), gender, id1, id2, id3, id4, organization, round);
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
        return String.format("%s-%s%s", pre, post.length() == 1 ? "0" : "", post);
    }
}
