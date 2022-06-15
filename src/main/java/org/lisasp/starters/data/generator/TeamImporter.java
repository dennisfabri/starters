package org.lisasp.starters.data.generator;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lisasp.basics.jre.id.IdGenerator;
import org.lisasp.starters.data.entity.Team;
import org.lisasp.starters.data.service.StarterRepository;
import org.lisasp.starters.data.service.TeamRepository;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TeamImporter {

    private final IdGenerator idGenerator;

    public void doImport(TeamRepository repository) {
        if (repository.countByGender("female") > 0) {
            log.info("Teams already imported - checking for updates");
            // return;
        } else {
            log.info("Importing teams.");
        }

        removeEmptyTeams(repository);

        importFile("import/Mannschaft Ocean.csv", repository);
        importFile("import/Mannschaft Pool.csv", repository);
        log.info("Importing teams - finished");
    }

    private void removeEmptyTeams(TeamRepository repository) {
        repository.deleteAll(repository.findByOrganization(""));
        repository.deleteAll(repository.findByOrganization(null));
    }

    private void importFile(String filename, TeamRepository repository) {
        try (FileInputStream inputStream = new FileInputStream(filename)) {
            List<ImportedTeam> importedTeams = new CsvToBeanBuilder(new InputStreamReader(inputStream,
                                                                                          StandardCharsets.UTF_8)).withType(ImportedTeam.class).withSeparator(
                    ';').build().parse();
            importedTeams.forEach(r -> {
                List<Team> teams = r.toEntities(idGenerator);
                teams.stream().filter(t -> t.getOrganization() != null && !t.getOrganization().isBlank()).forEach(team -> {
                    if (!repository.existsByOrganizationAndDisciplineAndGender(team.getOrganization(),team.getDiscipline(), team.getGender())) {
                        log.info("Team: {}", r);
                        repository.save(team);
                    }
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
