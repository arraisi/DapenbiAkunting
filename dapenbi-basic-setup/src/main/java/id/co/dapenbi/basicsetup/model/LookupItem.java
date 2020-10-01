package id.co.dapenbi.basicsetup.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "BSE_LOOKUP_ITEM")
public class LookupItem {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BSE_LOOKUP_ITEM_SEQ")
	@SequenceGenerator(name = "BSE_LOOKUP_ITEM_SEQ", sequenceName = "BSE_LOOKUP_ITEM_SEQ", allocationSize = 1)
	@Column(name = "LOOKUP_ITEM_ID")
	private Long lookupItemId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "LOOKUP_ID", nullable = false)
	private Lookup lookup;

	@Column(name = "LOOKUP_ITEM_CODE")
	private String lookupItemCode;

	@Column(name = "LOOKUP_ITEM_NAME")
	private String lookupItemName;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATION_DATE")
	private Date creationDate;

	@Column(name = "LAST_UPDATED_BY")
	private String lastUpdatedBy;

	@Column(name = "LAST_UPDATE_DATE")
	private Date lastUpdateDate;

	public Long getLookupItemId() {
		return lookupItemId;
	}

	public void setLookupItemId(Long lookupItemId) {
		this.lookupItemId = lookupItemId;
	}

	public Lookup getLookup() {
		return lookup;
	}

	public void setLookup(Lookup lookup) {
		this.lookup = lookup;
	}

	public String getLookupItemCode() {
		return lookupItemCode;
	}

	public void setLookupItemCode(String lookupItemCode) {
		this.lookupItemCode = lookupItemCode;
	}

	public String getLookupItemName() {
		return lookupItemName;
	}

	public void setLookupItemName(String lookupItemName) {
		this.lookupItemName = lookupItemName;
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
