package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.Collection;

public class DelphiGraphDataSingle extends AbstractDelphiGraphData {
    private final Collection<DelphiGraphEntry> data = new ArrayList<>();
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void addEntry(DelphiGraphEntry entry) {
        data.add(entry);
    }

    public Collection<DelphiGraphEntry> getData() {
        return new ArrayList<>(data);
    }
}
