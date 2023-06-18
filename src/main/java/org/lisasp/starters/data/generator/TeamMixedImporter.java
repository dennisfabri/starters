package org.lisasp.starters.data.generator;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lisasp.basics.jre.id.IdGenerator;
import org.lisasp.starters.data.entity.Team;
import org.lisasp.starters.data.service.TeamRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class TeamMixedImporter {

    private final IdGenerator idGenerator;

    @Transactional
    public void doImport(TeamRepository repository) {
        fixTeams(repository);
        if (repository.countByDiscipline("Lifesaver Relay") > 0) {
            log.info("Mixed Teams already imported.");
            return;
        }
        log.info("Removing wrong teams");
        //repository.deleteByGender("mixed");

        log.info("Importing mixed teams.");

        importFile("import/Mannschaft Ocean Mixed.csv", repository);
        importFile("import/Mannschaft Pool Mixed.csv", repository);
        log.info("Importing mixed teams - finished");
    }

    private void fixTeams(TeamRepository repository) {
        List<Team> teams = repository.findByDisciplineAndGender("Lifesaver Relay", "mixed");
        teams.stream().filter(team -> team.getStartnumber() == null || team.getStartnumber().isBlank()).forEach(team -> {
            Optional<Team> ocean = repository.findByOrganizationAndDisciplineAndGender(team.getOrganization(), "Ocean Lifesaver Relay", team.getGender());
            ocean.ifPresent(o -> {
                team.setStartnumber(o.getStartnumber());
                repository.save(team);
                log.info("Team '{}' saved with '{}'", team.getOrganization(), team.getStartnumber());
            });
        });
    }

    private void importFile(String filename, TeamRepository repository) {
        try (FileInputStream inputStream = new FileInputStream(filename)) {
            List<ImportedTeam> records = new CsvToBeanBuilder<ImportedTeam>(new InputStreamReader(inputStream,
                    StandardCharsets.UTF_8)).withType(ImportedTeam.class).withSeparator(
                    ';').build().parse();
            records.forEach(r -> {
                log.info("Team: {}", r);
                repository.saveAll(r.toEntities(idGenerator));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
