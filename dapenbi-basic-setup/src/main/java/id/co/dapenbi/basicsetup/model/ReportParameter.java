package id.co.dapenbi.basicsetup.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "BSE_REPORT_PARAMETER")
public class ReportParameter {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BSE_REPORT_PARAMETER_SEQ")
	@SequenceGenerator(name = "BSE_REPORT_PARAMETER_SEQ", sequenceName = "BSE_REPORT_PARAMETER_SEQ", allocationSize = 1)
	@Column(name = "REPORT_PARAMETER_ID")
	private Long reportParameterId;

	@Column(name = "REPORT_ID")
	private Long reportId;

	@Column(name = "PARAMETER_NAME")
	private String parameterName;

	@Column(name = "PARAMETER_CAPTION")
	private String parameterCaption;

	@Column(name = "PARAMETER_TYPE")
	private String parameterType;

	@Column(name = "DATA_TYPE")
	private String dataType;

	@Column(name = "ORDER_NUMBER")
	private Long orderNumber;

	@Column(name = "LOOKUP_URL")
	private String lookupUrl;

	public Long getReportParameterId() {
		return reportParameterId;
	}

	public void setReportParameterId(Long reportParameterId) {
		this.reportParameterId = reportParameterId;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterCaption() {
		return parameterCaption;
	}

	public void setParameterCaption(String parameterCaption) {
		this.parameterCaption = parameterCaption;
	}

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getLookupUrl() {
		return lookupUrl;
	}

	public void setLookupUrl(String lookupUrl) {
		this.lookupUrl = lookupUrl;
	}

}
