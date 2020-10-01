package id.co.dapenbi.basicsetup.model;

import javax.persistence.*;

@Entity
@Table(name = "BSE_AUTO_NUMBER")
public class AutoNumber {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BSE_AUTO_NUMBER_SEQ")
	@SequenceGenerator(name = "BSE_AUTO_NUMBER_SEQ", sequenceName = "BSE_AUTO_NUMBER_SEQ", allocationSize = 1)
	@Column(name = "AUTO_NUMBER_ID")
	private Long autoNumberId;
	
	@Column(name = "TRANSACTION_CODE")
	private String transactionCode;
	
	@Column(name = "NUMBERING_TYPE")
	private String numberingType;
	
	@Column(name = "PREFIX")
	private String prefix;
	
	@Column(name = "SEPARATOR")
	private String separator;
	
	@Column(name = "DATE_FORMAT")
	private String dateFormat;
	
	@Column(name = "INCREMENT_DIGIT")
	private Long incrementDigit;
	
	@Column(name = "INCREMENT_RESET_MODE")
	private String incrementResetMode;
	
	@Column(name = "USE_COMPANY_CODE")
	private Boolean useCompanyCode;

	public Long getAutoNumberId() {
		return autoNumberId;
	}

	public void setAutoNumberId(Long autoNumberId) {
		this.autoNumberId = autoNumberId;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public String getNumberingType() {
		return numberingType;
	}

	public void setNumberingType(String numberingType) {
		this.numberingType = numberingType;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Long getIncrementDigit() {
		return incrementDigit;
	}

	public void setIncrementDigit(Long incrementDigit) {
		this.incrementDigit = incrementDigit;
	}

	public String getIncrementResetMode() {
		return incrementResetMode;
	}

	public void setIncrementResetMode(String incrementResetMode) {
		this.incrementResetMode = incrementResetMode;
	}

	public Boolean getUseCompanyCode() {
		return useCompanyCode;
	}

	public void setUseCompanyCode(Boolean useCompanyCode) {
		this.useCompanyCode = useCompanyCode;
	}
	
	
}
