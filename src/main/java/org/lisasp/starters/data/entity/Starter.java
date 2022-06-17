package org.lisasp.starters.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Starter extends AbstractEntity {

    @Column(unique = true)
    @NotBlank
    private String startnumber;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Min(0)
    @Max(2100)
    private Integer yearOfBirth;
    @NotBlank
    private String gender;
    @NotBlank
    private String organization;

    public <S extends Starter> Starter(S entity) {
        this(entity.getStartnumber(), entity.getFirstName(), entity.getLastName(), entity.getYearOfBirth(), entity.getGender(), entity.getOrganization());
        setId(entity.getId());
    }

    public String toString() {
        return "Starter(startnumber=" + this.getStartnumber() + ", firstName=" + this.getFirstName() + ", lastName=" + this.getLastName() + ", yearOfBirth=" + this.getYearOfBirth() + ", gender=" + this.getGender() + ", organization=" + this.getOrganization() + ")";
    }

    public String yearOfBirthAsString() {
        return yearOfBirth <= 0 ? "" : "" + yearOfBirth;
    }
}
