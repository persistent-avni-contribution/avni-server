package org.openchs.web.request;

import org.joda.time.DateTime;

import java.util.List;

public class RelationshipContract extends ReferenceDataContract{
    private String relationshipTypeUuid;

    private String individualBIsToARelation;

    private String individualBUuid;

    private DateTime enterDateTime;

    private DateTime exitDateTime;

    private List<ObservationContract> exitObservations;

    public String getRelationshipTypeUuid() {
        return relationshipTypeUuid;
    }

    public void setRelationshipTypeUuid(String relationshipTypeUuid) {
        this.relationshipTypeUuid = relationshipTypeUuid;
    }

    public String getIndividualBIsToARelation() {
        return individualBIsToARelation;
    }

    public void setIndividualBIsToARelation(String individualBIsToARelation) {
        this.individualBIsToARelation = individualBIsToARelation;
    }

    public String getIndividualBUuid() {
        return individualBUuid;
    }

    public void setIndividualBUuid(String individualBUuid) {
        this.individualBUuid = individualBUuid;
    }

    public DateTime getEnterDateTime() {
        return enterDateTime;
    }

    public void setEnterDateTime(DateTime enterDateTime) {
        this.enterDateTime = enterDateTime;
    }

    public DateTime getExitDateTime() {
        return exitDateTime;
    }

    public void setExitDateTime(DateTime exitDateTime) {
        this.exitDateTime = exitDateTime;
    }

    public List<ObservationContract> getExitObservations() {
        return exitObservations;
    }

    public void setExitObservations(List<ObservationContract> exitObservations) {
        this.exitObservations = exitObservations;
    }
}
