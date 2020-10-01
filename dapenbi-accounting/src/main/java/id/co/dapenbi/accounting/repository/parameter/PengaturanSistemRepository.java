package id.co.dapenbi.accounting.repository.parameter;

import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.Optional;

public interface PengaturanSistemRepository extends DataTablesRepository<PengaturanSistem, String> {

    @Modifying
    @Query("update PengaturanSistem set statusAktif = '0' where 1 = 1")
    void resetStatusAktif();

    @Query("from PengaturanSistem where statusAktif = '1'")
    Optional<PengaturanSistem> findByStatusAktif();

    @Modifying
    @Query("update PengaturanSistem set statusOpen = 'O' where statusAktif = '1'")
    void setStatusOpen();

    @Modifying
    @Query("update PengaturanSistem set kodeTahunBuku = ?1, kodePeriode = ?2 where statusAktif = '1'")
    void setTahunBukuAndPeriode(String kodeTahunBuku, String kodePeriode);

    @Modifying
    @Query("update PengaturanSistem set tglTransaksi = ?1 where statusAktif = '1'")
    void setTanggalTransaksi(Timestamp tglTransaksi);

    Optional<PengaturanSistem> findByKodeTahunBuku(String kodeTahunBuku);
}
