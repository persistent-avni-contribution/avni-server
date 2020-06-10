package org.openchs.web.request.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchFilter {

    private String type;
    private String Scope;
    private String titleKey;
    private String conceptName;
    private String conceptUUID;
    private String conceptDataType;
    private ScopeParameter scopeParameters;
    private String subjectTypeUUID;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScope() {
        return Scope;
    }

    public void setScope(String scope) {
        Scope = scope;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    public ScopeParameter getScopeParameters() {
        return scopeParameters;
    }

    public void setScopeParameters(ScopeParameter scopeParameters) {
        this.scopeParameters = scopeParameters;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getConceptUUID() {
        return conceptUUID;
    }

    public void setConceptUUID(String conceptUUID) {
        this.conceptUUID = conceptUUID;
    }

    public String getConceptDataType() {
        return conceptDataType;
    }

    public void setConceptDataType(String conceptDataType) {
        this.conceptDataType = conceptDataType;
    }

    public String getSubjectTypeUUID() {
        return subjectTypeUUID;
    }

    public void setSubjectTypeUUID(String subjectTypeUUID) {
        this.subjectTypeUUID = subjectTypeUUID;
    }

    @Override
    public String toString() {
        return "SearchFilter{" +
                "type='" + type + '\'' +
                ", Scope='" + Scope + '\'' +
                ", titleKey='" + titleKey + '\'' +
                ", conceptName='" + conceptName + '\'' +
                ", conceptUUID='" + conceptUUID + '\'' +
                ", conceptDataType='" + conceptDataType + '\'' +
                ", scopeParameters=" + scopeParameters +
                ", subjectTypeUUID='" + subjectTypeUUID + '\'' +
                '}';
    }
}
