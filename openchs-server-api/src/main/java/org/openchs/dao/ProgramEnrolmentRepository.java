package org.openchs.dao;

import org.joda.time.DateTime;
import org.openchs.domain.Program;
import org.openchs.domain.ProgramEnrolment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(collectionResourceRel = "programEnrolment", path = "programEnrolment", exported = false)
@PreAuthorize("hasAnyAuthority('user','admin','organisation_admin')")
public interface ProgramEnrolmentRepository extends TransactionalDataRepository<ProgramEnrolment>, FindByLastModifiedDateTime<ProgramEnrolment>, OperatingIndividualScopeAwareRepository<ProgramEnrolment>, OperatingIndividualScopeAwareRepositoryWithTypeFilter<ProgramEnrolment> {
    Page<ProgramEnrolment> findByIndividualAddressLevelVirtualCatchmentsIdAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(
            long catchmentId,
            DateTime lastModifiedDateTime,
            DateTime now,
            Pageable pageable);

    Page<ProgramEnrolment> findByIndividualFacilityIdAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(
            long facilityId,
            DateTime lastModifiedDateTime,
            DateTime now,
            Pageable pageable);

    Page<ProgramEnrolment> findByIndividualAddressLevelVirtualCatchmentsIdAndProgramUuidAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(
            long catchmentId,
            String programUuid,
            DateTime lastModifiedDateTime,
            DateTime now,
            Pageable pageable);

    Page<ProgramEnrolment> findByIndividualFacilityIdAndProgramUuidAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(
            long facilityId,
            String programUuid,
            DateTime lastModifiedDateTime,
            DateTime now,
            Pageable pageable);

    List<ProgramEnrolment> findByProgram(Program program);

    @Override
    default Page<ProgramEnrolment> findByCatchmentIndividualOperatingScope(long catchmentId, DateTime lastModifiedDateTime, DateTime now, Pageable pageable) {
        return findByIndividualAddressLevelVirtualCatchmentsIdAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(catchmentId, lastModifiedDateTime, now, pageable);
    }

    @Override
    default Page<ProgramEnrolment> findByFacilityIndividualOperatingScope(long facilityId, DateTime lastModifiedDateTime, DateTime now, Pageable pageable) {
        return findByIndividualFacilityIdAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(facilityId, lastModifiedDateTime, now, pageable);
    }

    @Override
    default Page<ProgramEnrolment> findByCatchmentIndividualOperatingScopeAndFilterByType(long catchmentId, DateTime lastModifiedDateTime, DateTime now, String filter, Pageable pageable) {
        return findByIndividualAddressLevelVirtualCatchmentsIdAndProgramUuidAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(catchmentId, filter, lastModifiedDateTime, now, pageable);
    }

    @Override
    default Page<ProgramEnrolment> findByFacilityIndividualOperatingScopeAndFilterByType(long facilityId, DateTime lastModifiedDateTime, DateTime now, String filter, Pageable pageable) {
        return findByIndividualFacilityIdAndProgramUuidAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(facilityId, filter, lastModifiedDateTime, now, pageable);
    }

    @Query("select enl from ProgramEnrolment enl " +
            "join enl.individual i " +
            "where enl.program.uuid = :programUUID and enl.isVoided = false and " +
            "i.isVoided = false ")
    Page<ProgramEnrolment> findEnrolments(String programUUID, Pageable pageable);

    Page<ProgramEnrolment> findByAuditLastModifiedDateTimeIsBetweenAndProgramNameOrderByAuditLastModifiedDateTimeAscIdAsc(
            DateTime lastModifiedDateTime,
            DateTime now,
            String program,
            Pageable pageable);

    ProgramEnrolment findByLegacyId(String legacyId);
}
