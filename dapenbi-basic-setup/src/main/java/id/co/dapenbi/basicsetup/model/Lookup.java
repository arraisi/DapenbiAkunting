package id.co.dapenbi.basicsetup.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "BSE_LOOKUP")
public class Lookup {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BSE_LOOKUP_SEQ")
	@SequenceGenerator(name = "BSE_LOOKUP_SEQ", sequenceName = "BSE_LOOKUP_SEQ", allocationSize = 1)
	@Column(name = "LOOKUP_ID")
	private Long lookupId;

	@Column(name = "LOOKUP_CODE")
	private String lookupCode;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "FLAG_ACTIVE")
	private boolean flagActive;

	@Column(name = "FLAG_SYSTEM")
	private boolean flagSystem;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATION_DATE")
	private Date creationDate;

	@Column(name = "LAST_UPDATED_BY")
	private String lastUpdatedBy;

	@Column(name = "LAST_UPDATE_DATE")
	private Date lastUpdateDate;

	public Long getLookupId() {
		return lookupId;
	}

	public void setLookupId(Long lookupId) {
		this.lookupId = lookupId;
	}

	public String getLookupCode() {
		return lookupCode;
	}

	public void setLookupCode(String lookupCode) {
		this.lookupCode = lookupCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isFlagActive() {
		return flagActive;
	}

	public void setFlagActive(boolean flagActive) {
		this.flagActive = flagActive;
	}

	public boolean isFlagSystem() {
		return flagSystem;
	}

	public void setFlagSystem(boolean flagSystem) {
		this.flagSystem = flagSystem;
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
