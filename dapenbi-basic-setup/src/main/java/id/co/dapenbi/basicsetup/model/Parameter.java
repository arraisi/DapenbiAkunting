package id.co.dapenbi.basicsetup.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "BSE_PARAMETER")
public class Parameter {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BSE_PARAMETER_SEQ")
	@SequenceGenerator(name = "BSE_PARAMETER_SEQ", sequenceName = "BSE_PARAMETER_SEQ", allocationSize = 1)
	@Column(name = "PARAMETER_ID")
	private Long parameterId;

	@Column(name = "PARAMETER_CODE")
	private String parameterCode;

	@Column(name = "PARAMETER_VALUE")
	private String parameterValue;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "FLAG_ACTIVE")
	private boolean flagActive;

	@Column(name = "CREATED_BY")
	public String createdBy;

	@Column(name = "CREATION_DATE")
	public Date creationDate;

	@Column(name = "LAST_UPDATED_BY")
	public String lastUpdatedBy;

	@Column(name = "LAST_UPDATE_DATE")
	public Date lastUpdateDate;

	public Long getParameterId() {
		return parameterId;
	}

	public void setParameterId(Long parameterId) {
		this.parameterId = parameterId;
	}

	public String getParameterCode() {
		return parameterCode;
	}

	public void setParameterCode(String parameterCode) {
		this.parameterCode = parameterCode;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFlagActive(boolean flagActive) {
		this.flagActive = flagActive;
	}

	public Boolean getFlagActive() {
		return flagActive;
	}

	public void setFlagActive(Boolean flagActive) {
		this.flagActive = flagActive;
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
