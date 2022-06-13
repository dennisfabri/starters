package org.lisasp.starters.views.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lisasp.starters.data.entity.Starter;
import org.lisasp.starters.data.entity.Team;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamVM {
    private UUID id;
    private String startnumber;
    private String discipline;
    private String gender;
    private Starter starter1;
    private Starter starter2;
    private Starter starter3;
    private Starter starter4;
    private String organization;

    public TeamVM(Team team, List<Starter> starters) {
        this(team, starters.stream().collect(Collectors.toMap(Starter::getStartnumber, s -> s)));
    }

    public TeamVM(Team team, Map<String, Starter> starters) {
        this(team.getId(), team.getStartnumber(), team.getDiscipline(), team.getGender(), null, null, null, null, team.getOrganization());

        starter1 = starters.get(team.getStarter1());
        starter2 = starters.get(team.getStarter2());
        starter3 = starters.get(team.getStarter3());
        starter4 = starters.get(team.getStarter4());
    }

    public Team toEntity() {
        Team team = new Team(startnumber, discipline, gender, getStartnumber1(), getStartnumber2(), getStartnumber3(), getStartnumber4(), organization);
        team.setId(id);
        return team;
    }

    private String getStartnumber1() {
        if (starter1 == null) {
            return null;
        }
        return starter1.getStartnumber();
    }

    private String getStartnumber2() {
        if (starter2 == null) {
            return null;
        }
        return starter2.getStartnumber();
    }

    private String getStartnumber3() {
        if (starter3 == null) {
            return null;
        }
        return starter3.getStartnumber();
    }

    private String getStartnumber4() {
        if (starter4 == null) {
            return null;
        }
        return starter4.getStartnumber();
    }
}
