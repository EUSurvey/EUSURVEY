package com.ec.survey.model;

import org.springframework.util.comparator.NullSafeComparator;

import java.util.Comparator;

public class LdapSearchResult {

	public LdapSearchResult(String login, String displayName, String organisation, String group, String fname, String lname) {
		super();
		this.login = login;
		this.displayName = displayName;
		this.organisation = organisation;
		this.group = group;
		this.fname = fname;
		this.lname = lname;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getOrganisation() {
		return organisation;
	}
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	private String login ;
	private String displayName ;
	private String organisation ;
	private String group ;
	private String fname ;
	private String lname ;
		
	public static class Comparators {
        public static final Comparator<LdapSearchResult> FIRST = Comparator.comparing(o -> o.fname);
        public static final Comparator<LdapSearchResult> LAST = Comparator.comparing(o -> o.lname);
        public static final Comparator<LdapSearchResult> GROUP = (o1, o2) -> {
            Comparator<String> comp = new NullSafeComparator<>(String.CASE_INSENSITIVE_ORDER, false);
            return comp.compare(o1.group,o2.group);
        };
        public static final Comparator<LdapSearchResult> DISPLAYNAME = Comparator.comparing(o -> o.displayName);
    }
	
}

