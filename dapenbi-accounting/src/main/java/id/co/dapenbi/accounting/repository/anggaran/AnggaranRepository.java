package id.co.dapenbi.accounting.repository.anggaran;

import id.co.dapenbi.accounting.entity.anggaran.Anggaran;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnggaranRepository extends DataTablesRepository<Anggaran, String> {

    @Query("from Anggaran where idRekening.idRekening = ?1 and tahunBuku.kodeTahunBuku = ?2 order by createdDate DESC")
    List<Anggaran> listByRekeningAndTahunBuku(Integer idRekening, String kodeTahunBuku);

    @Modifying
    @Query("update Anggaran set statusAktif = ?1 where noAnggaran = ?2")
    void setStatusAktif(String statusAktif, String noAnggaran);

    @Query("from Anggaran where idRekening.idRekening = ?1 and tahunBuku.kodeTahunBuku = ?2 and statusAktif = '1'")
    Optional<Anggaran> getRealisasi(Integer idRekening, String kodeTahunBuku);

    @Query("from Anggaran where idRekening.idRekening = ?1 and tahunBuku.kodeTahunBuku = ?2 and statusAktif = '1'")
    Optional<Anggaran> getRealisasiBerjalan(Integer idRekening, String kodeTahunBuku);

    @Query("from Anggaran where idRekening = ?1 and statusAktif = '1'")
    Optional<Anggaran> findByIdRekeningAndStatusAktif1(Rekening idRekening);

    @Modifying
    @Query("update Anggaran set statusAktif = 0 where idRekening.idRekening = ?1 and tahunBuku.kodeTahunBuku = ?2")
    void updateStatusAktifByIdRekening(Integer idRekening, String kodeTahunBuku);

    Iterable<Anggaran> findByIdRekeningAndStatusDataNot(Rekening idRekening, String statusData);
}
