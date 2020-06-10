package org.openchs.web.request.search;

import org.joda.time.LocalDate;
import java.util.*;

public class IndividualSearchRequest {

    private String name;

    private LocalDate age;

    private boolean includeVoided;

    private String gender;

    private String subjectTypeUuid;

    private String address;

    private LocalDate registrationDate;

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

    public LocalDate getAge() {
        return age;
    }

    public void setAge(LocalDate age) {
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

    public String getSubjectTypeUuid() {
        return subjectTypeUuid;
    }

    public void setSubjectTypeUuid(String subjectTypeUuid) {
        this.subjectTypeUuid = subjectTypeUuid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
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
}