package id.co.dapenbi.accounting.repository.laporan;

import id.co.dapenbi.accounting.entity.laporan.LaporanRekening;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;

public interface LaporanRekeningRepository extends DataTablesRepository<LaporanRekening, Integer> {

    void deleteByIdLaporanDtl(Integer idLaporanDtl);

    @Query("from LaporanRekening where idLaporanDtl = ?1 and statusData = '1'")
    Iterable<LaporanRekening> findByIdLaporanDtl(Integer idLaporanDtl);

    @Modifying
    @Query("update LaporanRekening set idRekening = ?2, rumusUrutan = ?3, rumusOperator = ?4, statusData = ?5, updatedBy = ?6, updatedDate = ?7 where idLaporanRek = ?1")
    void updateLaporanRekening(Integer idLaporanRek, Integer idRekening, Integer rumusUrutan, String rumusOperator, String statusData, String updatedBy, Timestamp updatedDate);
}
