package org.lisasp.starters.data.generator;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lisasp.basics.jre.id.IdGenerator;
import org.lisasp.starters.data.entity.Starter;
import org.lisasp.starters.data.service.StarterRepository;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StarterImporter {

    private final IdGenerator idGenerator;

    public void doImport(StarterRepository repository) {
        if (repository.count() > 0) {
            log.info("Starters already imported - checking for updates.");
        } else {
            log.info("Importing starters.");
        }

        try (FileInputStream inputStream = new FileInputStream("import/starters.csv")) {
            List<ImportedStarter> records = new CsvToBeanBuilder<ImportedStarter>(new InputStreamReader(inputStream,
                    StandardCharsets.UTF_8)).withType(ImportedStarter.class).withSeparator(
                    ';').build().parse();
            records.forEach(r -> {
                Starter entity = r.toEntity(idGenerator);
                if (!repository.existsByStartnumber(entity.getStartnumber())) {
                    log.info("Starter: {}", r);
                    repository.save(entity);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Importing starters - finished");
    }
}
