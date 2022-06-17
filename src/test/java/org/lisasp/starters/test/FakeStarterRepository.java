package org.lisasp.starters.test;

import org.lisasp.starters.data.entity.Starter;
import org.lisasp.starters.data.service.StarterRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class FakeStarterRepository implements StarterRepository {

    private HashMap<UUID, Starter> data = new HashMap<>();

    @Override
    public List<Starter> findAll() {
        return null;
    }

    @Override
    public List<Starter> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Starter> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Starter> findByOrganization(String organization, Pageable pageable) {
        return null;
    }

    @Override
    public List<Starter> findByOrganization(String organization) {
        return null;
    }

    @Override
    public boolean existsByStartnumber(String startnumber) {
        return false;
    }

    @Override
    public List<Starter> findAllById(Iterable<UUID> uuids) {
        return null;
    }

    @Override
    public long count() {
        return data.size();
    }

    @Override
    public void deleteById(UUID uuid) {

    }

    @Override
    public void delete(Starter entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends Starter> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Starter> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        data.put(entity.getId(), new Starter(entity));

        return entity;
    }

    @Override
    public <S extends Starter> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Starter> findById(UUID uuid) {
        return Optional.ofNullable(new Starter(data.get(uuid)));
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Starter> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Starter> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Starter> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Starter getOne(UUID uuid) {
        return null;
    }

    @Override
    public Starter getById(UUID uuid) {
        return null;
    }

    @Override
    public Starter getReferenceById(UUID uuid) {
        return null;
    }

    @Override
    public <S extends Starter> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Starter> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Starter> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Starter> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Starter> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Starter> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Starter, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
