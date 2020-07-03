package com.ec.survey.model;

public class DepartmentItem {

	public DepartmentItem() {

	}

	public DepartmentItem(String domainCode, String name) {
		super();
		this.domainCode = domainCode;
		this.name = name;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String domainCode;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domainCode == null) ? 0 : domainCode.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DepartmentItem other = (DepartmentItem) obj;
		if (domainCode == null) {
			if (other.domainCode != null)
				return false;
		} else if (!domainCode.equals(other.domainCode)) {
			return false;
		}
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	private String name;
}
