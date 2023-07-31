package org.lisasp.starters.data.generator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lisasp.starters.data.service.StarterRepository;
import org.lisasp.starters.data.service.TeamRepository;
import org.lisasp.starters.data.service.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataUpdater {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final StarterRepository starterRepository;
    private final TeamRepository teamRepository;
    private final StarterImporter starterImporter;
    private final TeamImporter teamImporter;
    private final TeamMixedImporter teamMixedImporter;
    private final UserImporter userImporter;

    // @Transactional()
    public void run() {
        log.info("Generating demo data");
        try {
            teamImporter.doUpdate(teamRepository);
        } catch (RuntimeException ex) {
            DataUpdater.log.info("Could not import everything", ex);
        }
        log.info("Generated demo data");
    }
}
