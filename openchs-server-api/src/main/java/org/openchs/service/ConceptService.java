package org.openchs.service;

import org.openchs.dao.CHSRepository;
import org.openchs.dao.ConceptAnswerRepository;
import org.openchs.dao.ConceptRepository;
import org.openchs.domain.CHSEntity;
import org.openchs.domain.Concept;
import org.openchs.domain.ConceptAnswer;
import org.openchs.domain.ConceptDataType;
import org.openchs.util.O;
import org.openchs.web.request.ConceptContract;
import org.openchs.web.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
public class ConceptService {
    private final Logger logger;
    private ConceptRepository conceptRepository;
    private ConceptAnswerRepository conceptAnswerRepository;

    @Autowired
    public ConceptService(ConceptRepository conceptRepository, ConceptAnswerRepository conceptAnswerRepository) {
        logger = LoggerFactory.getLogger(this.getClass());
        this.conceptRepository = conceptRepository;
        this.conceptAnswerRepository = conceptAnswerRepository;
    }

    private Concept fetchOrCreateConcept(String uuid) {
        Concept concept = conceptRepository.findByUuid(uuid);
        if (concept == null) {
            concept = createConcept(uuid);
        }
        return concept;
    }

    private Concept createConcept(String uuid) {
        Concept concept = new Concept();
        concept.setUuid(uuid);
        return concept;
    }

    private boolean conceptExistsWithSameNameAndDifferentUUID(ConceptContract conceptRequest) {
        Concept concept = conceptRepository.findByName(conceptRequest.getName());
        return concept != null && !concept.getUuid().equals(conceptRequest.getUuid());
    }

//    private Concept fetchOrCreateAnswer(ConceptContract answerConceptRequest) {
//        Concept answer = conceptRepository.findByUuid(answerConceptRequest.getUuid());
//        if (answer == null) {
//            answer = new Concept();
//            if (answerConceptRequest.getUuid() == null) {
//                answer.assignUUID();
//            } else {
//                answer.setUuid(answerConceptRequest.getUuid());
//            }
//            answer.setDataType(ConceptDataType.NA.toString());
//            if (StringUtils.isEmpty(answerConceptRequest.getName())) {
//                throw new ValidationException("Name missing for a new answer concept  " + answerConceptRequest.getUuid());
//            }
//        }
//        if (!StringUtils.isEmpty(answerConceptRequest.getName())) {
//            answer.setName(answerConceptRequest.getName());
//        }
//        conceptRepository.save(answer);
//        return answer;
//    }

    private ConceptAnswer fetchOrCreateConceptAnswer(Concept concept, ConceptContract answerConceptRequest, short answerOrder) {
        if (StringUtils.isEmpty(answerConceptRequest.getUuid())) {
            throw new ValidationException("UUID missing for answer");
        }
        ConceptAnswer conceptAnswer = concept.findConceptAnswerByConceptUUID(answerConceptRequest.getUuid());
        if (conceptAnswer == null) {
            conceptAnswer = new ConceptAnswer();
            conceptAnswer.assignUUID();
        }
        conceptAnswer.setAnswerConcept(map(answerConceptRequest));
        conceptAnswer.setVoided(answerConceptRequest.isVoided());
        conceptAnswer.setOrder(answerOrder);
        conceptAnswer.setAbnormal(answerConceptRequest.isAbnormal());
        return conceptAnswer;
    }

    private Concept createCodedConcept(Concept concept, ConceptContract conceptRequest) {
        List<ConceptContract> answers = (List<ConceptContract>) O.coalesce(conceptRequest.getAnswers(), new ArrayList<>());
        AtomicInteger index = new AtomicInteger(0);
        List<ConceptAnswer> conceptAnswers = answers.stream()
                .map(answerContract -> fetchOrCreateConceptAnswer(concept, answerContract, (short) index.incrementAndGet()))
                .collect(Collectors.toList());
        concept.addAll(conceptAnswers);
        return concept;
    }

    private Concept createNumericConcept(Concept concept, ConceptContract conceptRequest) {
        concept.setHighAbsolute(conceptRequest.getHighAbsolute());
        concept.setLowAbsolute(conceptRequest.getLowAbsolute());
        concept.setHighNormal(conceptRequest.getHighNormal());
        concept.setLowNormal(conceptRequest.getLowNormal());
        concept.setUnit(conceptRequest.getUnit());
        return concept;
    }

    private Concept map(@NotNull ConceptContract conceptRequest) {
        Concept concept = fetchOrCreateConcept(conceptRequest.getUuid());

        concept.setName(conceptRequest.getName() != null ? conceptRequest.getName() : concept.getName());
        concept.setDataType(conceptRequest.getDefaultDataType());
        concept.setVoided(conceptRequest.isVoided());

        switch (ConceptDataType.valueOf(conceptRequest.getDefaultDataType())) {
            case Coded:
                concept = createCodedConcept(concept, conceptRequest);
                break;
            case Numeric:
                concept = createNumericConcept(concept, conceptRequest);
                break;
        }
        return concept;
    }

    public Concept saveOrUpdate(ConceptContract conceptRequest) {
        if (conceptRequest == null) return null;
        if (conceptExistsWithSameNameAndDifferentUUID(conceptRequest)) {
            throw new RuntimeException(String.format("Concept %s exists with different uuid", conceptRequest.getName()));
        }
        logger.info(String.format("Creating concept: %s", conceptRequest.toString()));

        Concept concept = map(conceptRequest);

        return conceptRepository.save(concept);
    }

    public Concept get(String uuid) {
        return conceptRepository.findByUuid(uuid);
    }

    public ConceptAnswer getAnswer(String conceptUUID, String answerConceptUUID) {
        Concept concept = this.get(conceptUUID);
        Concept answerConcept = this.get(conceptUUID);
        return conceptAnswerRepository.findByConceptAndAnswerConcept(concept, answerConcept);
    }

    public Concept saveOrUpdate(Concept concept) {
        return conceptRepository.save(concept);
    }

}