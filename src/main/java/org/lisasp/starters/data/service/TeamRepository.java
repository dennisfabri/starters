package org.lisasp.starters.data.service;

import java.util.UUID;

import org.lisasp.starters.data.entity.Starter;
import org.lisasp.starters.data.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    Page<Team> findByOrganization(String organization, Pageable pageable);

    long countByGender(String gender);
    long countByDiscipline(String discipline);

    void deleteByGender(String gender);
}
