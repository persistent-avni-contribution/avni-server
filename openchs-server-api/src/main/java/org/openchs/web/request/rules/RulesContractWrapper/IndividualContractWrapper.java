package org.openchs.web.request.rules.RulesContractWrapper;

import org.joda.time.LocalDate;
import org.openchs.web.request.GenderContract;
import org.openchs.web.request.ObservationContract;
import org.openchs.web.request.SubjectTypeContract;
import org.openchs.web.request.rules.request.RuleRequestEntity;

import java.util.List;

public class IndividualContractWrapper {
    private String uuid;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private GenderContract gender;

    private LocalDate registrationDate;

    private LowestAddressLevelContract lowestAddressLevel;

    private List<ObservationContract> observations;

    private boolean voided;

    private String registrationLocation;

    private SubjectTypeContract subjectType;

    private RuleRequestEntity rule;

    private List<ProgramEnrolmentContractWrapper> enrolments;

    private List<EncounterContractWrapper> encounters;

    private List<WorkLists> workLists;

    public List<WorkLists> getWorkLists() {
        return workLists;
    }

    public void setWorkLists(List<WorkLists> workLists) {
        this.workLists = workLists;
    }

    public List<ProgramEnrolmentContractWrapper> getEnrolments() {
        return enrolments;
    }

    public void setEnrolments(List<ProgramEnrolmentContractWrapper> enrolments) {
        this.enrolments = enrolments;
    }

    public List<EncounterContractWrapper> getEncounters() {
        return encounters;
    }

    public void setEncounters(List<EncounterContractWrapper> encounters) {
        this.encounters = encounters;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public GenderContract getGender() {
        return gender;
    }

    public void setGender(GenderContract gender) {
        this.gender = gender;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LowestAddressLevelContract getLowestAddressLevel() {
        return lowestAddressLevel;
    }

    public void setLowestAddressLevel(LowestAddressLevelContract lowestAddressLevel) {
        this.lowestAddressLevel = lowestAddressLevel;
    }

    public List<ObservationContract> getObservations() {
        return observations;
    }

    public void setObservations(List<ObservationContract> observations) {
        this.observations = observations;
    }

    public boolean isVoided() {
        return voided;
    }

    public void setVoided(boolean voided) {
        this.voided = voided;
    }

    public String getRegistrationLocation() {
        return registrationLocation;
    }

    public void setRegistrationLocation(String registrationLocation) {
        this.registrationLocation = registrationLocation;
    }

    public SubjectTypeContract getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SubjectTypeContract subjectType) {
        this.subjectType = subjectType;
    }

    public RuleRequestEntity getRule() {
        return rule;
    }

    public void setRule(RuleRequestEntity rule) {
        this.rule = rule;
    }
}