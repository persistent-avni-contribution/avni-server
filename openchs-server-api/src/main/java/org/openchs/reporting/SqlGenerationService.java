package org.openchs.reporting;

import org.openchs.application.FormElement;
import org.openchs.application.FormType;
import org.openchs.dao.OperationalEncounterTypeRepository;
import org.openchs.dao.OperationalProgramRepository;
import org.openchs.dao.application.FormMappingRepository;
import org.openchs.domain.ConceptDataType;
import org.openchs.domain.OperationalEncounterType;
import org.openchs.domain.OperationalProgram;
import org.openchs.domain.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.singletonList;

@Service
public class SqlGenerationService {
    private final String VIEW_TEMPLATE;

    private final OperationalProgramRepository operationalProgramRepository;
    private final OperationalEncounterTypeRepository operationalEncounterTypeRepository;
    private final FormMappingRepository formMappingRepository;

    @Autowired
    public SqlGenerationService(OperationalProgramRepository operationalProgramRepository, OperationalEncounterTypeRepository operationalEncounterTypeRepository, FormMappingRepository formMappingRepository) throws IOException {
        this.operationalProgramRepository = operationalProgramRepository;
        this.operationalEncounterTypeRepository = operationalEncounterTypeRepository;
        this.formMappingRepository = formMappingRepository;

        VIEW_TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("/pivot/pivot.sql").getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    public Map<String, String> getSqlsFor(String operationalProgramName, String operationalEncounterTypeName) {
        OperationalProgram operationalProgram = operationalProgramRepository.findByNameIgnoreCase(operationalProgramName);
        Iterable<OperationalEncounterType> operationalEncounterTypes = operationalEncounterTypeName == null ?
                operationalEncounterTypeRepository.findAll() :
                singletonList(operationalEncounterTypeRepository.findByNameIgnoreCase(operationalEncounterTypeName));
        if (operationalProgram != null && operationalProgram.getProgram() != null) {
            return getSqlFor(operationalProgram, operationalEncounterTypes);
        }
        throw new IllegalArgumentException(String.format("Not found OperationalProgram{name='%s'}", operationalProgramName));
    }

    private List<FormElement> getRegistrationFormElements() {
        return formMappingRepository
                .findByEntityIdAndObservationsTypeEntityIdAndFormFormType(null, null, FormType.IndividualProfile)
                .getForm()
                .getAllFormElements();
    }

    private List<FormElement> getProgramEnrolmentFormElements(Program program) {
        return formMappingRepository
                .findByEntityIdAndObservationsTypeEntityIdAndFormFormType(program.getId(), null, FormType.ProgramEnrolment)
                .getForm()
                .getAllFormElements();
    }

    private List<FormElement> getProgramEncounterFormElements(OperationalProgram operationalProgram, OperationalEncounterType type) {
        return formMappingRepository
                .findAllByEntityIdAndObservationsTypeEntityIdAndFormFormType(operationalProgram.getProgram().getId(), type.getEncounterType().getId(), FormType.ProgramEnrolment)
                .stream()
                .flatMap(fm -> fm.getForm().getAllFormElements().stream())
                .collect(Collectors.toList());
    }

    private Map<String, String> getSqlFor(OperationalProgram operationalProgram, Iterable<OperationalEncounterType> types) {
        final String mainViewQuery;
        List<FormElement> registrationFormElements = getRegistrationFormElements();
        List<FormElement> enrolmentFormElements = getProgramEnrolmentFormElements(operationalProgram.getProgram());

        mainViewQuery = VIEW_TEMPLATE.replace("${individual}", buildObsevationSelection("individual", registrationFormElements))
                .replace("${programEnrolment}", buildObsevationSelection("programEnrolment", enrolmentFormElements))
                .replace("${operationalProgramUuid}", operationalProgram.getUuid());

        return StreamSupport.stream(types.spliterator(), true).map(type -> {
            List<FormElement> programEncounterFormElements = getProgramEncounterFormElements(operationalProgram, type);
            String encounterSpecificSql = mainViewQuery.replace("${programEncounter}", buildObsevationSelection("programEncounter", programEncounterFormElements));
            return new SimpleEntry<>(type.getName(), encounterSpecificSql);
        }).collect(Collectors.toConcurrentMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    private String buildObsevationSelection(String tableName, List<FormElement> value) {
        return value.parallelStream().map(formElement -> {
            StringBuilder stringBuilder = new StringBuilder();
            switch (ConceptDataType.valueOf(formElement.getConcept().getDataType())) {
                case Numeric:
                case Text:
                case Notes:
                case Date:
                    stringBuilder
                            .append(tableName)
                            .append(".observations->>'")
                            .append(formElement.getConcept().getUuid())
                            .append("'");
                    break;
                case Coded: {
                    if (formElement.isSingleSelect()) {
                        stringBuilder.append("single_select_coded(")
                                .append(tableName)
                                .append(".observations->>'")
                                .append(formElement.getConcept().getUuid())
                                .append("')");
                    } else {
                        stringBuilder.append("multi_select_coded(")
                                .append(tableName)
                                .append(".observations->'")
                                .append(formElement.getConcept().getUuid())
                                .append("')");
                    }
                    break;
                }
            }
            stringBuilder.append("::TEXT as \"").append(formElement.getConcept().getName()).append("\"");
            return stringBuilder.toString();
        }).collect(Collectors.joining(","));
    }

}
