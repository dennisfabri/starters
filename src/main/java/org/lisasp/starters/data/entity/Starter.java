package org.lisasp.starters.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Starter extends AbstractEntity {

    @NotBlank
    private String startnumber;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    // @Min(1900)
    // @Max(2100)
    private int yearOfBirth;
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
}
