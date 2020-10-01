package id.co.dapenbi.accounting.repository.laporan;

import id.co.dapenbi.accounting.entity.laporan.LaporanHeader;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface LaporanHeaderRepository extends DataTablesRepository<LaporanHeader, Integer> {

    @Modifying
    @Query("update LaporanHeader set namaLaporan = ?2, keterangan = ?3, namaTabel = ?4, urutan = ?5, statusData = ?6, updatedBy = ?7, updatedDate = ?8 where idLaporanHdr = ?1")
    void updateLaporanHeader(Integer idLaporanHdr, String namaLaporan, String keterangan, String namaTabel, Integer urutan, String statusData, String updatedBy, Timestamp updatedDate);

    @Query("from LaporanHeader where 1 = 1 and namaTabel = ?1 order by urutan asc")
    List<LaporanHeader> listByNamaTabelSortByUrutan(String namaTabel);
}
