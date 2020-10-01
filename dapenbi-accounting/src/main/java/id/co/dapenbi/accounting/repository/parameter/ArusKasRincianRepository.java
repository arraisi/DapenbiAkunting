package id.co.dapenbi.accounting.repository.parameter;

import id.co.dapenbi.accounting.entity.parameter.ArusKasRincian;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArusKasRincianRepository extends DataTablesRepository<ArusKasRincian, String> {

    Iterable<ArusKasRincian> findByKodeArusKas(String kodeArusKas);

    @Query("from ArusKasRincian where 1 = 1 order by cast(kodeArusKas as integer) asc, cast(kodeRincian as integer) asc")
    List<ArusKasRincian> listAllSortByKodeArusKasAndIdArusKas();
}
