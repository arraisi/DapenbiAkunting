package id.co.dapenbi.accounting.repository.master;

import id.co.dapenbi.accounting.entity.master.LookupMaster;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LookupMasterRepository extends DataTablesRepository<LookupMaster, String> {

    @Query("from LookupMaster where jenisLookup = ?1 and statusData = '1'")
    List<LookupMaster> findByJenisLookup(String jenisLookup);

    Optional<LookupMaster> findByKodeLookup(String kodeLookup);
}
