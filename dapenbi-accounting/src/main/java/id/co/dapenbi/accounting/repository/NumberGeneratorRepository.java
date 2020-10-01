package id.co.dapenbi.accounting.repository;

import id.co.dapenbi.accounting.entity.NumberGenerator;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NumberGeneratorRepository extends DataTablesRepository<NumberGenerator, Integer> {
    Optional<NumberGenerator> findByName(String name);

    @Modifying
    @Query("UPDATE NumberGenerator SET generateNumber = (generateNumber+1) WHERE name = ?1")
    int incrementByName(String name);
}
