package id.co.dapenbi.audittrail;

import id.co.dapenbi.audittrail.service.AuditTrailService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AuditTrailController {
    @Autowired
    public AuditTrailService auditTrailService;

    private Object currentObject;
    private Object prevObject;

    public void setAuditObject(Object currentObject, Object prevObject){
        this.currentObject = currentObject;
        this.prevObject = prevObject;
    }

    public Object getCurrentObject() {
        return currentObject;
    }

    public Object getPrevObject() {
        return prevObject;
    }
}
