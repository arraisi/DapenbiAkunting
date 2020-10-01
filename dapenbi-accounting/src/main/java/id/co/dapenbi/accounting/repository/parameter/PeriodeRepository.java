package id.co.dapenbi.accounting.repository.parameter;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.Optional;

public interface PeriodeRepository extends DataTablesRepository<Periode, String> {
    Optional<Periode> findByNamaPeriode(String namePeriode);

    @Modifying
    @Query("update Periode set namaPeriode = ?2, triwulan = ?3, quarter = ?4, statusAktif = ?5, updatedBy = ?6, updatedDate = ?7 where kodePeriode = ?1")
    void update(
            String kodePeriode,
            String namaPeriode,
            String triwulan,
            String quarter,
            String statusAktif,
            String updatedBy,
            Timestamp updatedDate
            );

    @Query("from Periode order by kodePeriode asc")
    Iterable<Periode> findAll();
}
