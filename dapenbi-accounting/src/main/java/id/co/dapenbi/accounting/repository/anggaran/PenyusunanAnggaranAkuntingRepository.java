package id.co.dapenbi.accounting.repository.anggaran;

import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkunting;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface PenyusunanAnggaranAkuntingRepository extends DataTablesRepository<PenyusunanAnggaranAkunting, String> {

    @Query("from PenyusunanAnggaranAkunting where kodeThnBuku.kodeTahunBuku = ?1 order by createdDate DESC")
    List<PenyusunanAnggaranAkunting> listByTahunBukuPeriode(String kodeThnBuku);
    
    @Modifying
    @Query("update PenyusunanAnggaranAkunting set statusAktif = ?1 where noAnggaran = ?2")
    void setStatusAktif(String statusAktif, String noAnggaran);

    @Modifying
    @Query("update PenyusunanAnggaranAkunting set statusData = ?2, userValidasi = ?3, tglValidasi = ?4, updatedBy = ?5, updatedDate = ?6, statusAktif = ?7 where noAnggaran = ?1")
    void setStatusData(String noAnggaran, String statusData, String userValidasi, Timestamp tglValidasi, String updatedBy, Timestamp updatedDate, String statusAktif);
}
