package org.openchs.web.request;

import java.util.List;

public class LocationContract extends ReferenceDataContract {
    private Integer level;
    private List<String> parents;
    private String type;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getParents() {
        return parents;
    }

    public void setParents(List<String> parents) {
        this.parents = parents;
    }
}
