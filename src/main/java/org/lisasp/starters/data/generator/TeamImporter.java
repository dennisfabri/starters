package org.lisasp.starters.data.generator;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lisasp.basics.jre.id.IdGenerator;
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
        if (repository.count() > 0) {
            log.info("Teams already imported.");
            return;
        }
        log.info("Importing teams.");

        importFile("import/Mannschaft Ocean.csv", repository);
        importFile("import/Mannschaft Ocean Mixed.csv", repository);
        importFile("import/Mannschaft Pool.csv", repository);
        importFile("import/Mannschaft Ocean Mixed.csv", repository);
        log.info("Importing teams - finished");
    }

    private void importFile(String filename, TeamRepository repository) {
        try (FileInputStream inputStream = new FileInputStream(new File(filename))) {
            List<ImportedTeam> records = new CsvToBeanBuilder(new InputStreamReader(inputStream,
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
