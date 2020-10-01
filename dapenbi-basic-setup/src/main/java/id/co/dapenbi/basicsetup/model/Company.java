package id.co.dapenbi.basicsetup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BSE_COMPANY")
public class Company {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BSE_COMPANY_SEQ")
	@Column(name = "COMPANY_ID")
	private Long companyId;
	
	@Column(name = "COMPANY_CODE")
	private String companyCode;
	
	@Column(name = "COMPANY_NAME")
	private String companyName;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
