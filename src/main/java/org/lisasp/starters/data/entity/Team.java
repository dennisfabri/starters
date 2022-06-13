package org.lisasp.starters.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Team extends AbstractEntity {

    private String startnumber;
    private String discipline;
    private String gender;
    private String starter1;
    private String starter2;
    private String starter3;
    private String starter4;
    private String organization;
}
