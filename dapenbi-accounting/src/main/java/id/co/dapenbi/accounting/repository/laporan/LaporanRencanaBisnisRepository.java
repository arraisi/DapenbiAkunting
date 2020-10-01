package id.co.dapenbi.accounting.repository.laporan;

import id.co.dapenbi.accounting.entity.laporan.LaporanKeuangan;
import id.co.dapenbi.accounting.entity.laporan.LaporanRencanaBisnis;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

public interface LaporanRencanaBisnisRepository extends DataTablesRepository<LaporanRencanaBisnis, Integer> {

    Optional<LaporanKeuangan> findByKodeRumus(String kodeRumus);

    @Query("from LaporanRencanaBisnis where kodeRumus = ?1 and idLaporanHdr = ?2 and kodeThnBuku = ?3 and kodePeriode = ?4 and kodeDRI = ?5")
    Optional<LaporanRencanaBisnis> findByKodeRumusAndIdLaporanHdrAndTahunBukuAndPeriodeAndDRI(String kodeRumus, Integer idLaporanHdr, String kodeTahunBuku, String kodePeriode, String kodeDRI);

    @Query("from LaporanRencanaBisnis where idLaporanDtl = ?1 and kodeThnBuku = ?2 and kodePeriode = ?3 and kodeDRI = ?4")
    Optional<LaporanRencanaBisnis> findByIdLaporanDtlAndTahunBukuAndPeriodeAndDRI(Integer idLaporanDtl, String kodeTahunBuku, String kodePeriode, String kodeDRI);


    @Modifying
    @Query("update LaporanRencanaBisnis set saldoBerjalan = ?2, saldoSebelum = ?3, tglLaporan = ?4, updatedBy = ?5, updatedDate = ?6 where idLaporan = ?1")
    void update(Integer idLaporan, BigDecimal saldoBerjalan, BigDecimal saldoSebelum, Timestamp tglLaporan, String updatedBy, Timestamp updatedDate);
}
