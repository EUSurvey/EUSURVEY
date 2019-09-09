package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.*;

import java.text.Collator;
import java.util.*;

/**
 * Represents a matrix question in a survey
 */
@Entity
@DiscriminatorValue("MATRIX")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Matrix extends MatrixOrTable {
	
	private static final long serialVersionUID = 1L;
	private boolean isSingleChoice;
	private Boolean isInterdependent;
	private boolean useRadioButtons;
	private Integer order;
	private Integer minRows;
	private Integer maxRows;
	private boolean foreditor;
	
	private List<DependencyItem> dependentElements = new ArrayList<>();
	
	public Matrix(Survey survey, String ptitle, String shortname, String uid) {
		setTitle(ptitle);
		setShortname(shortname);
		setUniqueId(uid);
	}
	
	public Matrix() {
	}
	
	@Column(name = "QUESTIONORDER")
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}	
	
	@OneToMany(targetEntity=DependencyItem.class, cascade=CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "MATRIX_DEP")
	@Fetch(value = FetchMode.SELECT)
	@javax.persistence.OrderColumn(name="MATDEP_ID")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<DependencyItem> getDependentElements() {
		return dependentElements;
	}
	public void setDependentElements(List<DependencyItem> dependentElements) {
		this.dependentElements = dependentElements;
	}
		
	private Map<Integer, String> mapDependentElements = null;
	
	private String[] cachedDependentElementsStrings = null;
	
	@Transient
	public String[] getDependentElementsStrings()
	{
		if (cachedDependentElementsStrings != null) return cachedDependentElementsStrings;
				
		int maxposition = -1;
		if (mapDependentElements == null)
		{
			mapDependentElements = new HashMap<>();
			for (DependencyItem dep : dependentElements)
			{
				for (Element element: dep.getDependentElements())
				{
					if (mapDependentElements.containsKey(dep.getPosition()))
					{
						String old = mapDependentElements.get(dep.getPosition());
						mapDependentElements.put(dep.getPosition(), old + element.getId() + ";");
					} else {
						mapDependentElements.put(dep.getPosition(), element.getId() + ";");
					}
					if (dep.getPosition() > maxposition)
					{
						maxposition = dep.getPosition();
					}
				}
			}
		}
		if (mapDependentElements != null)
		{
			String[] result = new String[maxposition+1];
			for (int i = 0; i <= maxposition; i++) {
				result[i] = mapDependentElements.getOrDefault(i, "");
			}
			cachedDependentElementsStrings = result;
			return result;
		}
		
		String[] result = new String[1];
		result[0] = "";
		return result;
	}
	
	@Transient
	public String getDependentElementUIDsStrings()
	{
		StringBuilder result = new StringBuilder();

		for (DependencyItem dep : dependentElements)
		{
			for (Element element: dep.getDependentElements())
			{
				result.append(element.getUniqueId()).append(dep.getPosition());
			}
		}		
		
		return result.toString();
	}
	
	@Transient
	public String getDependentElementsString(Integer position)
	{		
		if (mapDependentElements == null)
		{
			mapDependentElements = new HashMap<>();
			for (DependencyItem dep : dependentElements)
			{
				for (Element element: dep.getDependentElements())
				{
					if (mapDependentElements.containsKey(dep.getPosition()))
					{
						String old = mapDependentElements.get(dep.getPosition());
						mapDependentElements.put(dep.getPosition(), old + element.getId() + ";");
					} else {
						mapDependentElements.put(dep.getPosition(), element.getId() + ";");
					}
				}
			}
		}		
		
		if (mapDependentElements.containsKey(position))
		{
			return mapDependentElements.get(position);
		}
		
		return "";
	}
	
	public Matrix copy(String fileDir) throws ValidationException, IntrusionException
	{
		Matrix copy = new Matrix();
		initCopy(copy, fileDir);
		copy.isSingleChoice = isSingleChoice;
		copy.isInterdependent = isInterdependent;
		copy.order = order;
		copy.minRows = minRows;
		copy.maxRows = maxRows;
		copy.setWidths(widths);
		copy.setTableType(tableType);
		
		return copy;
	}

	@Column(name = "MATRIXROWS")
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}

	@Column(name = "MATRIXCOLUMNS")
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	@Column(name = "MIN_ROWS")
	public Integer getMinRows() {
		return minRows;
	}	
	public void setMinRows(Integer minRows) {
		this.minRows = minRows;
	}
	
	@Column(name = "MAX_ROWS")
	public Integer getMaxRows() {
		return maxRows;
	}	
	public void setMaxRows(Integer maxRows) {
		this.maxRows = maxRows;
	}

	@Column(name = "MATRIXSINGLE")
	public boolean getIsSingleChoice() {
		return isSingleChoice;
	}
	public void setIsSingleChoice(boolean isSingleChoice) {
		this.isSingleChoice = isSingleChoice;
	}
	
	@Column(name = "MATRIXINTER")
	public Boolean getIsInterdependent() {
		return isInterdependent;
	}
	public void setIsInterdependent(Boolean isInterdependent) {
		this.isInterdependent = isInterdependent;
	}
	
	@Override
	public boolean differsFrom(Element element) {
		if (basicDiffersFrom(element)) return true;
		
		if (!(element instanceof Matrix)) return true;
		
		Matrix matrix = (Matrix)element;

		if (!(isSingleChoice == matrix.isSingleChoice)) return true;

		if (getChildElements().size() != matrix.getChildElements().size()) return true;
		
		for (int r = 0; r < getChildElements().size(); r++)
		{
			if (this.getChildElements().get(r).differsFrom(matrix.getChildElements().get(r))) return true;
		}
		
		if (!getDependentElementUIDsStrings().equals(matrix.getDependentElementUIDsStrings())) return true;
		
		if (!Objects.equals(minRows, matrix.minRows)) return true;
		if (!Objects.equals(maxRows, matrix.maxRows)) return true;
		
		if (!Objects.equals(order, matrix.order)) return true;
		
		if (!isInterdependent.equals(matrix.isInterdependent)) return true;
		
		return false;
	}

	public Boolean isUseRadioButtons() {
		return useRadioButtons;
	}

	public void setUseRadioButtons(Boolean useRadioButtons) {
		this.useRadioButtons = useRadioButtons != null && useRadioButtons;
	}
	
	@Transient
	public boolean getIsUseRadioButtons() {
		return useRadioButtons;
	}
	
	@Transient
	public String getCss()
	{
		String css = super.getCss();
		
		if (isInterdependent != null && isInterdependent && isSingleChoice)
		{
			css += " interdependent";
		}
		
		if (minRows != null && minRows > 0)
		{
			css += " minrows" + minRows;
		}
		
		if (maxRows != null && maxRows > 0)
		{
			css += " maxrows" + maxRows;
		}
		
		return css;
	}
	
	private List<Element> childElementsOrdered = null;
	
	@Transient
	public Collection<Element> getQuestionsOrdered()
	{
		if (foreditor) return getQuestions();
		
		if (order != null && order == 1)
		{
			final Collator instance = Collator.getInstance();
			Map<String, Element> elements = new TreeMap<>(instance);
			for (Element element: getQuestions())
			{
				if (elements.containsKey(element.getTitle()))
				{
					elements.put(element.getTitle() + elements.size(), element);
				} else {
					elements.put(element.getTitle(), element);
				}
			}
			
			return elements.values();
		} else if (order != null && order == 2)
		{
			List<Element> questionsOrdered = getQuestions();
			Collections.shuffle(questionsOrdered);
			return questionsOrdered;
		} else {
			return getQuestions();
		}
	}
	
	@Transient
	public List<Element> getChildElementsOrdered()
	{
		if (childElementsOrdered != null) return childElementsOrdered;		
		
		childElementsOrdered = new ArrayList<>();
		
		if (order != null && order > 0)
		{
			childElementsOrdered.add(getChildElements().get(0));
			childElementsOrdered.addAll(getAnswers());
			childElementsOrdered.addAll(getQuestionsOrdered());
			
			return childElementsOrdered;
		} else {
			childElementsOrdered = getChildElements();
			return childElementsOrdered;
		}
	}
	
	@Transient
	public boolean getAllQuestionsDependent()
	{
		for (Element element: getQuestions())
		{
			if (!element.getIsDependent())	return false;
		}
		return true;
	}

	@Transient
	public boolean isForeditor() {
		return foreditor;
	}
	public void setForeditor(boolean foreditor) {
		this.foreditor = foreditor;
	}

	public Element getChildByUniqueId(String uid) {
		for (Element child : getChildElements())
		{
			if (child.getUniqueId().equals(uid)) return child;
		}
		return null;
	}
}
