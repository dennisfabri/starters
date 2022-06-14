package org.lisasp.starters.data.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lisasp.starters.data.entity.Starter;
import org.lisasp.starters.data.entity.Team;
import org.lisasp.starters.data.model.Discipline;
import org.lisasp.starters.data.model.ExportType;
import org.lisasp.starters.data.model.StarterExport;
import org.lisasp.starters.views.team.TeamVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final TeamRepository repository;

    private final StarterService starterService;

    public Optional<TeamVM> get(UUID id) {

        Optional<Team> team = repository.findById(id);
        if (team.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new TeamVM(team.get(), starterService.list(team.get().getOrganization())));
    }

    public Page<TeamVM> list(Pageable pageable) {
        Function<? super Team, TeamVM> converter = new TeamToVMConverter(starterService.list());
        return repository.findAll(pageable).map(converter);
    }

    public Page<TeamVM> list(String organization, Pageable pageable) {
        Function<? super Team, TeamVM> converter = new TeamToVMConverter(starterService.list(organization));
        return repository.findByOrganization(organization, pageable).map(converter);
    }

    public List<StarterExport> getStarters(ExportType exportType) {
        List<Starter> starters = starterService.list();

        return switch (exportType) {
            case Individual -> mapToIndividualExport(starters);
            case Members -> mapToTeamExport(starters);
            case MixedMembers -> mapToMixedExport(starters);
        };
    }

    private List<StarterExport> mapToIndividualExport(List<Starter> starters) {
        return starters.stream().map(starter -> new StarterExport(starter.getStartnumber(),
                                                                  starter.getFirstName(),
                                                                  starter.getLastName(),
                                                                  starter.getGender(), mapYearOfBirth(starter.getYearOfBirth()))).toList();
    }

    private List<StarterExport> mapToTeamExport(List<Starter> starters) {
        return starters.stream().map(starter -> new StarterExport(toTeamId(starter.getStartnumber(), starter.getGender()),
                                                                  starter.getFirstName(),
                                                                  starter.getLastName(),
                                                                  starter.getGender(),
                                                                  mapYearOfBirth(starter.getYearOfBirth()))).toList();
    }

    private List<StarterExport> mapToMixedExport(List<Starter> starters) {
        return starters.stream().map(starter -> new StarterExport(toMixedTeamId(starter.getStartnumber()),
                                                                  starter.getFirstName(),
                                                                  starter.getLastName(),
                                                                  starter.getGender(),
                                                                  mapYearOfBirth(starter.getYearOfBirth()))).toList();
    }

    private String mapYearOfBirth(int yearOfBirth) {
        if (yearOfBirth <= 0) {
            return "";
        }
        return "" + yearOfBirth;
    }

    private static char[] alphabet12 = "abcdefghijkl".toCharArray();

    private String toTeamId(String startnumber, String gender) {
        if (startnumber == null || startnumber.isBlank()) {
            return "";
        }
        String[] parts = startnumber.split("-");
        if (parts.length != 2) {
            return "";
        }

        String id = gender.equalsIgnoreCase("female") ? "1" : "2";

        int pos = (Integer.valueOf(parts[1]) - 1) % 6;

        return String.format("%s%s%s", parts[0], id, alphabet12[pos]);
    }

    private String toMixedTeamId(String startnumber) {
        if (startnumber == null || startnumber.isBlank()) {
            return "";
        }
        String[] parts = startnumber.split("-");
        if (parts.length != 2) {
            return "";
        }

        int pos = Integer.valueOf(parts[1]) - 1;

        return String.format("%s%s%s", parts[0], "0", alphabet12[pos]);
    }

    public Page<Discipline> listDisciplines(Pageable pageable) {
        return repository.findDisciplines(pageable);
    }

    public List<Team> listTeamsForDiscipline(Discipline discipline) {
        return repository.findByDisciplineAndGender(discipline.getName(), discipline.getGender()).stream().map(t -> updateStarter(t)).toList();
    }

    private Team updateStarter(Team t) {
        boolean isMixed = t.getGender().equalsIgnoreCase("mixed");
        t.setStarter1(mapSN(t.getStarter1(), isMixed));
        t.setStarter2(mapSN(t.getStarter2(), isMixed));
        t.setStarter3(mapSN(t.getStarter3(), isMixed));
        t.setStarter4(mapSN(t.getStarter4(), isMixed));
        return t;
    }

    private String mapSN(String starter, boolean isMixed) {
        if (starter == null || starter.isBlank()) {
            return "";
        }
        String[] parts = starter.split("-");
        if (parts.length != 2) {
            return starter;
        }
        int sn = Integer.valueOf(parts[1]);
        if (!isMixed && sn > 6) {
            sn -= 6;
        }
        sn--;
        return "" + sn;
    }

    private static class TeamToVMConverter implements Function<Team, TeamVM> {

        private final Map<String, Starter> starters;

        public TeamToVMConverter(List<Starter> starterList) {
            starters = starterList.stream().collect(Collectors.toMap(Starter::getStartnumber, s -> s));
        }

        @Override
        public TeamVM apply(Team team) {
            return new TeamVM(team, starters);
        }
    }
}
