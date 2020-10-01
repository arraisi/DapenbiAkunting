package id.co.dapenbi.accounting.repository.parameter;

import id.co.dapenbi.accounting.entity.parameter.Rekening;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RekeningRepository extends DataTablesRepository<Rekening, Integer> {
    @Query("from Rekening where idParent = '0' order by idRekening asc")
    Iterable<Rekening> findAllParent();

    @Query("from Rekening where idParent = ?1 order by idRekening asc")
    Iterable<Rekening> findByParent(Integer parent);

    Long countByIdRekening(Integer idRekening);

    Long countByIdParent(Integer idParent);

    int deleteByIdParent(Integer idParent);

    @Modifying
    @Query("update Rekening set statusAktif = ?2 where idParent = ?1")
    int updateStatusAktifByIdParent(Integer idParent, String status);

    Optional<Rekening> findByIdParent(Integer idParent);

    Optional<Rekening> findByIdRekening(int idRekening);

    @Query("from Rekening where 1 = 1 order by kodeRekening asc")
    List<Rekening> findAllByOrder();

    @Query("from Rekening where 1 = 1 and tipeRekening =?1 and isSummary = 'N' order by kodeRekening asc")
    List<Rekening> findByTipeRekening(String tipeRekening);

    @Modifying
    @Query("update Rekening set isEdit = ?2 where idRekening = ?1")
    int updateEdited(Integer idRekening, Integer status);

    @Query("from Rekening where kodeRekening = ?1")
    Optional<Rekening> findByKodeRekening(String kodeRekening);

    @Query("from Rekening where levelRekening = ?1 order by kodeRekening")
    List<Rekening> listByLevel(Integer level);
}
