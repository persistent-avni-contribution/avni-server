package org.openchs.web.request.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.openchs.application.Form;
import org.openchs.web.request.ReferenceDataContract;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;

import static org.openchs.application.FormElement.PLACEHOLDER_CONCEPT_NAME;
import static org.openchs.application.FormElement.PLACEHOLDER_CONCEPT_UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "uuid", "formType", "userUUID", "formElementGroups"})
public class FormContract extends ReferenceDataContract {
    private String formType;
    private List<FormElementGroupContract> formElementGroups;
    private Long organisationId;
    private String subjectType;
    private String programName;
    private List<String> encounterTypes;
    private String decisionRule;
    private String visitScheduleRule;
    private String validationRule;
    private String checklistsRule;
    private String createdBy;
    private String lastModifiedBy;
    private DateTime createdDateTime;
    private DateTime lastModifiedDateTime;

    public FormContract() {
    }

    public FormContract(String uuid, String userUUID, String name, String formType) {
        super(uuid, userUUID, name);
        this.formType = formType;
        formElementGroups = new ArrayList<>();
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public List<FormElementGroupContract> getFormElementGroups() {
        return formElementGroups;
    }

    public void setFormElementGroups(List<FormElementGroupContract> formElementGroups) {
        this.formElementGroups = formElementGroups;
    }

    public void addFormElementGroup(FormElementGroupContract formElementGroupContract) {
        formElementGroups.add(formElementGroupContract);
    }

    @Override
    public String toString() {
        return "{" +
                "name=" + this.getName() + '\'' +
                "formType='" + formType + '\'' +
                '}';
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void validate() throws InvalidObjectException {
        HashSet<String> uniqueConcepts = new HashSet<>();
        for (FormElementGroupContract formElementGroup : getFormElementGroups()) {
            for (FormElementContract formElement : formElementGroup.getFormElements()) {
                String conceptUuid = formElement.getConcept().getUuid();
                String conceptName = formElement.getConcept().getName();
                if (!formElement.isVoided() &&
                        !PLACEHOLDER_CONCEPT_NAME.matcher(conceptName == null ? "" : conceptName).matches() &&
                        !conceptUuid.equals(PLACEHOLDER_CONCEPT_UUID) &&
                        !uniqueConcepts.add(conceptUuid)) {
                    throw new InvalidObjectException(String.format(
                            "Cannot use same concept twice. Form{uuid='%s',..} uses Concept{uuid='%s',..} twice",
                            getUuid(),
                            conceptUuid));
                }
            }
        }
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public List<String> getEncounterTypes() {
        return encounterTypes;
    }

    public void setEncounterTypes(List<String> encounterTypes) {
        this.encounterTypes = encounterTypes;
    }

    public static FormContract fromForm(Form form) {
        FormContract formContract = new FormContract();
        formContract.setFormType(form.getFormType().name());
        formContract.setName(form.getName());
        formContract.setUuid(form.getUuid());
        formContract.setVoided(form.isVoided());
        formContract.setDecisionRule(form.getDecisionRule());
        formContract.setVisitScheduleRule(form.getVisitScheduleRule());
        formContract.setValidationRule(form.getValidationRule());
        formContract.setChecklistsRule(form.getChecklistsRule());
      
        List<FormElementGroupContract> fegContracts = form.getFormElementGroups().stream()
                .map(FormElementGroupContract::fromFormElementGroup)
                .sorted(Comparator.comparingDouble(FormElementGroupContract::getDisplayOrder))
                .collect(Collectors.toList());
        formContract.setFormElementGroups(fegContracts);
        return formContract;
    }

    @Override
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public boolean isVoided() {
        return super.isVoided();
    }

    public String getDecisionRule() {
        return decisionRule;
    }

    public void setDecisionRule(String decisionRule) {
        this.decisionRule = decisionRule;
    }

    public String getVisitScheduleRule() {
        return visitScheduleRule;
    }

    public void setVisitScheduleRule(String visitScheduleRule) {
        this.visitScheduleRule = visitScheduleRule;
    }

    public String getValidationRule() {
        return validationRule;
    }

    public String getChecklistsRule(){
        return checklistsRule;
    }

    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }

    public void setChecklistsRule(String checklistsRule){
        this.checklistsRule = checklistsRule;
    }

    public void setCreatedBy(String username){
        this.createdBy = username;
    }
    public String getCreatedBy(){
        return createdBy;
    }

    public void setLastModifiedBy(String username){
        this.lastModifiedBy = username;
    }

    public String getLastModifiedBy(){
        return lastModifiedBy;
    }


    public void setCreatedDateTime(DateTime createDateTime){
        this.createdDateTime = createDateTime;
    }

    public DateTime getCreatedDateTime(){
        return createdDateTime;
    }

    public void setModifiedDateTime(DateTime lastModifiedDateTime){
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public DateTime getModifiedDateTime(){
        return lastModifiedDateTime;
    }
}