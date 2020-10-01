package id.co.dapenbi.basicsetup.finder;

import java.util.Date;

import id.co.dapenbi.basicsetup.model.AutoNumber;

public class AutoNumberLogFinder {
	private Long autoNumberLogId;
	private AutoNumber autoNumber;
	private Date dateLog;
	private String orderByMode;

	public Long getAutoNumberLogId() {
		return autoNumberLogId;
	}

	public void setAutoNumberLogId(Long autoNumberLogId) {
		this.autoNumberLogId = autoNumberLogId;
	}

	public AutoNumber getAutoNumber() {
		return autoNumber;
	}

	public void setAutoNumber(AutoNumber autoNumber) {
		this.autoNumber = autoNumber;
	}

	public Date getDateLog() {
		return dateLog;
	}

	public void setDateLog(Date dateLog) {
		this.dateLog = dateLog;
	}

	public String getOrderByMode() {
		return orderByMode;
	}

	public void setOrderByMode(String orderByMode) {
		this.orderByMode = orderByMode;
	}

}
