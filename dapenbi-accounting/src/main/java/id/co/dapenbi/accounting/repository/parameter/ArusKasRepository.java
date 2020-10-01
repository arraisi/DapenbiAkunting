package id.co.dapenbi.accounting.repository.parameter;

import id.co.dapenbi.accounting.entity.parameter.ArusKas;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;

public interface ArusKasRepository extends DataTablesRepository<ArusKas, String> {

    @Modifying
    @Query("update ArusKas set keterangan = ?2, statusAktif = ?3, updatedBy = ?4, updatedDate = ?5, arusKasAktivitas = ?6 where kodeArusKas = ?1")
    void update(
            String kodeArusKas,
            String keterangan,
            String statusAktif,
            String updatedBy,
            Timestamp updatedDate,
            String arusKasAktivitas
    );
}
