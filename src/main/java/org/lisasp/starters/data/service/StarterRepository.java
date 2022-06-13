package org.lisasp.starters.data.service;

import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.formula.functions.T;
import org.lisasp.starters.data.entity.Starter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StarterRepository extends JpaRepository<Starter, UUID> {

    Page<Starter> findByOrganization(String organization, Pageable pageable);
    List<Starter> findByOrganization(String organization);
}
