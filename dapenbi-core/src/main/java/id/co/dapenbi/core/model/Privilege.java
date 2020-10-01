package id.co.dapenbi.core.model;

public class Privilege {
	private boolean flagRead;
	private boolean flagEdit;
	private boolean flagDelete;
	private boolean flagValidation;
	private boolean flagPrint;

	public boolean isFlagRead() {
		return flagRead;
	}

	public void setFlagRead(boolean flagRead) {
		this.flagRead = flagRead;
	}

	public boolean isFlagEdit() {
		return flagEdit;
	}

	public void setFlagEdit(boolean flagEdit) {
		this.flagEdit = flagEdit;
	}

	public boolean isFlagDelete() {
		return flagDelete;
	}

	public void setFlagDelete(boolean flagDelete) {
		this.flagDelete = flagDelete;
	}

	public boolean isFlagValidation() {
		return flagValidation;
	}

	public void setFlagValidation(boolean flagValidation) {
		this.flagValidation = flagValidation;
	}

	public boolean isFlagPrint() {
		return flagPrint;
	}

	public void setFlagPrint(boolean flagPrint) {
		this.flagPrint = flagPrint;
	}

}
