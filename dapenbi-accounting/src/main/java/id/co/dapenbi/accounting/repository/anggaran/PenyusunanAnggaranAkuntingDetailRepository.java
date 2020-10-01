package id.co.dapenbi.accounting.repository.anggaran;

import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkuntingDetail;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;

public interface PenyusunanAnggaranAkuntingDetailRepository extends DataTablesRepository<PenyusunanAnggaranAkuntingDetail, Integer> {

    int deleteByNoAnggaran(String noAnggaran);

    @Modifying
    @Query("update PenyusunanAnggaranAkuntingDetail set statusAktif = ?1 where noAnggaran = ?2")
    void setStatusAktif(String statusAktif, String noAnggaran);

    @Modifying
    @Query("update PenyusunanAnggaranAkuntingDetail set statusData = ?2, userValidasi = ?3, tglValidasi = ?4, updatedBy = ?5, updatedDate = ?6, statusAktif = ?7 where noAnggaran = ?1")
    void setStatusData(String noAnggaran, String statusData, String userValidasi, Timestamp tglValidasi, String updatedBy, Timestamp updatedDate, String statusAktif);

}
