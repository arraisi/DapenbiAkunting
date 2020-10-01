package id.co.dapenbi.basicsetup.finder;

import id.co.dapenbi.basicsetup.model.Company;
import id.co.dapenbi.basicsetup.model.Lookup;

public class LookupItemFinder {
	private Long lookupItemId;
	private String lookupItemCode;
	private String lookupItemName;
	private Lookup lookup;
	private Company company;

	public Long getLookupItemId() {
		return lookupItemId;
	}

	public void setLookupItemId(Long lookupItemId) {
		this.lookupItemId = lookupItemId;
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

	public Lookup getLookup() {
		return lookup;
	}

	public void setLookup(Lookup lookup) {
		this.lookup = lookup;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
