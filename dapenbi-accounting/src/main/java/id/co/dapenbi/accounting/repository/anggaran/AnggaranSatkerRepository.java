package id.co.dapenbi.accounting.repository.anggaran;

import id.co.dapenbi.accounting.entity.anggaran.AnggaranSatker;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AnggaranSatkerRepository extends DataTablesRepository<AnggaranSatker, String> {

    @Query("from AnggaranSatker anggaranSatker " +
            "where anggaranSatker.idRekening.idRekening = ?1 " +
            "   and anggaranSatker.idSatker.kodeLookup = ?2")
    Optional<AnggaranSatker> findByIdRekeningAndIdSatker(Integer idRekening, String idSatker);

    Iterable<AnggaranSatker> findByIdRekeningAndTahunBuku(Rekening idRekening, TahunBuku tahunBuku);
}
