package org.openchs.web.request.search;

import java.util.*;

public class IndividualSearchRequest {

    private String name;

    private MinMax age;

    private boolean includeVoided;

    private String gender;

    private String subjectType;

    private String address;

    private MinMax registrationDate;

    private List<Concepts> concept;

    private String searchAll;

    private MinMax encounterDate;

    private MinMax programEncounterDate;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
                ", address='" + address + '\'' +
                ", registrationDate=" + registrationDate +
                ", concept=" + concept +
                ", searchAll='" + searchAll + '\'' +
                ", encounterDate=" + encounterDate +
                ", programEncounterDate=" + programEncounterDate +
                ", enrolmentDate=" + enrolmentDate +
                '}';
    }
}