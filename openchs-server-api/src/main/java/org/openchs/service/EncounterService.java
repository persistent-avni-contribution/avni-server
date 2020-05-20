package org.openchs.service;

import com.bugsnag.Bugsnag;
import org.openchs.common.EntityHelper;
import org.openchs.dao.*;
import org.openchs.dao.individualRelationship.RuleFailureLogRepository;
import org.openchs.domain.*;
import org.openchs.geo.Point;
import org.openchs.web.request.*;
import org.openchs.web.request.rules.RulesContractWrapper.VisitSchedule;
import org.openchs.web.request.rules.constant.EntityEnum;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EncounterService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(EncounterService.class);
    @Autowired
    Bugsnag bugsnag;
    private EncounterTypeRepository encounterTypeRepository;
    private ObservationService observationService;
    private RuleFailureLogRepository ruleFailureLogRepository;
    private IndividualRepository individualRepository;
    private EncounterRepository encounterRepository;


    @Autowired
    public EncounterService(EncounterTypeRepository encounterTypeRepository,
                            ObservationService observationService,
                            RuleFailureLogRepository ruleFailureLogRepository,
                            IndividualRepository individualRepository,
                            EncounterRepository encounterRepository) {
        this.encounterTypeRepository = encounterTypeRepository;
        this.observationService = observationService;
        this.ruleFailureLogRepository = ruleFailureLogRepository;
        this.individualRepository = individualRepository;
        this.encounterRepository = encounterRepository;
    }

    public Individual getAllEncountersForIndividual(String uuid){
        Individual individual = individualRepository.findByUuid(uuid);
        return individual;
    }

    public List<Encounter> constructVisitSchedules(Individual individual,String encounterTypeName){
        return individual.getEncounters().stream().filter(encounter -> encounter.getEncounterType().getName().equals(encounterTypeName)).collect(Collectors.toList());
    }

    public void saveVisitSchedules(String uuid,List<VisitSchedule> visitSchedules){
        Individual individual = getAllEncountersForIndividual(uuid);
        for( VisitSchedule visitSchedule : visitSchedules){
            try {
                processVisitSchedule(uuid, visitSchedule,individual);
            }catch (Exception e){
                RuleFailureLog ruleFailureLog = RuleFailureLog.createInstance(uuid,"Save : Visit Schedule Rule",uuid,"Save : "+ EntityEnum.ENCOUNTER_ENTITY.getEntityName(),"Web",e);
                ruleFailureLogRepository.save(ruleFailureLog);
            }
        }
    }

    public void processVisitSchedule(String uuid,VisitSchedule visitSchedule,Individual individual) throws Exception {
        List<Encounter> allScheduleEncountersByType = constructVisitSchedules(individual,visitSchedule.getEncounterType());
        if(allScheduleEncountersByType.isEmpty() || "createNew".equals(visitSchedule.getVisitCreationStrategy())){
            EncounterType encounterType = encounterTypeRepository.findByName(visitSchedule.getEncounterType());
            if(encounterType == null){
                throw new Exception("NextScheduled visit is for encounter type="+visitSchedule.getName()+" that doesn't exist");
            }
            Encounter encounter = createEmptyEncounter(individual,encounterType);
            allScheduleEncountersByType.add(encounter);
        }
        allScheduleEncountersByType.stream().forEach( encounter -> {
            updateEncounterWithVisitSchedule(encounter,visitSchedule);
            encounterRepository.save(encounter);
        });
    }

    public void updateEncounterWithVisitSchedule(Encounter encounter, VisitSchedule visitSchedule){
        encounter.setEarliestVisitDateTime(visitSchedule.getEarliestDate());
        encounter.setMaxVisitDateTime(visitSchedule.getMaxDate());
        encounter.setName(visitSchedule.getName());
    }

    public Encounter createEmptyEncounter(Individual individual,EncounterType encounterType){
        Encounter encounter = new Encounter();
        encounter.setEncounterType(encounterType);
        encounter.setIndividual(individual);
        encounter.setEncounterDateTime(null);
        encounter.setUuid(UUID.randomUUID().toString());
        encounter.setVoided(false);
        encounter.setObservations(new ObservationCollection());
        encounter.setCancelObservations(new ObservationCollection());
        return encounter;
    }

    public void saveEncounters(EncounterRequest request){
        logger.info("Saving encounter with uuid %s", request.getUuid());

        checkForSchedulingCompleteConstraintViolation(request);

        EncounterType encounterType = encounterTypeRepository.findByUuidOrName(request.getEncounterType(), request.getEncounterTypeUUID());
        Individual individual = individualRepository.findByUuid(request.getIndividualUUID());
        if (individual == null) {
            throw new IllegalArgumentException(String.format("Individual not found with UUID '%s'", request.getIndividualUUID()));
        }

        Encounter encounter = EntityHelper.newOrExistingEntity(encounterRepository, request, new Encounter());
        //Planned visit can not overwrite completed encounter
        if (encounter.isCompleted() && request.isPlanned())
            return;

        encounter.setEncounterDateTime(request.getEncounterDateTime());
        encounter.setIndividual(individual);
        encounter.setEncounterType(encounterType);
        encounter.setObservations(observationService.createObservations(request.getObservations()));
        encounter.setName(request.getName());
        encounter.setEarliestVisitDateTime(request.getEarliestVisitDateTime());
        encounter.setMaxVisitDateTime(request.getMaxVisitDateTime());
        encounter.setCancelDateTime(request.getCancelDateTime());
        encounter.setCancelObservations(observationService.createObservations(request.getCancelObservations()));
        encounter.setVoided(request.isVoided());
        PointRequest encounterLocation = request.getEncounterLocation();
        if (encounterLocation != null)
            encounter.setEncounterLocation(new Point(encounterLocation.getX(), encounterLocation.getY()));
        PointRequest cancelLocation = request.getCancelLocation();
        if (cancelLocation != null)
            encounter.setCancelLocation(new Point(cancelLocation.getX(), cancelLocation.getY()));

        encounterRepository.save(encounter);
        logger.info(String.format("Saved encounter with uuid %s", request.getUuid()));
    }

    private void checkForSchedulingCompleteConstraintViolation(EncounterRequest request) {
        if ((request.getEarliestVisitDateTime() != null || request.getMaxVisitDateTime() != null)
                && (request.getEarliestVisitDateTime() == null || request.getMaxVisitDateTime() == null)
        ) {
            //violating constraint so notify bugsnag
            bugsnag.notify(new Exception(String.format("ProgramEncounter violating scheduling constraint uuid %s earliest %s max %s", request.getUuid(), request.getEarliestVisitDateTime(), request.getMaxVisitDateTime())));
        }
    }

}