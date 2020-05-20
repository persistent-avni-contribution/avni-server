package org.openchs.web;

import org.codehaus.jettison.json.JSONException;
import org.openchs.framework.security.UserContextHolder;
import org.openchs.service.RuleService;
import org.openchs.web.request.RuleDependencyRequest;
import org.openchs.web.request.RuleRequest;
import org.openchs.web.request.rules.constant.WorkFlowTypeEnum;
import org.openchs.web.request.rules.request.RequestEntityWrapper;
import org.openchs.web.request.rules.response.RuleResponseEntity;
import org.openchs.web.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class RuleController {
    private final Logger logger;
    private final RuleService ruleService;

    @Autowired
    public RuleController(RuleService ruleService) {
        logger = LoggerFactory.getLogger(this.getClass());
        this.ruleService = ruleService;
    }

    @RequestMapping(value = "/ruleDependency", method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyAuthority('organisation_admin')")
    public ResponseEntity<?> saveDependency(@RequestBody RuleDependencyRequest ruleDependency) {
        logger.info(String.format("Creating rule dependency for: %s", UserContextHolder.getUserContext().getOrganisation().getName()));
        return new ResponseEntity<>(ruleService.createDependency(ruleDependency.getCode(), ruleDependency.getHash()).getUuid(),
                HttpStatus.CREATED);
    }

    @RequestMapping(value = "/rules", method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyAuthority('organisation_admin')")
    public ResponseEntity<?> saveRules(@RequestBody List<RuleRequest> ruleRequests) {
        logger.info(String.format("Creating rules for: %s", UserContextHolder.getUserContext().getOrganisation().getName()));
        try {
            ruleService.createOrUpdate(ruleRequests);
        } catch (ValidationException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/rule", method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyAuthority('organisation_admin')")
    public ResponseEntity<?> saveRule(@RequestBody RuleRequest ruleRequest) {
        logger.info(String.format("Creating rules for: %s", UserContextHolder.getUserContext().getOrganisation().getName()));
        ruleService.createOrUpdate(ruleRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/web/decisionrule", method = RequestMethod.POST)
    ResponseEntity<?> decisionRules(@RequestBody RequestEntityWrapper requestEntityWrapper) throws IOException, JSONException {
        RuleResponseEntity ruleResponseEntity = null;
        if(requestEntityWrapper.getRule().getWorkFlowType() != null) {
            switch (WorkFlowTypeEnum.findByValue(requestEntityWrapper.getRule().getWorkFlowType().toLowerCase())) {
                case INDIVIDUAL:
                    ruleResponseEntity = ruleService.decisionRuleIndividualWorkFlow(requestEntityWrapper);
                    break;
                case PROGRAM_ENCOUNTER:
                    ruleResponseEntity = ruleService.decisionRuleProgramEncounterWorkFlow(requestEntityWrapper);
                    break;
                case PROGRAM_ENROLMENT:
                    ruleResponseEntity = ruleService.decisionRuleProgramEnrolmentWorkFlow(requestEntityWrapper);
                    break;
                case ENCOUNTER:
                    ruleResponseEntity = ruleService.decisionRuleEncounterWorkFlow(requestEntityWrapper);
                    break;
            }
        }
        if(ruleResponseEntity.getStatus().equalsIgnoreCase("success")) {
            return ResponseEntity.ok().body(ruleResponseEntity);
        }else{
            return ResponseEntity.badRequest().body(ruleResponseEntity);
        }
    }

    @RequestMapping(value = "/web/visitrule", method = RequestMethod.POST)
    ResponseEntity<?> visitScheduleRules(@RequestBody RequestEntityWrapper requestEntityWrapper) throws IOException, JSONException {
        RuleResponseEntity ruleResponseEntity = null;
        if(requestEntityWrapper.getRule().getWorkFlowType() != null) {
            switch (WorkFlowTypeEnum.findByValue(requestEntityWrapper.getRule().getWorkFlowType().toLowerCase())) {
                case PROGRAM_ENROLMENT:
                    ruleResponseEntity = ruleService.visitScheduleRuleProgramEnrolmentWorkFlow(requestEntityWrapper);
                    break;
                case PROGRAM_ENCOUNTER:
                    ruleResponseEntity = ruleService.visitScheduleRuleProgramEncounterWorkFlow(requestEntityWrapper);
                    break;
                /* Encounter VisitSchedule , Not in scope of 2nd Release
                case "encounter":
                    ruleResponseEntity = ruleService.visitScheduleRuleEncounterWorkFlow(requestEntityWrapper);
                    break;
                */
            }
        }
        if(ruleResponseEntity.getStatus().equalsIgnoreCase("success")) {
            return ResponseEntity.ok().body(ruleResponseEntity);
        }else{
            return ResponseEntity.badRequest().body(ruleResponseEntity);
        }
    }

    @RequestMapping(value = "/web/validationrule", method = RequestMethod.POST)
    ResponseEntity<?> validationRules(@RequestBody RequestEntityWrapper requestEntityWrapper) throws IOException, JSONException {
        RuleResponseEntity ruleResponseEntity = null;
        if (requestEntityWrapper.getRule().getWorkFlowType() != null) {
            switch (requestEntityWrapper.getRule().getWorkFlowType().toLowerCase()) {
                case "individual":
                   ruleResponseEntity = ruleService.validationRuleIndividualWorkFlow(requestEntityWrapper);
                    break;
                case "programenrolment":
                    ruleResponseEntity = ruleService.validationRuleProgramEnrolmentWorkFlow(requestEntityWrapper);
                    break;
                case "programencounter":
                    ruleResponseEntity = ruleService.validationRuleProgramEncounterWorkFlow(requestEntityWrapper);
                    break;
                case "encounter":
                    ruleResponseEntity = ruleService.validationRuleEncounterWorkFlow(requestEntityWrapper);
                    break;
            }
        }
        if (ruleResponseEntity.getStatus().equalsIgnoreCase("success")) {
            return ResponseEntity.ok().body(ruleResponseEntity);
        } else {
            return ResponseEntity.badRequest().body(ruleResponseEntity);
        }
    }

    @RequestMapping(value = "/web/eligibiltycheckrule", method = RequestMethod.POST)
    ResponseEntity<?> programEnrolmentCheckRules(@RequestBody RequestEntityWrapper requestEntityWrapper) throws IOException, JSONException {
        RuleResponseEntity ruleResponseEntity = null;
        if (requestEntityWrapper.getRule().getWorkFlowType() != null) {
            switch (requestEntityWrapper.getRule().getWorkFlowType().toLowerCase()) {
                case "programenrolment":
                    ruleResponseEntity = ruleService.programEnrolmentCheckRuleWorkFlow(requestEntityWrapper);
                    break;
                case "encounter":
                    ruleResponseEntity = ruleService.encounterCheckRuleWorkFlow(requestEntityWrapper);
                    break;
            }
        }

        if (ruleResponseEntity.getStatus().equalsIgnoreCase("success")) {
            return ResponseEntity.ok().body(ruleResponseEntity);
        } else {
            return ResponseEntity.badRequest().body(ruleResponseEntity);
        }
    }

}
