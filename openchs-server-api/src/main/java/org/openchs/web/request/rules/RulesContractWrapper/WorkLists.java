package org.openchs.web.request.rules.RulesContractWrapper;

import java.util.List;

public class WorkLists {
    private String name;
    private List<WorkItems> workItems;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WorkItems> getWorkItems() {
        return workItems;
    }

    public void setWorkItems(List<WorkItems> workItems) {
        this.workItems = workItems;
    }
}