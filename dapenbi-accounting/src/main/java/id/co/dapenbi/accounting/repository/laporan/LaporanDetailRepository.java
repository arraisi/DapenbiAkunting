package id.co.dapenbi.accounting.repository.laporan;

import id.co.dapenbi.accounting.entity.laporan.LaporanDetail;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface LaporanDetailRepository extends DataTablesRepository<LaporanDetail, Integer> {

    void deleteByLaporanHeader(Integer laporanHeader);

    @Query("from LaporanDetail where laporanHeader = ?1 and statusData = '1' order by cast(urutan as integer) asc")
    Iterable<LaporanDetail> findByLaporanHeader(Integer laporanHeader);

    @Query("from LaporanDetail where laporanHeader = ?1 and rumus is not null")
    List<LaporanDetail> findByLaporanHeaderWithRumus(Integer laporanHeader);

    @Query("from LaporanDetail where laporanHeader = ?1 and rumus is null")
    List<LaporanDetail> findByLaporanHeaderWithNoRumus(Integer laporanHeader);

    @Query("from LaporanDetail where laporanHeader = ?1 and urutan = ?2")
    Optional<LaporanDetail> findByIdLapHDRAndUrutan(Integer lapHDRId, String urutan);

    @Modifying
    @Query("update LaporanDetail set kodeRumus = ?2, levelAkun = ?3, rumus = ?4, statusData = ?5, updatedBy = ?6, updatedDate = ?7, cetakJudul = ?8, cetakGaris = ?9, spi = ?10, judul = ?11, urutan = ?12, saldoSebelum = ?13, statusRumus = ?14 where idLaporanDtl = ?1")
    void updateLaporanDetail(Integer idLaporanDtl, String kodeRumus, Integer levelAkun, String rumus, String statusData, String updatedBy, Timestamp updatedDate, String cetakJudul, String cetakGaris, String spi, String judul, String urutan, BigDecimal saldoSebelum, String statusRumus);

    Optional<LaporanDetail> findByKodeRumus(String kodeRumus);
}
