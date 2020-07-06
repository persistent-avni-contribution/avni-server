package org.openchs.web.request.search;



import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

public class IndividualSearchRequest {

    private String name;

    private MinMax age;

    private boolean includeVoided;

    private List<String> gender;

    private String subjectType;

    private List<Long>  addressIds;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MinMax registrationDate;

    private List<Concepts> concept;

    private String searchAll;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MinMax encounterDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MinMax programEncounterDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MinMax enrolmentDate;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MinMax getAge() {
        return age;
    }

    public void setAge(MinMax age) {
        this.age = age;
    }

    public boolean isIncludeVoided() {
        return includeVoided;
    }

    public void setIncludeVoided(boolean includeVoided) {
        this.includeVoided = includeVoided;
    }

    public List<String> getGender() {
        return gender;
    }

    public void setGender(List<String> gender) {
        this.gender = gender;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public List<Long> getAddressIds() {
        return addressIds;
    }

    public void setAddressIds(List<Long> addressIds) {
        this.addressIds = addressIds;
    }

    public MinMax getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(MinMax registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<Concepts> getConcept() {
        return concept;
    }

    public void setConcept(List<Concepts> concept) {
        this.concept = concept;
    }

    public String getSearchAll() {
        return searchAll;
    }

    public void setSearchAll(String searchAll) {
        this.searchAll = searchAll;
    }

    public MinMax getEncounterDate() {
        return encounterDate;
    }

    public void setEncounterDate(MinMax encounterDate) {
        this.encounterDate = encounterDate;
    }

    public MinMax getProgramEncounterDate() {
        return programEncounterDate;
    }

    public void setProgramEncounterDate(MinMax programEncounterDate) {
        this.programEncounterDate = programEncounterDate;
    }

    public MinMax getEnrolmentDate() {
        return enrolmentDate;
    }

    public void setEnrolmentDate(MinMax enrolmentDate) {
        this.enrolmentDate = enrolmentDate;
    }

    @Override
    public String toString() {
        return "IndividualSearchRequest{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", includeVoided=" + includeVoided +
                ", gender='" + gender + '\'' +
                ", subjectType='" + subjectType + '\'' +
                ", address='" + addressIds + '\'' +
                ", registrationDate=" + registrationDate +
                ", concept=" + concept +
                ", searchAll='" + searchAll + '\'' +
                ", encounterDate=" + encounterDate +
                ", programEncounterDate=" + programEncounterDate +
                ", enrolmentDate=" + enrolmentDate +
                '}';
    }
}