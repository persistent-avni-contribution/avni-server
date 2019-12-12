package org.openchs.web.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.openchs.domain.Concept;
import org.openchs.domain.ConceptDataType;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"name", "uuid", "dataType", "answers", "order", "lowAbsolute", "highAbsolute", "lowNormal", "highNormal", "unit", "unique", "organisationUUID"})
public class ConceptContract extends ReferenceDataContract {
    private String dataType;
    private List<ConceptContract> answers;
    private Double lowAbsolute;
    private Double highAbsolute;
    private Double lowNormal;
    private Double highNormal;
    private String unit;
    private String organisationUUID;
    private boolean abnormal;
    private boolean unique = false;
    private Double order;

    public static ConceptContract create(Concept concept) {
        ConceptContract conceptContract = new ConceptContract();
        conceptContract.setUuid(concept.getUuid());
        conceptContract.setName(concept.getName());
        return conceptContract;
    }

    public String getDataType() {
        return dataType == null ? null : dataType.trim();
    }

    @JsonIgnore
    public boolean isCoded() {
        return ConceptDataType.Coded.toString().equals(this.getDataType());
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<ConceptContract> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ConceptContract> answers) {
        this.answers = answers;
    }

    public Double getLowAbsolute() {
        return lowAbsolute;
    }

    public void setLowAbsolute(Double lowAbsolute) {
        this.lowAbsolute = lowAbsolute;
    }

    public Double getHighAbsolute() {
        return highAbsolute;
    }

    public void setHighAbsolute(Double highAbsolute) {
        this.highAbsolute = highAbsolute;
    }

    public Double getLowNormal() {
        return lowNormal;
    }

    public void setLowNormal(Double lowNormal) {
        this.lowNormal = lowNormal;
    }

    public Double getHighNormal() {
        return highNormal;
    }

    public void setHighNormal(Double highNormal) {
        this.highNormal = highNormal;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isAbnormal() {
        return abnormal;
    }

    public void setAbnormal(boolean abnormal) {
        this.abnormal = abnormal;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getOrganisationUUID() {
        return organisationUUID;
    }

    public void setOrganisationUUID(String organisationUUID) {
        this.organisationUUID = organisationUUID;
    }

    public Double getOrder() {
        return order;
    }

    public void setOrder(Double order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return String.format("UUID: %s, Name: %s, DataType: %s", this.getUuid(), this.getName(), this.getDataType());
    }
}