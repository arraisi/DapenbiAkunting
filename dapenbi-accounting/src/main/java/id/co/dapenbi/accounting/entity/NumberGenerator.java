package id.co.dapenbi.accounting.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACC_NUMBER_GENERATOR")
public class NumberGenerator {

    @Id
    @SequenceGenerator(sequenceName = "ACC_NUMBER_GENERATOR_SEQ", allocationSize = 1, name = "numberGeneratorGenerator")
    @GeneratedValue(generator = "numberGeneratorGenerator")
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "GENERATE_NUMBER")
    private BigInteger generateNumber;

}
