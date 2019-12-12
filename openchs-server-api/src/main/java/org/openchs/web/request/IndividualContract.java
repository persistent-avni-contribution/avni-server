package org.openchs.web.request;

import org.openchs.domain.ProgramEnrolment;
import org.openchs.domain.ProgramEnrolment.MinimalProgramEnrolmentProjection;
import org.openchs.domain.individualRelationship.IndividualRelationship;
import org.openchs.web.request.common.CommonIndividualRequest;

import java.util.*;

public class IndividualContract extends CommonIndividualRequest {

    private String fullAddress;

    private List<RelationshipContract> relationships = new ArrayList<>();

    private List<ObservationContract> observations = new ArrayList<>();

    private List<MinimalProgramEnrolmentProjection> enrolments = new ArrayList<>();

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public List<RelationshipContract> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<RelationshipContract> relationships) {
        this.relationships = relationships;
    }

    public List<ObservationContract> getObservations() {
        return observations;
    }

    public void setObservations(List<ObservationContract> observations) {
        this.observations = observations;
    }

    public List<MinimalProgramEnrolmentProjection> getEnrolments() {
        return enrolments;
    }

    public void setEnrolments(List<MinimalProgramEnrolmentProjection> enrolments) {
        this.enrolments = enrolments;
    }

}
