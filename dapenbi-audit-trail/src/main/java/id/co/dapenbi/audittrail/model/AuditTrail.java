package id.co.dapenbi.audittrail.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "AUD_AUDIT_TRAIL")
public class AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUD_AUDIT_TRAIL_SEQ")
    @SequenceGenerator(name = "AUD_AUDIT_TRAIL_SEQ", sequenceName = "AUD_AUDIT_TRAIL_SEQ", allocationSize = 1)
    @Column(name = "AUDIT_TRAIL_ID")
    private Long auditTrailId;

    @Column(name = "ACTION_TYPE")
    private String actionType;

    @Column(name = "MODULE_CODE")
    private String moduleCode;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "AUDIT_TIMESTAMP")
    private Date auditTimestamp;

    @Column(name = "PREVIOUS_OBJECT")
    private String previousObject;

    @Column(name = "CURRENT_OBJECT")
    private String currentObject;

    public Long getAuditTrailId() {
        return auditTrailId;
    }

    public void setAuditTrailId(Long auditTrailId) {
        this.auditTrailId = auditTrailId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getAuditTimestamp() {
        return auditTimestamp;
    }

    public void setAuditTimestamp(Date auditTimestamp) {
        this.auditTimestamp = auditTimestamp;
    }

    public String getPreviousObject() {
        return previousObject;
    }

    public void setPreviousObject(String previousObject) {
        this.previousObject = previousObject;
    }

    public String getCurrentObject() {
        return currentObject;
    }

    public void setCurrentObject(String currentObject) {
        this.currentObject = currentObject;
    }
}
