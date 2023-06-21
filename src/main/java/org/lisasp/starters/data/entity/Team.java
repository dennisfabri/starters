package org.lisasp.starters.data.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "organization", "discipline", "gender", "round" }) })
public class Team extends AbstractEntity {

    @Column(nullable = false)
    private String startnumber;
    @Column(nullable = false)
    private String discipline;
    @Column(nullable = false)
    private String gender;
    private String starter1;
    private String starter2;
    private String starter3;
    private String starter4;
    @Column(nullable = false)
    private String organization;
    @ColumnDefault("0")
    private int round;
}
