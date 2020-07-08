package org.openchs.dao;

import jdk.nashorn.internal.parser.DateParser;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.openchs.domain.*;
import org.openchs.web.request.search.Concepts;
import org.openchs.web.request.search.IndividualSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import software.amazon.ion.impl.PrivateScalarConversions;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.openchs.domain.OperatingIndividualScope.ByCatchment;
import static org.openchs.domain.OperatingIndividualScope.ByFacility;
import static org.springframework.data.jpa.domain.Specification.where;

@Repository
@RepositoryRestResource(collectionResourceRel = "individual", path = "individual", exported = false)
@PreAuthorize("hasAnyAuthority('user','admin','organisation_admin')")
public interface IndividualRepository extends TransactionalDataRepository<Individual>, OperatingIndividualScopeAwareRepository<Individual>, OperatingIndividualScopeAwareRepositoryWithTypeFilter<Individual> {
    Page<Individual> findByAuditLastModifiedDateTimeIsBetweenAndIsVoidedFalseOrderByAuditLastModifiedDateTimeAscIdAsc(
            DateTime lastModifiedDateTime,
            DateTime now,
            Pageable pageable);

    Page<Individual> findByAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(DateTime lastModifiedDateTime,
                                                                                                      DateTime now,
                                                                                                      Pageable pageable);

    Page<Individual> findByAuditLastModifiedDateTimeIsBetweenAndSubjectTypeNameOrderByAuditLastModifiedDateTimeAscIdAsc(DateTime lastModifiedDateTime,
                                                                                                      DateTime now,
                                                                                                      String subjectType,
                                                                                                      Pageable pageable);

    Page<Individual> findByAddressLevelVirtualCatchmentsIdAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(
            long catchmentId,
            DateTime lastModifiedDateTime,
            DateTime now,
            Pageable pageable);

    Page<Individual> findByFacilityIdAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(
            long facilityId,
            DateTime lastModifiedDateTime,
            DateTime now,
            Pageable pageable);

    Page<Individual> findByAddressLevelVirtualCatchmentsIdAndSubjectTypeUuidAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(
            long catchmentId,
            String subjectTypeUuid,
            DateTime lastModifiedDateTime,
            DateTime now,
            Pageable pageable);

    Page<Individual> findByFacilityIdAndSubjectTypeUuidAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(
            long facilityId,
            String subjectTypeUuid,
            DateTime lastModifiedDateTime,
            DateTime now,
            Pageable pageable);

    @Override
    default Page<Individual> findByCatchmentIndividualOperatingScope(long catchmentId, DateTime lastModifiedDateTime, DateTime now, Pageable pageable) {
        return findByAddressLevelVirtualCatchmentsIdAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(catchmentId, lastModifiedDateTime, now, pageable);
    }

    @Override
    default Page<Individual> findByFacilityIndividualOperatingScope(long facilityId, DateTime lastModifiedDateTime, DateTime now, Pageable pageable) {
        return findByFacilityIdAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(facilityId, lastModifiedDateTime, now, pageable);
    }

    @Override
    default Page<Individual> findByCatchmentIndividualOperatingScopeAndFilterByType(long catchmentId, DateTime lastModifiedDateTime, DateTime now, String filters, Pageable pageable) {
        return findByAddressLevelVirtualCatchmentsIdAndSubjectTypeUuidAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(catchmentId, filters, lastModifiedDateTime, now, pageable);
    }

    @Override
    default Page<Individual> findByFacilityIndividualOperatingScopeAndFilterByType(long facilityId, DateTime lastModifiedDateTime, DateTime now, String filters, Pageable pageable) {
        return findByFacilityIdAndSubjectTypeUuidAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(facilityId, filters, lastModifiedDateTime, now, pageable);
    }

    /*default Specification<Individual> getFilterSpecForVoid(IndividualSearchRequest individualSearchRequest) {


        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                !individualSearchRequest.isIncludeVoided()  ? cb.and() : cb.or( cb.isTrue(root.get("isVoided")));

    }*/
    default Specification<Individual> getFilterSpecForVoid(IndividualSearchRequest individualSearchRequest) {
        Boolean includeVoided=individualSearchRequest.isIncludeVoided();
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                includeVoided ? cb.or(cb.isTrue(root.get("isVoided")),cb.isFalse(root.get("isVoided"))) :
                        cb.isFalse(root.get("isVoided"))  ;
    }


    default Specification<Individual> getFilterSpecForName(IndividualSearchRequest individualSearchRequest) {
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
           String value  = individualSearchRequest.getName();
            if (value != null && !"".equals(value.trim())){
                Predicate[] predicates = new Predicate[2];
                String[] values = value.trim().split(" ");
                if (values.length > 0) {
                    predicates[0] = cb.like(cb.upper(root.get("firstName")),  values[0].toUpperCase() + "%");
                    predicates[1] = cb.like(cb.upper(root.get("lastName")),  values[0].toUpperCase() + "%");
                }
                if (values.length > 1) {
                    predicates[1] = cb.like(cb.upper(root.get("lastName")),  values[1].toUpperCase() + "%");
                }
                return cb.or(predicates[0], predicates[1]);
            }
            return null;
        };
    }
    default Specification<Individual> getFilterSpecForNameOld(String individualSearchRequest) {
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            String value  = individualSearchRequest;
            if (value != null && !"".equals(value.trim())){
                Predicate[] predicates = new Predicate[2];
                String[] values = value.trim().split(" ");
                if (values.length > 0) {
                    predicates[0] = cb.like(cb.upper(root.get("firstName")),  values[0].toUpperCase() + "%");
                    predicates[1] = cb.like(cb.upper(root.get("lastName")),  values[0].toUpperCase() + "%");
                }
                if (values.length > 1) {
                    predicates[1] = cb.like(cb.upper(root.get("lastName")),  values[1].toUpperCase() + "%");
                }
                return cb.or(predicates[0], predicates[1]);
            }
            return null;
        };
    }


    default Specification<Individual> getFilterSpecForObs(IndividualSearchRequest individualSearchRequest) {
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
        {
            if (null == individualSearchRequest && null == individualSearchRequest.getConcept())
                return null;
            else {
                List<Concepts> conceptList=individualSearchRequest.getConcept();
                List<Predicate> individualPredicates = new ArrayList<>();
                    for(Concepts concept:conceptList) {
                        if("CODED".equalsIgnoreCase(concept.getDataType()) && "INDIVIDUAL".equalsIgnoreCase(concept.getSearchScope()))
                            individualPredicates.add(findInObservationSingleCoded(root.get("uuid"), concept.getUuid(), concept.getValue(), "INDIVIDUAL", "CODED", cb));
                         if("MULTICODED".equalsIgnoreCase(concept.getDataType()) && "INDIVIDUAL".equalsIgnoreCase(concept.getSearchScope()))
                            individualPredicates.add(findInObservationMultiCoded(root.get("uuid"), concept.getUuid(), concept.getValues(), "INDIVIDUAL", cb));
                         else if("CODED".equalsIgnoreCase(concept.getDataType()) && "PROGRAMENROLMENT".equalsIgnoreCase(concept.getSearchScope())) {
                            individualPredicates.add(findInObservationSingleCoded(root.join("programEnrolments", JoinType.INNER).get("uuid"), concept.getUuid(), concept.getValue(), "PROGRAMEENROLMENT", "CODED", cb));

                         }
                    }
                 //jsonContains(root.get("observations"), value , cb),
                //jsonContains(root.join("programEnrolments", JoinType.LEFT).get("observations"),  value , cb),
                // jsonContains(root.join("encounters", JoinType.LEFT).get("observations"),  value , cb));
                return cb.or( individualPredicates.toArray(new Predicate[0]));
            }

        };
    }

    default Specification<Individual> getFilterSpecForIndividualType(IndividualSearchRequest individualSearchRequest) {

        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                individualSearchRequest.getSubjectType() == null ? null : root.get("subjectType").get("uuid").in(individualSearchRequest.getSubjectType());
    }

    default Specification<Individual> getFilterSpecForGender(IndividualSearchRequest individualSearchRequest) {
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                individualSearchRequest.getGender() == null ? null : root.get("gender").get("uuid").in(individualSearchRequest.getGender());
    }

    default Specification<Individual> getFilterSpecForLocationIds(IndividualSearchRequest individualSearchRequest) {
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                individualSearchRequest.getAddressIds() == null ? null : root.get("addressLevel").get("id").in(individualSearchRequest.getAddressIds());
    }

    default Specification<Individual> getFilterSpecForAddress(String locationName) {
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                locationName == null ? cb.and() :
                        cb.like(cb.upper(root.get("addressLevel").get("titleLineage")), "%" + locationName.toUpperCase() + "%");
    }

    default Specification<Individual> getFilterSpecForAgeRange(IndividualSearchRequest  individualSearchRequest) {
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (null != individualSearchRequest.getAge()) {
                if (null != individualSearchRequest.getAge().getMaxValueInt() && null != individualSearchRequest.getAge().getMinValueInt()) {
                    return cb.and(cb.greaterThanOrEqualTo(root.get("individualAge"), individualSearchRequest.getAge().getMinValueInt())
                            , cb.lessThanOrEqualTo(root.get("individualAge"), individualSearchRequest.getAge().getMaxValueInt()));
                } else if (null != individualSearchRequest.getAge().getMinValueInt())
                    return cb.greaterThanOrEqualTo(root.get("individualAge"), individualSearchRequest.getAge().getMinValueInt());
            }
            return null;
        };
    }

    default Specification<Individual> getFilterSpecForRegistrationDateRange(IndividualSearchRequest  individualSearchRequest) {
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
        {

                if(null!=individualSearchRequest.getRegistrationDate()) {
                    if (individualSearchRequest.getRegistrationDate().getMinValue() != null && individualSearchRequest.getRegistrationDate().getMaxValue() != null)
                        return cb.and(
                                cb.greaterThanOrEqualTo(root.get("registrationDate"), individualSearchRequest.getRegistrationDate().getMinValue())
                                , cb.lessThanOrEqualTo(root.get("registrationDate"), individualSearchRequest.getRegistrationDate().getMaxValue())
                        );
                    else if (individualSearchRequest.getRegistrationDate().getMinValue() != null)

                        return cb.greaterThanOrEqualTo(root.get("registrationDate"), individualSearchRequest.getRegistrationDate().getMinValue());

                }
            return null ;
        };
    }

    default Specification<Individual> getFilterSpecForProgramEnrolmentDateRange(IndividualSearchRequest  individualSearchRequest) {
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if(null!=individualSearchRequest.getEnrolmentDate()) {
                if( null != individualSearchRequest.getEnrolmentDate().getMaxValue() && null != individualSearchRequest.getEnrolmentDate().getMinValue()) {
                    return cb.and(cb.greaterThanOrEqualTo(root.join("programEnrolments", JoinType.LEFT).get("enrolmentDateTime").as(Date.class),
                            individualSearchRequest.getEnrolmentDate().getMinValue().toDate())
                            , cb.lessThanOrEqualTo(root.join("programEnrolments", JoinType.LEFT).get("enrolmentDateTime").as(Date.class),
                                    individualSearchRequest.getEnrolmentDate().getMaxValue().toDate()));
                }
                else if(null != individualSearchRequest.getEnrolmentDate().getMinValue())
                    return cb.greaterThanOrEqualTo(root.join("programEnrolments", JoinType.LEFT).get("enrolmentDateTime").as(Date.class),
                            individualSearchRequest.getEnrolmentDate().getMinValue().toDate());
            }
            return null;
        };
    }

    default Specification<Individual> getFilterSpecForProgramEncounterDateRange(IndividualSearchRequest  individualSearchRequest) {
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if(null!=individualSearchRequest.getProgramEncounterDate()) {
                if(null != individualSearchRequest.getProgramEncounterDate().getMaxValue() && null != individualSearchRequest.getProgramEncounterDate().getMinValue()) {
                    return cb.and(
                            cb.greaterThanOrEqualTo(root.join("programEnrolments", JoinType.LEFT).join("programEncounters", JoinType.LEFT).get("encounterDateTime").as(Date.class),
                                    individualSearchRequest.getProgramEncounterDate().getMinValue().toDate())
                            , cb.lessThanOrEqualTo(root.join("programEnrolments", JoinType.LEFT).join("programEncounters", JoinType.LEFT).get("encounterDateTime").as(Date.class),
                                    individualSearchRequest.getProgramEncounterDate().getMaxValue().toDate()));
                }
                        else if(null != individualSearchRequest.getProgramEncounterDate().getMinValue())
                        return cb.greaterThanOrEqualTo(root.join("programEnrolments", JoinType.LEFT).join("programEncounters", JoinType.LEFT).get("encounterDateTime").as(Date.class),
                                individualSearchRequest.getProgramEncounterDate().getMinValue().toDate());
                }
                return null;
            };
        }

    default Specification<Individual> getFilterSpecForEncounterDateRange(IndividualSearchRequest  individualSearchRequest) {
        return (Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if(null!= individualSearchRequest.getEncounterDate()) {
                if (null != individualSearchRequest.getEncounterDate().getMaxValue() && null != individualSearchRequest.getEncounterDate().getMinValue()) {
                    return cb.and(
                            cb.greaterThanOrEqualTo(root.join("encounters", JoinType.LEFT).get("encounterDateTime").as(Date.class),
                                    individualSearchRequest.getEncounterDate().getMinValue().toDate())
                            , cb.lessThanOrEqualTo(root.join("encounters", JoinType.LEFT).get("encounterDateTime").as(Date.class),
                                    individualSearchRequest.getEncounterDate().getMaxValue().toDate()));
                }
                else if (null != individualSearchRequest.getEncounterDate().getMinValue())
                    cb.greaterThanOrEqualTo(root.join("encounters", JoinType.LEFT).get("encounterDateTime").as(Date.class),
                            individualSearchRequest.getEncounterDate().getMinValue().toDate());
            }
            return null;
        };
    }

    @Override
    default Specification<Individual> getFilterSpecForOperatingSubjectScope(User user) {
        OperatingIndividualScope scope = user.getOperatingIndividualScope();
        Facility facility = user.getFacility();
        Catchment catchment = user.getCatchment();
        if (ByCatchment.equals(scope)) {
            return (root, query, cb) ->
                    root.join("addressLevel")
                            .joinSet("virtualCatchments").get("id").in(catchment.getId());
        }
        if (ByFacility.equals(scope)) {
            return (root, query, cb) -> root.join("facility").get("id").in(facility.getId());
        }
        return (r, q, cb) -> cb.and();
    }

    @Query("select ind from Individual ind " +
            "where ind.isVoided = false " +
            "and ind.subjectType.uuid = :subjectTypeUUID")
    Page<Individual> findIndividuals(String subjectTypeUUID, Pageable pageable);

    Individual findByLegacyId(String legacyId);

}
