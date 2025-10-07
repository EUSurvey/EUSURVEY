package com.ec.survey.model.survey;

import com.ec.survey.tools.ConversionTools;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the base class for all survey elements like questions or sections.
 * All instances are saved in the table ELEMENTS. Each subclass has its own
 * value for the "type" column.
 */
@Entity
@Table(name = "ELEMENTS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class Element implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String uid;
	private String oldId;
	private String shortname;
	private Integer sourceId;
	private Survey survey;
	private Integer position;
	private String title;
	private String originalTitle;
	private boolean isDummy;
	private String triggers = "";
	private boolean hasPDFWidth = false;
	private float pdfWidth = -1;
	private Boolean locked;
	private String subType = "";
	private Integer displayMode;
	private Boolean useAndLogic = false;
	private boolean editorRowsLocked = false; //Rows + Possible Answer
	private boolean editorColumnsLocked = false; //Columns

	private Map<Integer, String[]> activitiesToLog = new HashMap<>();

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ELEM_UID")
	public String getUniqueId() {
		return uid;
	}

	public void setUniqueId(String uid) {
		this.uid = uid;
	}

	@Transient
	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}

	@Column(name = "ELEM_SHORTNAME")
	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	@Column(name = "SOURCE_ID")
	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	@Column(name = "EPOSITION")
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Lob
	@Column(name = "ETITLE", length = 40000)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "ELOCKED")
	public Boolean getLocked() {
		return locked != null && locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked != null && locked;
	}

	@Column(name = "SUBTYPE")
	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType != null ? subType : "";
	}

	@Column(name = "DISPLAYMODE")
	public Integer getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(Integer displayMode) {
		this.displayMode = displayMode != null ? displayMode : 0;
	}
	
	@Column(name = "ANDLOGIC")
	public Boolean getUseAndLogic() {
		return useAndLogic;
	}

	public void setUseAndLogic(Boolean useAndLogic) {
		this.useAndLogic = useAndLogic != null ? useAndLogic : false;
	}

	@Column(name = "EDIT_ROWS_LOCKED")
	public Boolean getEditorRowsLocked() {
		return editorRowsLocked;
	}

	public void setEditorRowsLocked(Boolean rowsLocked) {
		this.editorRowsLocked = rowsLocked != null ? rowsLocked : false;
	}

	@Column(name = "EDIT_COLUMNS_LOCKED")
	public Boolean getEditorColumnsLocked() {
		return editorColumnsLocked;
	}

	public void setEditorColumnsLocked(Boolean columnsLocked) {
		this.editorColumnsLocked = columnsLocked != null ? columnsLocked : false;
	}

	@Transient
	public String getOriginalTitle() {
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}

	@Transient
	public boolean getHasPDFWidth() {
		return hasPDFWidth;
	}

	@Transient
	public void setHasPDFWidth(boolean b) {
		hasPDFWidth = b;
	}

	@Transient
	public float getPDFWidth() {
		return pdfWidth;
	}

	@Transient
	public void setPDFWidth(float w) {
		pdfWidth = w;
	}

	@Transient
	public String getStrippedTitle() {
		if (title != null && title.length() > 0) {
			return ConversionTools.removeHTML(title, true).replace("\"", "'");
		}

		return "";
	}

	@Transient
	public String getStrippedTitleNoEscape() {
		if (title != null && title.length() > 0) {
			return ConversionTools.removeHTML(title, false).replace("\"", "'");
		}

		return "";
	}

	@Transient
	public String getStrippedTitleNoEscape2() {
		if (title != null && title.length() > 0) {
			return ConversionTools.removeHTMLNoEscape(title).replace("\"", "'");
		}

		return "";
	}

	@Transient
	public String getStrippedTitleAtMost100() {
		String strippedTitle = getStrippedTitle();
		if (strippedTitle.length() > 100)
			return strippedTitle.substring(0, 100) + "...";

		return strippedTitle;
	}

	@Transient
	public String getType() {
		return this.getClass().getSimpleName();
	}

	@Transient
	public String getNameOrType() {
		if (title != null && title.length() > 0)
			return title;
		return getType();
	}

	@Transient
	public String getNameOrTypeStripped() {
		String strippedTitle = getStrippedTitle();
		if (strippedTitle != null && strippedTitle.length() > 0)
			return strippedTitle;
		return getType();
	}

    @Transient
    public String getNameOrTypeStrippedAtMost100() {
        String strippedTitle = getNameOrTypeStripped();
        if (strippedTitle.length() > 100)
            return strippedTitle.substring(0, 100) + "...";
        return strippedTitle;
    }

	private Boolean isIsDependentMatrixQuestion = null;

	@Transient
	public boolean getIsDependentMatrixQuestion() {
		if (isIsDependentMatrixQuestion != null)
			return isIsDependentMatrixQuestion;

		if (getIsDependent()) {
			return true;
		}

		if (survey != null) {
			for (Element element : survey.getElements()) {
				if (element instanceof Matrix) {
					Matrix matrix = (Matrix) element;
					if (matrix.getQuestions().contains(this)) {
						return matrix.getIsDependent();
					}
				}
			}
		}

		return false;
	}

	public void presetIsDependentMatrixQuestion(Survey survey) {
		if (survey != null) {
			for (Element element : survey.getElements()) {
				if (element instanceof ChoiceQuestion) {
					for (PossibleAnswer p : ((ChoiceQuestion) element).getPossibleAnswers()) {
						if (p.getDependentElements().getDependentElements().contains(this)) {
							isIsDependentMatrixQuestion = true;
							return;
						}
					}
				} else if (element instanceof Matrix) {
					for (DependencyItem dep : ((Matrix) element).getDependentElements()) {
						if (dep != null && dep.getDependentElements().contains(this)) {
							isIsDependentMatrixQuestion = true;
							return;
						}
					}
				}
			}
		}

		if (getIsDependent(survey)) {
			isIsDependentMatrixQuestion = true;
			return;
		}

		if (survey != null) {
			for (Element element : survey.getElements()) {
				if (element instanceof Matrix) {
					Matrix matrix = (Matrix) element;
					if (matrix.getQuestions().contains(this)) {
						isIsDependentMatrixQuestion = matrix.getIsDependent(survey);
						return;
					}
				}
			}
		}

		isIsDependentMatrixQuestion = false;
	}

	@Transient
	public boolean getIsDependent() {
		return getIsDependent(this.getSurvey());
	}

	@Transient
	public boolean getIsDependent(Survey survey) {
		if (survey != null) {
			for (Element element : survey.getElements()) {
				if (element instanceof ChoiceQuestion) {
					for (PossibleAnswer p : ((ChoiceQuestion) element).getPossibleAnswers()) {
						if (p.getDependentElements().getDependentElements().contains(this)) {
							return true;
						}
					}
				} else if (element instanceof Matrix) {
					for (DependencyItem dep : ((Matrix) element).getDependentElements()) {
						if (dep != null && dep.getDependentElements().contains(this)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}
	
	@Transient
	public boolean getIsTriggerOrDependent() {
		if (this instanceof ChoiceQuestion) {
			for (PossibleAnswer p : ((ChoiceQuestion) this).getPossibleAnswers()) {
				if (!p.getDependentElements().getDependentElements().isEmpty()) {
					return true;
				}
			}
		} else if (this instanceof Matrix) {
			for (DependencyItem dep : ((Matrix) this).getDependentElements()) {
				if (dep != null && !dep.getDependentElements().isEmpty()) {
					return true;
				}
			}
		}
		
		return getIsDependent();
	}

	@Transient
	public String getTriggersMatrixQuestion() {
		StringBuilder result = new StringBuilder(triggers);

		if (survey != null && survey.getElements() != null) {
			for (Element element : survey.getElements()) {
				if (element instanceof Matrix) {
					Matrix matrix = (Matrix) element;
					if (matrix.getQuestions().contains(this)) {
						String parentTriggers = matrix.getTriggers();
						result.append(parentTriggers);
					}
				}
			}
		}

		return result.toString();
	}

	@Transient
	public String getTriggers() {
		return triggers;
	}

	public void setTriggers(String triggers) {
		this.triggers = triggers;
	}

	@Transient
	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey s) {
		this.survey = s;
	}

	public abstract Element copy(String fileDir) throws ValidationException;

	public abstract boolean differsFrom(Element element);

	@Transient
	protected boolean basicDiffersFrom(Element element) {
		if (shortname != null && !shortname.equals(element.shortname))
			return true;
		if (position != null && !position.equals(element.position))
			return true;
		if (useAndLogic != null && !useAndLogic.equals(element.useAndLogic))
			return true;
		return (title != null && !title.equals(element.title));
	}

	@Transient
	public Map<Integer, String[]> getActivitiesToLog() {
		return this.activitiesToLog;
	}

	public void setActivitiesToLog(Map<Integer, String[]> activitiesToLog) {
		this.activitiesToLog = activitiesToLog;
	}

	@Transient
	public boolean isDummy() {
		return isDummy;
	}

	public void setDummy(boolean isDummy) {
		this.isDummy = isDummy;
	}

	@Transient
	public boolean isUsedInResults() {
		return !(this instanceof Ruler || this instanceof Confirmation || this instanceof Image
				|| this instanceof Download || this instanceof Text
				|| (this instanceof GalleryQuestion && !((GalleryQuestion) this).getSelection()));
	}	
	
	@Transient
	public boolean isDelphiElement() {
		if (this instanceof Question)
		{
			return ((Question)this).getIsDelphiQuestion();
		}
		return false;
	}

	@Transient
	public boolean isElementHidden() {
		//Used for PDFs
		if (this instanceof Question) {
			return ((Question) this).getHidden();
		}
		return false;
	}
}
