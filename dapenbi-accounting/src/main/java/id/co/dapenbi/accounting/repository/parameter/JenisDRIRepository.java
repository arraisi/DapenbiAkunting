package id.co.dapenbi.accounting.repository.parameter;

import id.co.dapenbi.accounting.entity.parameter.JenisDRI;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;

public interface JenisDRIRepository extends DataTablesRepository<JenisDRI, String> {

    @Modifying
    @Query("update JenisDRI set namaDRI = ?2, statusAktif = ?3, updatedBy = ?4, updatedDate = ?5 where kodeDRI = ?1")
    void update(
            String kodeDRI,
            String namaDRI,
            String statusAktif,
            String updatedBy,
            Timestamp updatedDate
    );
}
