package org.openchs.web.request.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.openchs.domain.EncounterType;
import org.openchs.domain.Program;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScopeParameter {

    private List<String> encounterTypeUUIDs;
    private List<String> programUUIDs;

    public List<String> getEncounterTypeUUIDs() {
        return encounterTypeUUIDs;
    }

    public void setEncounterTypeUUIDs(List<String> encounterTypeUUIDs) {
        this.encounterTypeUUIDs = encounterTypeUUIDs;
    }

    public List<String> getProgramUUIDs() {
        return programUUIDs;
    }

    public void setProgramUUIDs(List<String> programUUIDs) {
        this.programUUIDs = programUUIDs;
    }

    @Override
    public String toString() {
        return '{' +
                "encounterTypeUUIDs=" + encounterTypeUUIDs +
                ", programUUIDs=" + programUUIDs +
                '}';
    }
}
