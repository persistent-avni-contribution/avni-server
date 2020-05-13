package org.openchs.web.request.rules.response;

import org.openchs.web.request.EncounterContract;
import org.openchs.web.request.ObservationContract;
import org.openchs.web.request.ProgramEncountersContract;

import java.util.List;

public class RuleResponseEntity{
    private String status;
    private DecisionResponseEntity data;
    private List<VisitScheduleResponse> visitSchedules;
    private List<ObservationContract> observation;
    private List<ValidationResponse> formValidate;
    private List<ProgramEncountersContract> programEncounters;
    private String message;
    private Boolean visibility;

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }


    public List<ProgramEncountersContract> getProgramEncounters() {
        return programEncounters;
    }

    public void setProgramEncounters(List<ProgramEncountersContract> programEncounters) {
        this.programEncounters = programEncounters;
    }

    public List<VisitScheduleResponse> getVisitSchedules() {
        return visitSchedules;
    }

    public void setVisitSchedules(List<VisitScheduleResponse> visitSchedules) {
        this.visitSchedules = visitSchedules;
    }

    public List<ObservationContract> getObservation() {
        return observation;
    }

    public void setObservation(List<ObservationContract> observation) {
        this.observation = observation;
    }

    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public DecisionResponseEntity getData() {
        return data;
    }

    public void setData(DecisionResponseEntity data) {
        this.data = data;
    }

    public List<ValidationResponse> getFormValidate() {
        return formValidate;
    }

    public void setFormValidate(List<ValidationResponse> formValidate) {
        this.formValidate = formValidate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
