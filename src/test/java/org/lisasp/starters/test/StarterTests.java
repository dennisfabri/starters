package org.lisasp.starters.test;

import static org.junit.Assert.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lisasp.starters.data.entity.Starter;
import org.lisasp.starters.data.service.StarterRepository;
import org.lisasp.starters.data.service.StarterService;

import java.util.Optional;
import java.util.UUID;

class StarterTests {

    private StarterRepository repository;
    private StarterService service;

    @BeforeEach
    void prepare() {
        repository = new FakeStarterRepository();
        service = new StarterService(repository);
    }

    @Test
    void addStarter() {
        UUID id = service.update(new Starter("1-1", "Jane", "Doe", 2008, "female", "lv")).getId();

        Optional<Starter> actual = service.get(id);
        assertTrue(actual.isPresent());
        Starter starter = actual.get();
        assertEquals("1-1", starter.getStartnumber());
        assertEquals("Jane", starter.getFirstName());
        assertEquals("Doe", starter.getLastName());
        assertEquals((Integer)2008, starter.getYearOfBirth());
        assertEquals("female", starter.getGender());
        assertEquals("lv", starter.getOrganization());

        assertEquals(1, service.count());
    }

    @Test
    void editStarterWithoutSave() {
        UUID id = service.update(new Starter("1-1", "Jane", "Doe", 2008, "female", "lv")).getId();
        Optional<Starter> edit = service.get(id);
        edit.get().setFirstName("Amy");
        edit.get().setLastName("Smith");
        edit.get().setYearOfBirth(2007);
        edit.get().setOrganization("og");

        // do not save:
        // service.update(edit.get());

        Optional<Starter> actual = service.get(id);
        assertTrue(actual.isPresent());
        Starter starter = actual.get();
        assertEquals("1-1", starter.getStartnumber());
        assertEquals("Jane", starter.getFirstName());
        assertEquals("Doe", starter.getLastName());
        assertEquals((Integer)2008, starter.getYearOfBirth());
        assertEquals("female", starter.getGender());
        assertEquals("lv", starter.getOrganization());

        assertEquals(1, service.count());
    }

    @Test
    void editStarter() {
        UUID id = service.update(new Starter("1-1", "Jane", "Doe", 2008, "female", "lv")).getId();
        Optional<Starter> edit = service.get(id);
        edit.get().setFirstName("Amy");
        edit.get().setLastName("Smith");
        edit.get().setYearOfBirth(2007);
        edit.get().setOrganization("og");

        service.update(edit.get());

        Optional<Starter> actual = service.get(id);
        assertTrue(actual.isPresent());
        Starter starter = actual.get();
        assertEquals("1-1", starter.getStartnumber());
        assertEquals("Amy", starter.getFirstName());
        assertEquals("Smith", starter.getLastName());
        assertEquals((Integer)2007, starter.getYearOfBirth());
        assertEquals("female", starter.getGender());
        assertEquals("og", starter.getOrganization());

        assertEquals(1, service.count());
    }
}
