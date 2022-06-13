package org.lisasp.starters.data.service;

import java.util.UUID;
import org.lisasp.starters.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByUsername(String username);
}