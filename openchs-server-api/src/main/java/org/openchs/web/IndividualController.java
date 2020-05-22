package org.openchs.web;

import org.joda.time.DateTime;
import org.openchs.dao.*;
import org.openchs.domain.AddressLevel;
import org.openchs.domain.Gender;
import org.openchs.domain.Individual;
import org.openchs.domain.SubjectType;
import org.openchs.geo.Point;
import org.openchs.projection.IndividualWebProjection;
import org.openchs.service.*;
import org.openchs.util.S;
import org.openchs.web.request.IndividualContract;
import org.openchs.web.request.IndividualRequest;
import org.openchs.web.request.PointRequest;
import org.openchs.web.response.ResponsePage;
import org.openchs.web.response.SubjectResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.jpa.domain.Specifications.where;

@RestController
public class IndividualController extends AbstractController<Individual> implements RestControllerResourceProcessor<Individual>, OperatingIndividualScopeAwareController<Individual>, OperatingIndividualScopeAwareFilterController<Individual> {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(IndividualController.class);
    private final IndividualRepository individualRepository;
    private final UserService userService;
    private final ProjectionFactory projectionFactory;
    private final IndividualService individualService;
    private ConceptRepository conceptRepository;
    private ConceptService conceptService;
    private final EncounterService encounterService;

    @Autowired
    public IndividualController(IndividualRepository individualRepository, UserService userService, ProjectionFactory projectionFactory, IndividualService individualService, ConceptRepository conceptRepository, ConceptService conceptService,EncounterService encounterService) {
        this.individualRepository = individualRepository;
        this.userService = userService;
        this.projectionFactory = projectionFactory;
        this.individualService = individualService;
        this.conceptRepository = conceptRepository;
        this.conceptService = conceptService;
        this.encounterService = encounterService;
    }

    @RequestMapping(value = "/api/subjects", method = RequestMethod.GET)
    @PreAuthorize(value = "hasAnyAuthority('user')")
    public ResponsePage getSubjects(@RequestParam("lastModifiedDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime lastModifiedDateTime,
                                    @RequestParam("now") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime now,
                                    @RequestParam(value = "subjectType", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String subjectType,
                                    Pageable pageable) {
        Page<Individual> subjects;
        boolean subjectTypeRequested = S.isEmpty(subjectType);
        if (subjectTypeRequested) {
            subjects = individualRepository.findByAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(lastModifiedDateTime, now, pageable);
        } else
            subjects = individualRepository.findByAuditLastModifiedDateTimeIsBetweenAndSubjectTypeNameOrderByAuditLastModifiedDateTimeAscIdAsc(lastModifiedDateTime, now, subjectType, pageable);
        ArrayList<SubjectResponse> subjectResponses = new ArrayList<>();
        subjects.forEach(subject -> {
            subjectResponses.add(SubjectResponse.fromSubject(subject, subjectTypeRequested, conceptRepository, conceptService));
        });
        return new ResponsePage(subjectResponses, subjects.getNumberOfElements(), subjects.getTotalPages(), subjects.getSize());
    }

    @GetMapping(value = "/api/subject/{id}")
    @PreAuthorize(value = "hasAnyAuthority('user')")
    @ResponseBody
    public ResponseEntity<SubjectResponse> get(@PathVariable("id") String uuid) {
        Individual subject = individualRepository.findByUuid(uuid);
        if (subject == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(SubjectResponse.fromSubject(subject, true, conceptRepository, conceptService), HttpStatus.OK);
    }

    @RequestMapping(value = "/individuals", method = RequestMethod.POST)
    @Transactional
    @PreAuthorize(value = "hasAnyAuthority('user', 'organisation_admin')")
    public void save(@RequestBody IndividualRequest individualRequest) {
        if(individualRequest.getVisitSchedules() != null && individualRequest.getVisitSchedules().size() > 0) {
            encounterService.saveVisitSchedules(individualRequest.getUuid(),individualRequest.getVisitSchedules());
        }
        individualService.saveIndividual(individualRequest);
    }

    @GetMapping(value = {"/individual", /*-->Both are Deprecated */ "/individual/search/byCatchmentAndLastModified", "/individual/search/lastModified"})
    @PreAuthorize(value = "hasAnyAuthority('user', 'organisation_admin')")
    public PagedResources<Resource<Individual>> getIndividualsByOperatingIndividualScope(
            @RequestParam("lastModifiedDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime lastModifiedDateTime,
            @RequestParam("now") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime now,
            @RequestParam(value = "subjectTypeUuid", required = false) String subjectTypeUuid,
            Pageable pageable) {
        if (subjectTypeUuid == null) {
            return wrap(getCHSEntitiesForUserByLastModifiedDateTime(userService.getCurrentUser(), lastModifiedDateTime, now, pageable));
        } else {
            return subjectTypeUuid.isEmpty() ? wrap(new PageImpl<>(Collections.emptyList())) :
                    wrap(getCHSEntitiesForUserByLastModifiedDateTimeAndFilterByType(userService.getCurrentUser(), lastModifiedDateTime, now, subjectTypeUuid, pageable));
        }
    }

    @GetMapping(value = "/individual/search")
    @PreAuthorize(value = "hasAnyAuthority('user', 'organisation_admin')")
    @ResponseBody
    public Page<IndividualWebProjection> search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "includeVoided", defaultValue = "false") Boolean includeVoided,
            @RequestParam(value = "obs", required = false) String obs,
            @RequestParam(value = "locationIds", required = false) List<Long> locationIds,
            Pageable pageable) {
        IndividualRepository repo = this.individualRepository;
        if (query != null && !"".equals(query.trim())) {
            return repo.findAll(
                            where(repo.getFilterSpecForAddress(query))
                                    .or(repo.getFilterSpecForObs(query))
                                    .or(repo.getFilterSpecForName(query))
                    , pageable)
                    .map(t -> projectionFactory.createProjection(IndividualWebProjection.class, t));
        }
        return repo.findAll(
                where(repo.getFilterSpecForVoid(includeVoided))
                        .and(repo.getFilterSpecForName(name))
                        .and(repo.getFilterSpecForObs(obs))
                        .and(repo.getFilterSpecForLocationIds(locationIds))
                , pageable)
                .map(t -> projectionFactory.createProjection(IndividualWebProjection.class, t));
    }

    @GetMapping(value = "/web/individual/{uuid}")
    @PreAuthorize(value = "hasAnyAuthority('user')")
    @ResponseBody
    public IndividualWebProjection getOneForWeb(@PathVariable String uuid) {
        return projectionFactory.createProjection(IndividualWebProjection.class, individualRepository.findByUuid(uuid));
    }

    @GetMapping(value = "/web/subjectProfile")
    @PreAuthorize(value = "hasAnyAuthority('user')")
    @ResponseBody
    public ResponseEntity<IndividualContract> getSubjectProfile(@RequestParam("uuid") String uuid) {
        IndividualContract individualContract = individualService.getSubjectInfo(uuid);
        return ResponseEntity.ok(individualContract);
    }

    @GetMapping(value = "/web/subject/{subjectUuid}/programs")
    @PreAuthorize(value = "hasAnyAuthority('user')")
    @ResponseBody
    public ResponseEntity<IndividualContract> getSubjectProgramEnrollment(@PathVariable("subjectUuid") String uuid) {
        IndividualContract individualEnrolmentContract = individualService.getSubjectProgramEnrollment(uuid);
        if (individualEnrolmentContract == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(individualEnrolmentContract);
    }

    @GetMapping(value = "/web/subject/{subjectUuid}/eligiblePrograms")
    @PreAuthorize(value = "hasAnyAuthority('user')")
    @ResponseBody
    public ResponseEntity<IndividualContract> getEligiblePrograms(@PathVariable("subjectUuid") String uuid) {
        IndividualContract individualEnrolmentContract = individualService.getSubjectProgramEnrollment(uuid);
        if (individualEnrolmentContract == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(individualEnrolmentContract);
    }

    @GetMapping(value = "/web/subject/{uuid}/encounters")
    @PreAuthorize(value = "hasAnyAuthority('user')")
    @ResponseBody
    public ResponseEntity<IndividualContract> getSubjectEncounters(@PathVariable("uuid") String uuid) {
        IndividualContract individualEncounterContract = individualService.getSubjectEncounters(uuid);
        if (individualEncounterContract == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(individualEncounterContract);
    }

    @Override
    public Resource<Individual> process(Resource<Individual> resource) {
        Individual individual = resource.getContent();
        resource.removeLinks();
        resource.add(new Link(individual.getAddressLevel().getUuid(), "addressUUID"));
        if (individual.getGender() != null) {
            resource.add(new Link(individual.getGender().getUuid(), "genderUUID"));
        }
        if (individual.getSubjectType() != null) {
            resource.add(new Link(individual.getSubjectType().getUuid(), "subjectTypeUUID"));
        }
        return resource;
    }

    @Override
    public OperatingIndividualScopeAwareRepository<Individual> resourceRepository() {
        return individualRepository;
    }





    @Override
    public OperatingIndividualScopeAwareRepositoryWithTypeFilter<Individual> repository() {
        return individualRepository;
    }
}
