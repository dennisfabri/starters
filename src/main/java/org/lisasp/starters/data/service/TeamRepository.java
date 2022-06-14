package org.lisasp.starters.data.service;

import org.lisasp.starters.data.entity.Team;
import org.lisasp.starters.views.export.Discipline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    Page<Team> findByOrganization(String organization, Pageable pageable);

    long countByGender(String gender);
    long countByDiscipline(String discipline);

    void deleteByGender(String gender);

    @Query("SELECT DISTINCT new org.lisasp.starters.views.export.Discipline(t.discipline, t.gender) FROM Team t")
    Page<Discipline> findDisciplines(Pageable pageable);

    List<Team> findByDisciplineAndGender(String discipline, String gender);
}
