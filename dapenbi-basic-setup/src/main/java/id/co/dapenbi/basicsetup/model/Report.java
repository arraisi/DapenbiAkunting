package id.co.dapenbi.basicsetup.model;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "BSE_REPORT")
public class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BSE_REPORT_SEQ")
	@SequenceGenerator(name = "BSE_REPORT_SEQ", sequenceName = "BSE_REPORT_SEQ", allocationSize = 1)
	@Column(name = "REPORT_ID")
	private Long reportId;

	@OneToMany(mappedBy = "reportId", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@Fetch(value = FetchMode.SUBSELECT)
	@OrderBy("ORDER_NUMBER ASC")
	private Collection<ReportParameter> reportParameter = new LinkedHashSet<ReportParameter>();

	@Column(name = "REPORT_NAME")
	private String reportName;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "REPORT_FILE_PATH")
	private String reportFilePath;

	@Column(name = "FLAG_ACTIVE")
	private Boolean flagActive;

	@Column(name = "MODULE_NAME")
	private String moduleName;

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Collection<ReportParameter> getReportParameter() {
		return reportParameter;
	}

	public void setReportParameter(Collection<ReportParameter> reportParameter) {
		this.reportParameter = reportParameter;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReportFilePath() {
		return reportFilePath;
	}

	public void setReportFilePath(String reportFilePath) {
		this.reportFilePath = reportFilePath;
	}

	public Boolean getFlagActive() {
		return flagActive;
	}

	public void setFlagActive(Boolean flagActive) {
		this.flagActive = flagActive;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

}
