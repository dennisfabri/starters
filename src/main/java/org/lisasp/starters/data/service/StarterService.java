package org.lisasp.starters.data.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.lisasp.starters.data.entity.Starter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StarterService {

    private final StarterRepository repository;

    public StarterService(StarterRepository repository) {
        this.repository = repository;
    }

    public Optional<Starter> get(UUID id) {
        return repository.findById(id);
    }

    public Starter update(Starter entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Starter> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Starter> list() {
        return repository.findAll();
    }

    public Page<Starter> list(String organization, Pageable pageable) {
        return repository.findByOrganization(organization, pageable);
    }

    public List<Starter> list(String organization) {
        return repository.findByOrganization(organization);
    }

    public int count() {
        return (int) repository.count();
    }

}
