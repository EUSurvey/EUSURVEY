package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.List;

public class DelphiTable {
    private List<DelphiTableEntry> entries = new ArrayList<>();
    private boolean hasNewComments;
    private int offset;
    private int total;
    private boolean showExplanationBox;

    public List<DelphiTableEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<DelphiTableEntry> entries) {
        this.entries = entries;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

	public boolean getShowExplanationBox() {
		return showExplanationBox;
	}

	public void setShowExplanationBox(boolean showExplanationBox) {
		this.showExplanationBox = showExplanationBox;
	}

    public boolean getHasNewComments() {
        return hasNewComments;
    }

    public void setHasNewComments(boolean hasNewComments) {
        this.hasNewComments = hasNewComments;
    }
}
