package org.openchs.dao;

import org.joda.time.DateTime;
import org.openchs.domain.AddressLevel;
import org.openchs.domain.Catchment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(collectionResourceRel = "locations", path = "locations")
public interface LocationRepository extends ReferenceDataRepository<AddressLevel> {
    @RestResource(path = "byCatchmentAndLastModified", rel = "byCatchmentAndLastModified")
    Page<AddressLevel> findByVirtualCatchmentsIdAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(
            @Param("catchmentId") long catchmentId,
            @Param("lastModifiedDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime lastModifiedDateTime,
            @Param("now") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime now,
            Pageable pageable);

//    @RestResource(path = "find")
//    @Query(value = "select l.* from address_level l " +
//            "where (:ids is null or l.id in :ids) and (:q is null or (lower(l.title::varchar) like lower(concat('%', :q, '%')::varchar))) " +
//            " and (:type is null or (l.type_id = (select id from address_level_type where name = :type)))" +
//            " ORDER BY ?#{#pageable}",
//            countQuery = "select count(l.id) from address_level l " +
//                    "where (:ids is null or l.id in :ids) and (:q is null or (lower(l.title::varchar) like lower(concat('%', :q, '%')::varchar))) " +
//                    " and (:type is null or (l.type_id = (select id from address_level_type where name = :type)))",
//            nativeQuery = true)
    Page<AddressLevel> findByTitleIgnoreCaseContaining(String title, Pageable pageable);
//
//    @RestResource(path = "findByParentLocation", rel = "findByParentLocation")
//    @Query(value = "select al.* from address_level al " +
//            "   join location_location_mapping lm on lm.location_id = al.id " +
//            "where :parentLocationId is null OR (lm.parent_location_id = :parentLocationId) ", nativeQuery = true)
//    List<AddressLevel> findByParentLocationId(@Param("parentLocationId") Long parentLocationId);
//
    AddressLevel findByTitleAndCatchmentsUuid(String title, String uuid);

    List<AddressLevel> findByTitleAndLevelAndUuidNot(String title, Double level, String uuid);

    AddressLevel findByTitleIgnoreCase(String title);

    List<AddressLevel> findByCatchments(Catchment catchment);

    default AddressLevel findByName(String name) {
        throw new UnsupportedOperationException("No field 'name' in Location. Field 'title' not unique.");
    }

    default AddressLevel findByNameIgnoreCase(String name) {
        throw new UnsupportedOperationException("No field 'name' in Location. Field 'title' not unique.");
    }

    @RestResource(path = "findAllById", rel = "findAllById")
    Page<AddressLevel> findByIdIn(@Param("ids") Long[] ids, Pageable pageable);

    default Double getTopLevel() {
        return 4.0;
    };

    Page<AddressLevel> findByTitleIgnoreCaseContainingAndLevel(String title, Double defaultedLevel, Pageable pageable);

//    Page<AddressLevel> findByTitleIgnoreCaseContainingAndLevelAndParentLocationMappingsIdContains(String title, Double defaultedLevel, Long parentLocationId, Pageable pageable);

    Page<AddressLevel> findByParentLocationMappingsParentLocationIdIs(Long parentLocationId, Pageable pageable);

    Page<AddressLevel> findByLevel(Double defaultedLevel, Pageable pageable);

    Page<AddressLevel> findByTitleIgnoreCaseContainingAndParentLocationMappingsParentLocationIdIs(String kotma, Long parentLocationId, Pageable pageable);
}
