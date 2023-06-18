package org.lisasp.starters.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

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
