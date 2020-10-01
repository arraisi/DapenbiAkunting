package id.co.dapenbi.basicsetup.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "BSE_AUTO_NUMBER_LOG")
public class AutoNumberLog {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BSE_AUTO_NUMBER_LOG_SEQ")
	@SequenceGenerator(name = "BSE_AUTO_NUMBER_LOG_SEQ", sequenceName = "BSE_AUTO_NUMBER_LOG_SEQ", allocationSize = 1)
	@Column(name = "AUTO_NUMBER_LOG_ID")
	private Long autoNumberLogId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "AUTO_NUMBER_ID", nullable = false)
	private AutoNumber autoNumber;

	@Column(name = "DATE_LOG")
	private Date dateLog;

	@Column(name = "LAST_INCREMENT")
	private Long lastIncrement;

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

	public Long getLastIncrement() {
		return lastIncrement;
	}

	public void setLastIncrement(Long lastIncrement) {
		this.lastIncrement = lastIncrement;
	}

}
