package id.co.dapenbi.audittrail.service;

import id.co.dapenbi.audittrail.model.AuditTrail;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface AuditTrailService {
    List<AuditTrail> findAll();
    boolean doAuditCreate(String moduleCode, Object currentObject, String userId);
    boolean doAuditUpdate(String moduleCode, Object currentObject, Object prevObject, String userId);
    boolean doAuditDelete(String moduleCode, Object prevObject, String userId);
    boolean doAuditValidate(String moduleCode,Object prevObject, Object currentObject, String userId);

}
