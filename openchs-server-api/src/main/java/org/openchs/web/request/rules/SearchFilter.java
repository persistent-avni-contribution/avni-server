package org.openchs.web.request.rules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.openchs.domain.EncounterType;
import org.openchs.domain.Program;

import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchFilter {

    private String type;
    private String Scope;
    private String titleKey;
    private String conceptName;
    private String conceptUUID;
    private String conceptDataType;
    private List<EncounterType> encounterTypeUUIDs;
    private List<Program> programUUIDs;
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

    public List<EncounterType> getEncounterTypeUUIDs() {
        return encounterTypeUUIDs;
    }

    public void setEncounterTypeUUIDs(List<EncounterType> encounterTypeUUIDs) {
        this.encounterTypeUUIDs = encounterTypeUUIDs;
    }

    public List<Program> getProgramUUIDs() {
        return programUUIDs;
    }

    public void setProgramUUIDs(List<Program> programUUIDs) {
        this.programUUIDs = programUUIDs;
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
                ", encounterTypeUUIDs=" + encounterTypeUUIDs +
                ", programUUIDs=" + programUUIDs +
                ", subjectTypeUUID='" + subjectTypeUUID + '\'' +
                '}';
    }
}
