package id.co.dapenbi.basicsetup.finder;

import id.co.dapenbi.basicsetup.model.Report;

public class ReportParameterFinder {
	private Long reportParameterId;
	private Report report;

	public Long getReportParameterId() {
		return reportParameterId;
	}

	public void setReportParameterId(Long reportParameterId) {
		this.reportParameterId = reportParameterId;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

}
