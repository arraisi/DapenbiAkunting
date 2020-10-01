package id.co.dapenbi.basicsetup.repository;

import id.co.dapenbi.basicsetup.model.LookupDependent;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LookupDependentRepository extends DataTablesRepository<LookupDependent, Long> {
}
