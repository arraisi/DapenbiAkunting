package id.co.dapenbi.accounting.repository.laporan;

import id.co.dapenbi.accounting.entity.laporan.LaporanKeuangan;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface LaporanKeuanganRepository extends DataTablesRepository<LaporanKeuangan, Integer> {

    Optional<LaporanKeuangan> findByKodeRumus(String kodeRumus);

    @Query("from LaporanKeuangan where kodeRumus = ?1 and kodeThnBuku = ?2 and kodePeriode = ?3 and kodeDRI = ?4")
    Optional<LaporanKeuangan> findByKodeRumusAndTahunBukuAndPeriodeAndDRI(String kodeRumus, String kodeTahunBuku, String kodePeriode, String kodeDRI, Integer idLaporanDtl);

    @Query("from LaporanKeuangan where kodeRumus = ?1 and idLaporanHdr = ?2 and kodeThnBuku = ?3 and kodePeriode = ?4 and kodeDRI = ?5")
    List<LaporanKeuangan> listByKodeRumusAndIdLaporanHdrAndTahunBukuAndPeriodeAndDRI(String kodeRumus, Integer idLaporanHdr, String kodeTahunBuku, String kodePeriode, String kodeDRI);

    @Query("from LaporanKeuangan where idLaporanDtl = ?1 and kodeThnBuku = ?2 and kodePeriode = ?3 and kodeDRI = ?4")
    Optional<LaporanKeuangan> findByIdLaporanDtlAndTahunBukuAndPeriodeAndDRI(Integer idLaporanDtl, String kodeTahunBuku, String kodePeriode, String kodeDRI);

    @Modifying
    @Query("update LaporanKeuangan set saldoBerjalan = ?2, saldoSebelum = ?3, tglLaporan = ?4, updatedBy = ?5, updatedDate = ?6 where idLaporan = ?1")
    void update(Integer idLaporan, BigDecimal saldoBerjalan, BigDecimal saldoSebelum, Timestamp tglLaporan, String updatedBy, Timestamp updatedDate);

    @Modifying
    @Query("delete from LaporanKeuangan where kodeThnBuku = ?1 and kodePeriode = ?2 and kodeDRI = ?3")
    void deleteByTahunBukuAndPeriodeAndDRI(String kodeTahunBuku, String kodePeriode, String kodeDRI);
}
