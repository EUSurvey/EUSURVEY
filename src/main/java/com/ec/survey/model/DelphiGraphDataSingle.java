package com.ec.survey.model;

import java.util.ArrayList;
import java.util.Collection;

public class DelphiGraphDataSingle {
    public final String type = "single";
    private final Collection<DelphiGraphEntry> data = new ArrayList<>();

    public void addEntry(DelphiGraphEntry entry) {
        data.add(entry);
    }

    public Collection<DelphiGraphEntry> getData() {
        return new ArrayList<>(data);
    }
}
