package id.co.dapenbi.audittrail.repository;

import id.co.dapenbi.audittrail.model.AuditTrail;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailRepository extends DataTablesRepository<AuditTrail, Long> {
}
