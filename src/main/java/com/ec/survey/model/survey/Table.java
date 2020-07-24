package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Objects;

/**
 * Represents a table question in a survey
 */
@Entity
@DiscriminatorValue("TABLE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Table extends MatrixOrTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Table(String ptitle, String shortname, String uid) {
		setTitle(ptitle);
		setShortname(shortname);
		setUniqueId(uid);
	}
	
	public Table() {
	}	
	
	public Table copy(String fileDir) throws ValidationException
	{
		Table copy = new Table();
		initCopy(copy, fileDir);	
		copy.setWidths(widths);
		copy.setTableType(tableType);
		return copy;
	}
	
	@Column(name = "TABLEROWS")
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}

	@Column(name = "TABLECOLUMNS")
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element)) return true;
		
		if (!(element instanceof Table)) return true;
		
		Table table = (Table)element;
		
		if (!Objects.equals(tableType, table.tableType)) return true;
		
		for (int r = 0; r < getChildElements().size(); r++)
		{
			if (this.getChildElements().get(r).differsFrom(table.getChildElements().get(r))) return true;
		}
		
		return false;
	}
	
}
