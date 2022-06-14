package org.lisasp.starters.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.lisasp.starters.data.Role;
import org.lisasp.starters.data.entity.User;
import org.lisasp.starters.data.service.StarterRepository;
import org.lisasp.starters.data.service.TeamRepository;
import org.lisasp.starters.data.service.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringComponent
@RequiredArgsConstructor
@Slf4j
public class DataGenerator {

    private final StarterImporter starterImporter;
    private final TeamImporter teamImporter;
    private final TeamMixedImporter teamMixedImporter;
    private final UserImporter userImporter;

    @Bean
    @Transactional
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository,
                                      StarterRepository starterRepository, TeamRepository teamRepository) {
        return args -> {

            try {
                userImporter.doImport(passwordEncoder, userRepository);

                starterImporter.doImport(starterRepository);
                teamImporter.doImport(teamRepository);
                teamMixedImporter.doImport(teamRepository);
            } catch (RuntimeException ex) {
                DataGenerator.log.info("Could not import everything", ex);
            }
            log.info("Generated demo data");
        };
    }

}
