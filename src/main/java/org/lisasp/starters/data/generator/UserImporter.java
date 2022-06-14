package org.lisasp.starters.data.generator;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lisasp.basics.jre.id.IdGenerator;
import org.lisasp.starters.data.Role;
import org.lisasp.starters.data.entity.User;
import org.lisasp.starters.data.service.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserImporter {

    private final IdGenerator idGenerator;

    public void doImport(PasswordEncoder passwordEncoder, UserRepository repository) {
        log.info("Importing Users");
        importFile(passwordEncoder, repository, "import/users.csv", Role.USER);
        importFile(passwordEncoder, repository, "import/admins.csv", Role.ADMIN);
        log.info("Importing users - finished");
    }

    private void importFile(PasswordEncoder passwordEncoder, UserRepository repository, String filename, Role role) {
        try (FileInputStream inputStream = new FileInputStream(filename)) {
            List<ImportedUser> records = new CsvToBeanBuilder(new InputStreamReader(inputStream,
                                                                                       StandardCharsets.UTF_8)).withType(ImportedUser.class).withSeparator(
                    ';').build().parse();
            records.forEach(r -> {
                log.info("User: {}", r.getName());
                createUser(r.getName(), passwordEncoder, r.getPassword(), role, repository);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createUser(String name,
                            PasswordEncoder passwordEncoder,
                            String password,
                            Role role,
                            UserRepository userRepository) {
        User user = userRepository.findByUsername(name.toLowerCase(Locale.ROOT));
        if (user == null) {
            user = new User();
        }
        user.setName(name);
        user.setUsername(name.toLowerCase(Locale.ROOT));
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(role));
        userRepository.save(user);
    }
}
