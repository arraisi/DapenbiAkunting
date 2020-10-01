package id.co.dapenbi.basicsetup.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "BSE_LOOKUP_DEPENDENT")
public class LookupDependent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BSE_LOOKUP_DEPENDENT_SEQ")
    @SequenceGenerator(name = "BSE_LOOKUP_DEPENDENT_SEQ", sequenceName = "BSE_LOOKUP_DEPENDENT_SEQ", allocationSize = 1)
    @Column(name = "LOOKUP_DEPENDENT_ID")
    private Long lookupDependentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "LOOKUP_ITEM_ID", nullable = false)
    private LookupItem lookupItem;

    @Column(name = "LOOKUP_DEPENDENT_CODE")
    private String lookupDependentCode;

    @Column(name = "LOOKUP_DEPENDENT_VALUE")
    private String lookupDependentValue;

    @Column(name = "CREATED_BY")
    public String createdBy;

    @Column(name = "CREATION_DATE")
    public Date creationDate;

    @Column(name = "LAST_UPDATED_BY")
    public String lastUpdatedBy;

    @Column(name = "LAST_UPDATE_DATE")
    public Date lastUpdateDate;

    public Long getLookupDependentId() {
        return lookupDependentId;
    }

    public void setLookupDependentId(Long lookupDependentId) {
        this.lookupDependentId = lookupDependentId;
    }

    public LookupItem getLookupItem() {
        return lookupItem;
    }

    public void setLookupItem(LookupItem lookupItem) {
        this.lookupItem = lookupItem;
    }

    public String getLookupDependentCode() {
        return lookupDependentCode;
    }

    public void setLookupDependentCode(String lookupDependentCode) {
        this.lookupDependentCode = lookupDependentCode;
    }

    public String getLookupDependentValue() {
        return lookupDependentValue;
    }

    public void setLookupDependentValue(String lookupDependentValue) {
        this.lookupDependentValue = lookupDependentValue;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
