package org.openchs.service;

import org.openchs.dao.ConceptRepository;
import org.openchs.dao.IndividualRepository;
import org.openchs.domain.*;
import org.openchs.domain.individualRelationship.IndividualRelationship;
import org.openchs.web.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;


@Service
public class IndividualService {
    private final Logger logger;
    private final IndividualRepository individualRepository;
    private final ConceptRepository conceptRepository;
    private final ProjectionFactory projectionFactory;

    @Autowired
    public IndividualService(ConceptRepository conceptRepository, IndividualRepository individualRepository, ProjectionFactory projectionFactory) {
        this.projectionFactory = projectionFactory;
        logger = LoggerFactory.getLogger(this.getClass());
        this.conceptRepository = conceptRepository;
        this.individualRepository = individualRepository;
    }

    public IndividualContract getSubjectInfo(String individualUuid) {
        Individual individual = individualRepository.findByUuid(individualUuid);
        IndividualContract individualContract = new IndividualContract();
        if (!Objects.nonNull(individual)) {

            return null;
        }
        List<ObservationContract> observationContractsList = constructObservations(individual.getObservations());
        List<RelationshipContract> relationshipContractList = constructRelationships(individual);

        individualContract.setObservations(observationContractsList);
        individualContract.setRelationships(relationshipContractList);
        Class<ProgramEnrolment.MinimalProgramEnrolmentProjection> clazz = ProgramEnrolment.MinimalProgramEnrolmentProjection.class;
        individualContract.setEnrolments(createProjections(clazz, individual.getProgramEnrolments()));
        individualContract.setUuid(individual.getUuid());
        individualContract.setFirstName(individual.getFirstName());
        individualContract.setLastName(individual.getLastName());
        individualContract.setDateOfBirth(individual.getDateOfBirth());
        individualContract.setGender(individual.getGender().getName());
        individualContract.setAddressLevel(individual.getAddressLevel().getTitle());
        individualContract.setFullAddress(individual.getAddressLevel().getTitleLineage());
        return individualContract;
    }

    public List<RelationshipContract> constructRelationships(Individual individual) {
        return individual.getRelationships().stream().map(individualRelationship -> {
            RelationshipContract relationshipContract = new RelationshipContract();
            relationshipContract.setUuid(individualRelationship.getUuid());
            relationshipContract.setIndividualBUuid(individualRelationship.getIndividualB().getUuid());
            relationshipContract.setIndividualBIsToARelation(individualRelationship.getRelationship().getIndividualBIsToA().getName());
            relationshipContract.setRelationshipTypeUuid(individualRelationship.getRelationship().getUuid());
            relationshipContract.setEnterDateTime(individualRelationship.getEnterDateTime());
            relationshipContract.setExitDateTime(individualRelationship.getExitDateTime());

            if (individualRelationship.getExitObservations() != null) {
                relationshipContract.setExitObservations(constructObservations(individualRelationship.getExitObservations()));
            }
            return relationshipContract;
        }).collect(Collectors.toList());
    }

    public List<ObservationContract> constructObservations(@NotNull ObservationCollection observationCollection) {
        return observationCollection.entrySet().stream().map(entry -> {
            ObservationContract observationContract = new ObservationContract();
            Concept questionConcept = conceptRepository.findByUuid(entry.getKey());
            ConceptContract conceptContract = ConceptContract.create(questionConcept);
            observationContract.setConcept(conceptContract);
            Object value = entry.getValue();
            if (questionConcept.getDataType().equalsIgnoreCase(ConceptDataType.Coded.toString())) {
                List<String> answers = value instanceof List ? (List<String>) value : singletonList(value.toString());
                List<ConceptContract> answerConceptList = questionConcept.getConceptAnswers().stream()
                        .filter(it ->
                                answers.contains(it.getAnswerConcept().getUuid())
                        ).map(it -> {
                            ConceptContract cc = ConceptContract.create(it.getAnswerConcept());
                            cc.setAbnormal(it.isAbnormal());
                            return cc;
                        }).collect(Collectors.toList());
                observationContract.setValue(answerConceptList);
            } else {
                observationContract.setValue(value);
            }
            return observationContract;
        }).collect(Collectors.toList());
    }

    public <T> List<T> createProjections(Class<T> clazz, Collection items) {
        return (List<T>) items.stream().map(it-> {
            T projection = projectionFactory.createProjection(clazz, (Object) it);
            return projection;
        }).collect(Collectors.toList());
    }
}
