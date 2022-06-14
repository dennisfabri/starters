package org.lisasp.starters.data.service;

import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lisasp.starters.data.entity.Starter;
import org.lisasp.starters.data.entity.Team;
import org.lisasp.starters.views.team.TeamVM;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamRepository repository;

    private final StarterService starterService;

    public Optional<TeamVM> get(UUID id) {

        Optional<Team> team = repository.findById(id);
        if (team.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new TeamVM(team.get(), starterService.list(team.get().getOrganization())));
    }

    public TeamVM update(TeamVM entity) {
        Team team = repository.save(entity.toEntity());
        log.info("Saved Team: {}", entity);
        return new TeamVM(team, starterService.list(team.getOrganization()));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<TeamVM> list(Pageable pageable) {
        Function<? super Team, TeamVM> converter = new TeamToVMConverter(starterService.list());
        return repository.findAll(pageable).map(converter);
    }

    public Page<TeamVM> list(String organization, Pageable pageable) {
        Function<? super Team, TeamVM> converter = new TeamToVMConverter(starterService.list(organization));
        return repository.findByOrganization(organization, pageable).map(converter);
    }

    public int count() {
        return (int) repository.count();
    }

    public Starter[] getStarters(String organization, String gender) {
        List<Starter> starters = starterService.list(organization);
        if (!gender.equalsIgnoreCase("mixed"))  {
            starters = starters.stream().filter(s -> s.getGender().equalsIgnoreCase(gender)).toList();
        }

        List<Starter> result = new ArrayList<>();
        // result.add(null);
        result.addAll(starters);
        return result.toArray(Starter[]::new);
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
