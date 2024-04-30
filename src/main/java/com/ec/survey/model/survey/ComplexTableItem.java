package com.ec.survey.model.survey;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.owasp.esapi.errors.ValidationException;

import com.ec.survey.tools.ElementHelper;
import com.ec.survey.tools.Tools;
import com.fasterxml.jackson.annotation.JsonValue;
import com.mysql.cj.util.StringUtils;

/**
 * ComplexTableItem represents a child element in a ComplexTable
 */
@Entity
@DiscriminatorValue("COMPLEXTABLEITEM")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ComplexTableItem extends Question {

    public enum CellType {
        Empty,
        StaticText,
        FreeText,
        Formula,
        SingleChoice,
        MultipleChoice,
        Number;

        @JsonValue
        public int toValue() {
            return ordinal();
        }
    }

    private static final long serialVersionUID = 1L;
    private CellType cellType;
    private int row;
    private int column;
    private int columnSpan = 1; //this is the cell size
    private int minCharacters; //used in freetext cells
    private int maxCharacters;
    private int minChoices; //used in multiple choice cells
    private int maxChoices;
    private int numRows;
    private List<PossibleAnswer> possibleAnswers = new ArrayList<>();
    private List<PossibleAnswer> missingPossibleAnswers = new ArrayList<>();
    private boolean useRadioButtons;
    private boolean useCheckboxes;
    private int numColumns = 1; //this is the number of columns (e.g. of check boxes) inside a cell
    private int order; //original, alphabetical, random
    private String resultText;
    private int decimalPlaces;
    private String unit;
    private Double minD; //used in number and formula cells
    private Double maxD;
    private String formula;

    public ComplexTableItem(String title, String originaltitle, String shortname, String uid) {
        setTitle(title);
        setOriginalTitle(originaltitle);
        setShortname(shortname);
        setUniqueId(uid);
    }

    public ComplexTableItem() {
    }

    @Column(name = "CELLTYPE")
    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    @Column(name = "RESULTTEXT")
    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    @Column(name = "CELLROW")
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Column(name = "CELLCOLUMN")
    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Column(name = "CELLCOLUMNSPAN")
    public int getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
    }

    @Column(name = "MINCHARS")
    public Integer getMinCharacters() {
        return minCharacters;
    }

    public void setMinCharacters(Integer min) {
        this.minCharacters = min;
    }

    @Column(name = "MAXCHARS")
    public Integer getMaxCharacters() {
        return maxCharacters;
    }

    public void setMaxCharacters(Integer max) {
        this.maxCharacters = max;
    }

    @Column(name = "MIN_CHOICES")
    public int getMinChoices() {
        return minChoices;
    }

    public void setMinChoices(int minChoices) {
        this.minChoices = minChoices;
    }

    @Column(name = "MAX_CHOICES")
    public int getMaxChoices() {
        return maxChoices;
    }

    public void setMaxChoices(int maxChoices) {
        this.maxChoices = maxChoices;
    }

    @Column(name = "NUMROWS")
    public Integer getNumRows() {
        return numRows;
    }

    public void setNumRows(Integer num) {
        this.numRows = num;
    }

    @OneToMany(targetEntity = PossibleAnswer.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
            name = "ELEMENTS_ELEMENTS",
            joinColumns = @JoinColumn(name = "ELEMENTS_ID"),
            inverseJoinColumns = @JoinColumn(name = "possibleAnswers_ID"))
    @Fetch(value = FetchMode.SELECT)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OrderBy(value = "position asc")
    public List<PossibleAnswer> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(List<PossibleAnswer> answers) {
        this.possibleAnswers = answers;
    }

    private Collection<PossibleAnswer> orderedPossibleAnswers = null;

    private boolean forEditor;

    @Transient
    public List<PossibleAnswer> getMissingPossibleAnswers() {
        return missingPossibleAnswers;
    }

    public void setMissingPossibleAnswers(List<PossibleAnswer> missingPossibleAnswers) {
        this.missingPossibleAnswers = missingPossibleAnswers;
    }

    @Transient
    public List<PossibleAnswer> getAllPossibleAnswers() {

        if (!missingPossibleAnswers.isEmpty()) {
            List<PossibleAnswer> result = new ArrayList<>();
            for (PossibleAnswer pa : missingPossibleAnswers) {
                if (!result.contains(pa)) {
                    result.add(pa);
                }
            }
            for (PossibleAnswer pa : possibleAnswers) {
                if (!result.contains(pa)) {
                    result.add(pa);
                }
            }

            result.sort(Survey.newElementByPositionComparator());

            return result;
        } else {
            return possibleAnswers;
        }
    }

    @Transient
    public Collection<PossibleAnswer> getOrderedPossibleAnswers() {
        if (forEditor) return possibleAnswers;

        if (orderedPossibleAnswers != null) return orderedPossibleAnswers;
        orderedPossibleAnswers = ElementHelper.getOrderedPossibleAnswers(getAllPossibleAnswers(), order, numColumns);
        return orderedPossibleAnswers;
    }

    @Column(name = "RADIO")
    public boolean getUseRadioButtons() {
        return useRadioButtons;
    }

    public void setUseRadioButtons(boolean useRadioButtons) {
        this.useRadioButtons = useRadioButtons;
    }

    @Column(name = "CHECKBOXES")
    public boolean getUseCheckboxes() {
        return useCheckboxes;
    }

    public void setUseCheckboxes(boolean useCheckboxes) {
        this.useCheckboxes = useCheckboxes;
    }

    @Column(name = "NUMCOLUMNS")
    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    @Column(name = "CHOICEORDER")
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Column(name = "DECIMALPLACES")
    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    @Column(name = "MINNUMBER")
    public Double getMin() {
        return minD;
    }

    public void setMin(Double minD) {
        this.minD = minD;
    }

    @Column(name = "MAXNUMBER")
    public Double getMax() {
        return maxD;
    }

    public void setMax(Double maxD) {
        this.maxD = maxD;
    }

    @Transient
    public String getMinString() {
        if (minD == null) return "";
        String s = String.valueOf(minD);
        if (s.endsWith(".0")) s = s.replace(".0", "");
        return s;
    }

    @Transient
    public String getMaxString() {
        if (maxD == null) return "";
        String s = String.valueOf(maxD);
        if (s.endsWith(".0")) s = s.replace(".0", "");
        return s;
    }

    @Column(name = "NUMBERUNIT")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Column(name = "FORMULA")
    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Transient
    public String getResultTitle(ComplexTable parent) {
        if (!StringUtils.isNullOrEmpty(this.resultText)) {
            return this.resultText;
        }

        //Table name : Row name : Column name
        ComplexTableItem row = parent.getChildAt(0, this.column);
        ComplexTableItem column = parent.getChildAt(this.row, 0);

		String columnPart = column == null ? "-" : valueOrDash(column.getStrippedTitle());
		String rowPart = row == null ? "-" : valueOrDash(row.getStrippedTitle());
		
		return valueOrDash(parent.getStrippedTitle()) + " : " + columnPart + " : " + rowPart;	
    }

    private String valueOrDash(String value) {
        if (value == null || value.trim().length() == 0) {
            return "-";
        }
        return value;
    }


    public ComplexTableItem copy(String fileDir) throws ValidationException {
        ComplexTableItem copy = new ComplexTableItem(getTitle(), getOriginalTitle(), getShortname(), getUniqueId());
        baseCopy(copy);
        copy.setCellType(cellType);
        copy.setResultText(resultText);
        copy.setRow(row);
        copy.setColumn(column);
        copy.setColumnSpan(columnSpan);
        copy.setMinCharacters(minCharacters);
        copy.setMaxCharacters(maxCharacters);
        copy.setMinChoices(minChoices);
        copy.setMaxChoices(maxChoices);
        copy.setNumRows(numRows);
        copy.setUseRadioButtons(useRadioButtons);
        copy.setUseCheckboxes(useCheckboxes);
        copy.setNumColumns(numColumns);
        copy.setOrder(order);
        copy.setDecimalPlaces(decimalPlaces);
        copy.setMax(maxD);
        copy.setMin(minD);
        copy.setUnit(unit);
        copy.setFormula(formula);

        for (PossibleAnswer possibleAnswer : getPossibleAnswers()) {
            PossibleAnswer answerCopy = possibleAnswer.copy(fileDir);
            copy.getPossibleAnswers().add(answerCopy);
        }

        return copy;
    }

    @Override
    public boolean differsFrom(Element element) {

        if (basicDiffersFrom(element)) return true;

        if (!(element instanceof ComplexTableItem))
            return true;

        ComplexTableItem other = (ComplexTableItem) element;

        if (row != other.row) return true;
        if (column != other.column) return true;
        if (columnSpan != other.columnSpan) return true;
        if (cellType != other.cellType) return true;
        if (minCharacters != other.minCharacters) return true;
        if (maxCharacters != other.maxCharacters) return true;
        if (minChoices != other.minChoices) return true;
        if (maxChoices != other.maxChoices) return true;
        if (numRows != other.numRows) return true;
        if (useRadioButtons != other.useRadioButtons) return true;
        if (useCheckboxes != other.useCheckboxes) return true;
        if (numColumns != other.numColumns) return true;
        if (order != other.order) return true;
        if (!Tools.isEqual(resultText, other.resultText)) return true;
        if (getDecimalPlaces() != null && !getDecimalPlaces().equals(other.getDecimalPlaces())) return true;
        if (!Tools.isEqual(maxD, other.maxD)) return true;
        if (!Tools.isEqual(minD, other.minD)) return true;
        if (!Tools.isEqual(unit, other.unit)) return true;
        if (!Tools.isEqual(formula, other.formula)) return true;

        for (int i = 0; i < getPossibleAnswers().size(); i++) {
            if (getPossibleAnswers().get(i).differsFrom(other.getPossibleAnswers().get(i))) {
                return true;
            }
        }

        return false;
    }

    @Transient
    @Override
    public String getCss() {
        String css = super.getCss() + " complex";

        switch (this.getCellType()) {
            case FreeText:
                css += " freetext";

                if (minCharacters > 0) {
                    css += " min" + minCharacters;
                }

                if (maxCharacters > 0) {
                    css += " max" + maxCharacters;
                } else {
                    css += " max5000";
                }
                break;
            case Number:
            case Formula:
                css += this.getCellType() == CellType.Number ? " number" : " formula";

                if (minD != null && minD > 0) {
                    css += " min" + minD;
                }

                if (maxD != null && maxD > 0) {
                    css += " max" + maxD;
                }

                break;
            case SingleChoice:
                css += " single-choice";
                break;
            case MultipleChoice:
                css += " multiple-choice";

                if (minChoices > 0) {
                    css += " min" + minChoices;
                }

                if (maxChoices > 0) {
                    css += " max" + maxChoices;
                }

                if (useCheckboxes) {
                    css += " checkboxes";
                } else {
                    css += " listbox";
                }

                break;
            default:
                break;
        }


        return css;
    }

    @Transient
    public PossibleAnswer getPossibleAnswer(int id) {
        for (PossibleAnswer possibleAnswer : getPossibleAnswers()) {
            if (possibleAnswer.getId() == id) {
                return possibleAnswer;
            }
        }
        return null;
    }

    @Transient
    public List<String> getPossibleNumberAnswers() {
        List<String> answers = new ArrayList<>();

        if (getCellType() == CellType.Number || getCellType() == CellType.Formula) {

            if (!showStatisticsForNumberQuestion()) {
                return answers;
            }

            NumberFormat nf = DecimalFormat.getInstance();
            nf.setMaximumFractionDigits(0);

            double v = minD;
            while (v <= maxD) {
                answers.add(nf.format(v));
                v++;
            }
        }

        return answers;
    }

    @Transient
    public boolean showStatisticsForNumberQuestion() {
        if (decimalPlaces > 0 || minD == null || maxD == null) {
            return false;
        }

        return (maxD - minD) <= 10;
    }

    @Transient
    public PossibleAnswer getPossibleAnswerByUniqueId(String uid) {
        for (PossibleAnswer possibleAnswer : getPossibleAnswers()) {
            if (possibleAnswer.getUniqueId() != null && possibleAnswer.getUniqueId().length() > 0 && possibleAnswer.getUniqueId().equals(uid)) {
                return possibleAnswer;
            }
        }
        return null;
    }

    @Transient
    public boolean isChoice() {
        return cellType == CellType.MultipleChoice || cellType == CellType.SingleChoice;
    }

    @Transient
    public String getAnswerWithPrefix(String answer) {
        return getId() + answer;
    }

    @Transient
    public boolean getForEditor() {
        return forEditor;
    }

    public void setForEditor(boolean forEditor) {
        this.forEditor = forEditor;
    }
}
