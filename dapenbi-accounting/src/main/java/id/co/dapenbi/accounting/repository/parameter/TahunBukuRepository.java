package id.co.dapenbi.accounting.repository.parameter;

import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface TahunBukuRepository extends DataTablesRepository<TahunBuku, String> {
    @Query("from TahunBuku where statusAktif = '1'")
    Optional<TahunBuku> findByStatusAktif();

    Optional<TahunBuku> findByTahun(String tahun);

    @Modifying
    @Query("update TahunBuku set namaTahunBuku = ?2, tahun = ?3, statusAktif = ?4, updatedBy = ?5, updatedDate = ?6 where kodeTahunBuku = ?1")
    void update(
            String kodeTahunBuku,
            String namaTahunBuku,
            String tahun,
            String statusAktif,
            String updatedBy,
            Timestamp updatedDate
    );

    @Query("from TahunBuku where tahun = ?1")
    Optional<TahunBuku> dataByTahun(String tahun);

    @Query("from TahunBuku where 1 = 1 order by tahun ASC")
    List<TahunBuku> listTahunSort();

    @Query("from TahunBuku where statusAktif = '1' order by tahun asc")
    List<TahunBuku> listByStatusAktif();
}
