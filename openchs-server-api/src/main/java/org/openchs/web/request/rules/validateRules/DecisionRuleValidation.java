package org.openchs.web.request.rules.validateRules;

import org.openchs.dao.ConceptRepository;
import org.openchs.dao.individualRelationship.RuleFailureLogRepository;
import org.openchs.domain.Concept;
import org.openchs.domain.RuleFailureLog;
import org.openchs.web.request.ConceptContract;
import org.openchs.web.request.ObservationContract;
import org.openchs.web.request.rules.request.RequestEntityWrapper;
import org.openchs.web.request.rules.response.DecisionResponse;
import org.openchs.web.request.rules.response.Decisions;
import org.openchs.web.request.rules.response.RuleResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DecisionRuleValidation {
    private final Logger logger;
    private final ConceptRepository conceptRepository;
    private final RuleFailureLogRepository ruleFailureLogRepository;

    @Autowired
    public DecisionRuleValidation(
                       ConceptRepository conceptRepository,
                       RuleFailureLogRepository ruleFailureLogRepository) {
        logger = LoggerFactory.getLogger(this.getClass());
        this.conceptRepository = conceptRepository;
        this.ruleFailureLogRepository = ruleFailureLogRepository;
    }


    public RuleFailureLog generateRuleFailureLog(RequestEntityWrapper requestEntityWrapper, String source, String entityType, String entityUuid){
        RuleFailureLog ruleFailureLog = new RuleFailureLog();
        ruleFailureLog.setFormId(requestEntityWrapper.getRule().getFormUuid());
        ruleFailureLog.setRuleType(requestEntityWrapper.getRule().getRuleType());
        ruleFailureLog.setEntityId(entityUuid);
        ruleFailureLog.setEntityType(entityType);
        ruleFailureLog.setSource(source);
        return ruleFailureLog;
    }

    public <T> ArrayList<T> validateDecision(List< ? extends Decisions> decisionResponse, RuleFailureLog ruleFailureLog){
        ArrayList<T> decisionValidation = (ArrayList<T>) decisionResponse.stream().filter(decision -> checkConceptForRule(decision.getName(),ruleFailureLog)).map(
                decisionValue -> {
                    decisionValue.setValue(validateDecisionValue((ArrayList<String>) decisionValue.getValue(), ruleFailureLog));
                    return decisionValue;
                }
        ).collect(Collectors.toList());
        return decisionValidation;
    }


    private <T> ArrayList<T> validateDecisionValue(ArrayList<T> decisionValue,RuleFailureLog ruleFailureLog) {
        ArrayList<T> validateDecisionList = new ArrayList<>();
        decisionValue.stream().
                forEach(value -> {
                    if (checkConceptForRule(value.toString(),ruleFailureLog)) {
                        validateDecisionList.add(value);
                    }
                });
        return validateDecisionList;
    }

    private Boolean checkConceptForRule(String conceptName,RuleFailureLog ruleFailureLog) {
        try {
            Concept concept = conceptRepository.findByName(conceptName);
            if(concept != null)
                return true;
        } catch (Exception e) {
            ruleFailureLog.setErrorMessage(e.getMessage() != null ? e.getMessage() : "");
            ruleFailureLog.setStacktrace(e.getStackTrace().toString());
            ruleFailureLog.setUuid(UUID.randomUUID().toString());
            ruleFailureLogRepository.save(ruleFailureLog);
            return false;
        }
        return false;
    }
}