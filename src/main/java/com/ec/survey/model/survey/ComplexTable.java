package com.ec.survey.model.survey;

import java.util.*;
import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.owasp.esapi.errors.ValidationException;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This is a class for the complex table
 */
@Entity
@DiscriminatorValue("COMPLEXTABLE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ComplexTable extends Question {
	
	public enum SizeType {
		FitToContent,
		FitToPage;
		
		@JsonValue
	    public int toValue() {
	        return ordinal();
	    }
	}

	private static final long serialVersionUID = 1L;
	private int rows;
	private int columns;
	private boolean showHeadersAndBorders;
	private SizeType size;
	private List<ComplexTableItem> childElements = new ArrayList<>();
	private List<ComplexTableItem> missingChildElements = new ArrayList<>();
	
	public ComplexTable() {}
	
	public ComplexTable(String title, String shortname, String uid) {
		super(title, shortname, uid);
	}

	@OneToMany(targetEntity=ComplexTableItem.class, cascade = CascadeType.ALL)
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(nullable=true, foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT))
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<ComplexTableItem> getChildElements() {
		return childElements;
	}
	public void setChildElements(List<ComplexTableItem> childElements) {
		this.childElements = childElements;	
	}

	@Column(name = "COMPLEXTABLEROWS")
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}

	@Column(name = "COMPLEXTABLECOLUMNS")
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	@Column(name = "COMPLEXTABLESHOWHEADERS")
	public boolean isShowHeadersAndBorders() {
		return showHeadersAndBorders;
	}
	public void setShowHeadersAndBorders(boolean showHeadersAndBorders) {
		this.showHeadersAndBorders = showHeadersAndBorders;
	}
	
	@Column(name = "COMPLEXTABLESIZE")
	public SizeType getSize() {
		return size;
	}
	public void setSize(SizeType size) {
		this.size = size;
	}
	
	public ComplexTable copy(String fileDir) throws ValidationException {
		ComplexTable copy = new ComplexTable();
		baseCopy(copy);
		
		copy.setColumns(columns);
		copy.setRows(rows);
		copy.setShowHeadersAndBorders(showHeadersAndBorders);
		copy.setSize(size);
		
		for (ComplexTableItem thatElement : getChildElements()) {
			ComplexTableItem elementCopy = thatElement.copy(fileDir);
			copy.getChildElements().add(elementCopy);
		}

		return copy;
	}

	@Transient
	@Override
	public String getCss()
	{
		String css = super.getCss();
		css += " complextable";
		return css.trim();
	}

	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element))
			return true;

		if (!(element instanceof ComplexTable))
			return true;

		ComplexTable other = (ComplexTable) element;

		if (rows != other.rows) return true;
		if (columns != other.columns) return true;
		if (showHeadersAndBorders != other.showHeadersAndBorders) return true;
		if (size != other.size) return true;
		
		if (getChildElements().size() != other.getChildElements().size())
			return true;
		
		for (int r = 0; r < getChildElements().size(); r++)
		{
			if (this.getChildElements().get(r).differsFrom(other.getChildElements().get(r))) return true;
		}
				
		return false;
	}
	
	@Transient
	public List<ComplexTableItem> getMissingChildElements() {
		return missingChildElements;
	}
	public void setMissingChildElements(List<ComplexTableItem> missingChildElements) {
		this.missingChildElements = missingChildElements;
	}
	
	@Transient
	public List<ComplexTableItem> getOrderedChildElements() {
		List<ComplexTableItem> result = new ArrayList<>();
		
		for (int r = 0; r <= this.rows; r++) {
			for (int c = 0; c <= this.columns; c++) {
				ComplexTableItem child = getChildAt(r, c);
				if (child != null) {
					result.add(child);
				}
			}
		}
		
		return result;
	}
	
	@Transient 
	public List<ComplexTableItem> getQuestionChildElements() {
		List<ComplexTableItem> result = new ArrayList<>();
		for (int r = 1; r <= this.rows; r++) {
			for (int c = 1; c <= this.columns; c++) {
				ComplexTableItem child = getChildAt(r, c);
				if (child != null && child.getCellType() != ComplexTableItem.CellType.Empty && child.getCellType() != ComplexTableItem.CellType.StaticText) {
					result.add(child);
				}
			}
		}
		for (ComplexTableItem child : missingChildElements) {
			if (child != null && child.getCellType() != ComplexTableItem.CellType.Empty && child.getCellType() != ComplexTableItem.CellType.StaticText) {
				result.add(child);
			}
		}
		return result;
	}
	
	@Transient
	public ComplexTableItem getChildAt(int row, int column) {
		for (ComplexTableItem child : getChildElements()) {
			if (child.getRow() == row && child.getColumn() == column) {
				return child;
			}
		}
		return null;
	}
	
	public boolean containsChild(int id)
	{
		for (Element element :  getQuestionChildElements())
		{
			if (element.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}
	
	@Transient
	public boolean isCellVisible(int col, int row) {
		if (col == 1) return true;
		
		for (int i = 1; i < this.columns; i++) {
			if (col > i) {
				ComplexTableItem previous = this.getChildAt(row, col-i);
				if (previous != null) {
					return previous.getColumnSpan() < (i+1);
				}
			} else {
				return true;
			}
		}
		
		return true;
	}
}
