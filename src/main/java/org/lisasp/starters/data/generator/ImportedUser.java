package org.lisasp.starters.data.generator;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.lisasp.basics.jre.id.IdGenerator;
import org.lisasp.starters.data.entity.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ImportedUser {

    @CsvBindByName(column="name")
    private String name;
    @CsvBindByName(column="password")
    private String password;
}
