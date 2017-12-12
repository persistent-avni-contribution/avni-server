package org.openchs.excel;

import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.openchs.dao.ConceptRepository;
import org.openchs.dao.IndividualRepository;
import org.openchs.domain.*;
import org.openchs.healthmodule.adapter.ProgramEnrolmentModuleInvoker;
import org.openchs.healthmodule.adapter.contract.checklist.ChecklistItemRuleResponse;
import org.openchs.healthmodule.adapter.contract.checklist.ChecklistRuleResponse;
import org.openchs.healthmodule.adapter.contract.enrolment.ProgramEnrolmentRuleInput;
import org.openchs.service.ChecklistService;
import org.openchs.util.O;
import org.openchs.web.*;
import org.openchs.web.request.*;
import org.openchs.web.request.application.ChecklistItemRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

public class RowProcessor {
    private final Logger logger;
    private List<String> registrationHeader = new ArrayList<>();
    private Map<SheetMetaData, List<String>> enrolmentHeaders = new HashMap<>();
    private Map<SheetMetaData, List<String>> programEncounterHeaders = new HashMap<>();
    private Map<SheetMetaData, List<String>> checklistHeaders = new HashMap<>();

    private IndividualController individualController;
    private ProgramEnrolmentController programEnrolmentController;
    private ProgramEncounterController programEncounterController;
    private IndividualRepository individualRepository;
    private ChecklistController checklistController;
    private ChecklistItemController checklistItemController;
    private ChecklistService checklistService;
    private ConceptRepository conceptRepository;

    RowProcessor(IndividualController individualController, ProgramEnrolmentController programEnrolmentController, ProgramEncounterController programEncounterController, IndividualRepository individualRepository, ChecklistController checklistController, ChecklistItemController checklistItemController, ChecklistService checklistService, ConceptRepository conceptRepository) {
        this.individualController = individualController;
        this.programEnrolmentController = programEnrolmentController;
        this.programEncounterController = programEncounterController;
        this.individualRepository = individualRepository;
        this.checklistController = checklistController;
        this.checklistItemController = checklistItemController;
        this.checklistService = checklistService;
        this.conceptRepository = conceptRepository;
        logger = LoggerFactory.getLogger(this.getClass());
    }

    void readRegistrationHeader(Row row) {
        readHeader(row, registrationHeader);
    }

    private void readHeader(Row row, List<String> headerList) {
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            String text = ExcelUtil.getText(row, i);
            if (text == null || text.equals("")) break;

            headerList.add(text);
        }
    }

    void processIndividual(Row row) throws ParseException {
        IndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setObservations(new ArrayList<>());
        for (int i = 0; i < registrationHeader.size(); i++) {
            String cellHeader = registrationHeader.get(i);
            if (cellHeader.equals("First Name")) {
                individualRequest.setFirstName(ExcelUtil.getText(row, i));
            } else if (cellHeader.equals("Last Name")) {
                individualRequest.setLastName(ExcelUtil.getText(row, i));
            } else if (cellHeader.equals("Date of Birth")) {
                individualRequest.setDateOfBirth(new LocalDate(ExcelUtil.getDate(row, i)));
            } else if (cellHeader.equals("Date of Birth Verified")) {
                individualRequest.setDateOfBirthVerified(TextToType.toBoolean(ExcelUtil.getText(row, i)));
            } else if (cellHeader.equals("Gender")) {
                individualRequest.setGender(TextToType.toGender(ExcelUtil.getText(row, i)));
            } else if (cellHeader.equals("Registration Date")) {
                individualRequest.setRegistrationDate(new LocalDate(ExcelUtil.getDate(row, i)));
            } else if (cellHeader.equals("Address")) {
                individualRequest.setAddressLevel(ExcelUtil.getText(row, i));
            } else if (cellHeader.equals("UUID")) {
                individualRequest.setUuid(ExcelUtil.getText(row, i));
            } else {
                individualRequest.addObservation(getObservationRequest(row, i, cellHeader));
            }
        }
        individualController.save(individualRequest);
    }

    private ObservationRequest getObservationRequest(Row row, int i, String cellHeader) {
        String cell = ExcelUtil.getText(row, i);
        if (cell.isEmpty()) return null;
        ObservationRequest observationRequest = new ObservationRequest();
        observationRequest.setConceptName(cellHeader);
        Concept concept = conceptRepository.findByName(cellHeader);
        if (concept == null)
            throw new NullPointerException(String.format("Concept with name |%s| not found", cellHeader));
        observationRequest.setValue(getPrimitiveValue(concept, cell));
        return observationRequest;
    }

    public Object getPrimitiveValue(Concept concept, String visibleText) {
        if (ConceptDataType.Numeric.toString().equals(concept.getDataType())) return Double.parseDouble(visibleText);
        if (ConceptDataType.Date.toString().equals(concept.getDataType())) return O.getDateInDbFormat(visibleText);
        if (ConceptDataType.Coded.toString().equals(concept.getDataType())) {
            Concept answerConcept = concept.findAnswerConcept(visibleText);
            if (answerConcept == null)
                throw new NullPointerException(String.format("Concept with name |%s| not found", visibleText));
            return Arrays.asList(answerConcept.getUuid());
        }
        return visibleText;
    }

    void readEnrolmentHeader(Row row, SheetMetaData sheetMetaData) {
        ArrayList<String> enrolmentHeader = new ArrayList<>();
        enrolmentHeaders.put(sheetMetaData, enrolmentHeader);
        readHeader(row, enrolmentHeader);
    }

    void readProgramEncounterHeader(Row row, SheetMetaData sheetMetaData) {
        ArrayList<String> programEncounterHeader = new ArrayList<>();
        programEncounterHeaders.put(sheetMetaData, programEncounterHeader);
        readHeader(row, programEncounterHeader);
    }

    void processEnrolment(Row row, SheetMetaData sheetMetaData, ProgramEnrolmentModuleInvoker programEnrolmentModuleInvoker) {
        ProgramEnrolmentRequest programEnrolmentRequest = new ProgramEnrolmentRequest();
        programEnrolmentRequest.setProgram(sheetMetaData.getProgramName());
        programEnrolmentRequest.setObservations(new ArrayList<>());
        programEnrolmentRequest.setProgramExitObservations(new ArrayList<>());

        List<String> enrolmentHeader = enrolmentHeaders.get(sheetMetaData);
        for (int i = 0; i < enrolmentHeader.size(); i++) {
            String cellHeader = enrolmentHeader.get(i);
            programEnrolmentRequest.setUuid(UUID.randomUUID().toString());
            if (cellHeader.equals("IndividualUUID"))
                programEnrolmentRequest.setIndividualUUID(ExcelUtil.getRawCellValue(row, i));
            else if (cellHeader.equals("Enrolment Date")) {
                programEnrolmentRequest.setEnrolmentDateTime(new DateTime(ExcelUtil.getDate(row, i)));
            } else {
                programEnrolmentRequest.addObservation(getObservationRequest(row, i, cellHeader));
            }
        }

        ProgramEnrolmentRuleInput programEnrolmentRuleInput = new ProgramEnrolmentRuleInput(programEnrolmentRequest, individualRepository, conceptRepository);
        List<ObservationRequest> observationRequests = programEnrolmentModuleInvoker.getDecisions(programEnrolmentRuleInput, conceptRepository);
        observationRequests.forEach(programEnrolmentRequest::addObservation);
        programEnrolmentController.save(programEnrolmentRequest);

        Checklist checklist = checklistService.findChecklist(programEnrolmentRequest.getUuid());

        ChecklistRuleResponse checklistRuleResponse = programEnrolmentModuleInvoker.getChecklist(programEnrolmentRuleInput);
        if (checklistRuleResponse != null) {
            ChecklistRequest checklistRequest = checklistRuleResponse.getChecklistRequest();
            checklistRequest.setProgramEnrolmentUUID(programEnrolmentRequest.getUuid());
            checklistRequest.setUuid(checklist == null ? UUID.randomUUID().toString() : checklist.getUuid());
            checklistController.save(checklistRequest);

            List<ChecklistItemRequest> items = checklistRuleResponse.getItems(checklistService, programEnrolmentRequest.getUuid(), conceptRepository);
            items.forEach(checklistItemRequest -> {
                checklistItemRequest.setChecklistUUID(checklistRequest.getUuid());
                checklistItemController.save(checklistItemRequest);
            });
        }
        this.logger.info(String.format("Imported Enrolment for Program: %s, Enrolment: %s", programEnrolmentRequest.getProgram(), programEnrolmentRequest.getUuid()));
    }

    void processProgramEncounter(Row row, SheetMetaData sheetMetaData) {
        int numberOfStaticColumns = 1;
        ProgramEncounterRequest programEncounterRequest = new ProgramEncounterRequest();
        programEncounterRequest.setObservations(new ArrayList<>());
        programEncounterRequest.setProgramEnrolmentUUID(ExcelUtil.getText(row, 0));
        programEncounterRequest.setUuid(UUID.randomUUID().toString());
        List<String> programEncounterHeader = programEncounterHeaders.get(sheetMetaData);
        for (int i = numberOfStaticColumns; i < programEncounterHeader.size() + numberOfStaticColumns; i++) {
            String cellHeader = programEncounterHeader.get(i - numberOfStaticColumns);
            if (cellHeader.equals("Visit Type")) {
                programEncounterRequest.setEncounterType(ExcelUtil.getText(row, i));
            } else if (cellHeader.equals("Visit Name")) {
                programEncounterRequest.setName(ExcelUtil.getText(row, i));
            } else if (cellHeader.equals("Earliest Date")) {
                programEncounterRequest.setEarliestVisitDateTime(new DateTime(ExcelUtil.getDate(row, i)));
            } else if (cellHeader.equals("Actual Date")) {
                programEncounterRequest.setEncounterDateTime(new DateTime(ExcelUtil.getDate(row, i)));
            } else if (cellHeader.equals("Max Date")) {
                programEncounterRequest.setMaxDateTime(new DateTime(ExcelUtil.getDate(row, i)));
            } else {
                programEncounterRequest.addObservation(getObservationRequest(row, i, cellHeader));
            }
        }
        programEncounterController.save(programEncounterRequest);
    }

    void readChecklistHeader(Row row, SheetMetaData sheetMetaData) {
        ArrayList<String> checklistHeader = new ArrayList<>();
        programEncounterHeaders.put(sheetMetaData, checklistHeader);
        readHeader(row, checklistHeader);
    }

    void processChecklist(Row row, SheetMetaData sheetMetaData) {
        int numberOfStaticColumns = 2;
        String programEnrolmentUUID = ExcelUtil.getText(row, 0);
        String checklistName = ExcelUtil.getText(row, 1);
        List<String> checklistHeader = checklistHeaders.get(sheetMetaData);

        for (int i = numberOfStaticColumns; i < checklistHeader.size() + numberOfStaticColumns; i++) {
            String checklistItemName = checklistHeader.get(i - numberOfStaticColumns);
            ChecklistItem checklistItem = checklistService.findChecklistItem(programEnrolmentUUID, checklistItemName);
            if (checklistItem == null)
                throw new RuntimeException(String.format("Couldn't find checklist item with name=%s", checklistItemName));
            Double offsetFromDueDate = ExcelUtil.getNumber(row, i);

            DateTime completionDate = null;
            if (offsetFromDueDate != null) {
                DateTime dueDate = checklistItem.getDueDate();
                completionDate = dueDate.plusDays(offsetFromDueDate.intValue());
                if (completionDate.isAfterNow()) completionDate = null;
            }

            checklistItem.setCompletionDate(completionDate);
            checklistService.saveItem(checklistItem);
        }
    }
}