package com.ec.survey.model.delphi;

import java.util.List;

public class DelphiContributions {
    private final int total;
    private final List<DelphiContribution> contributions;

    public DelphiContributions(int total, List<DelphiContribution> contributions) {
        this.total = total;
        this.contributions = contributions;
    }

    public int getTotal() {
        return total;
    }

    public List<DelphiContribution> getContributions() {
        return contributions;
    }
}
