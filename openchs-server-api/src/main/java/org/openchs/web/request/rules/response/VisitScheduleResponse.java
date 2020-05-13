package org.openchs.web.request.rules.response;

import org.joda.time.DateTime;

public class VisitScheduleResponse {
    private String name;
    private String encounterType;
    private DateTime earliestDate;
    private DateTime maxDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public DateTime getEarliestDate() {
        return earliestDate;
    }

    public void setEarliestDate(DateTime earliestDate) {
        this.earliestDate = earliestDate;
    }

    public DateTime getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(DateTime maxDate) {
        this.maxDate = maxDate;
    }
}
