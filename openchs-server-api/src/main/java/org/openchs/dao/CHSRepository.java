package org.openchs.dao;

import org.joda.time.DateTime;
import org.openchs.domain.CHSEntity;
import org.openchs.domain.Individual;

import javax.persistence.criteria.*;
import java.util.List;

public interface CHSRepository<T extends CHSEntity> {
    T findByUuid(String uuid);
    List<T> findAll();
    List<T> findAllByIsVoidedFalse();

    default Predicate jsonContains(Path<?> jsonb, String pattern, CriteriaBuilder builder) {
        return builder.isTrue(builder.function("jsonb_object_values_contain", Boolean.class,
                jsonb, builder.literal(pattern)));
    }

    default Predicate observationsdata(Path<?> uuid, String uuidKey, String valueAns, String searchIn, CriteriaBuilder builder) {
        return builder.isTrue(builder.function("jsonb_object_values_contain_single_coded", Boolean.class,
                uuid, builder.literal(uuidKey),builder.literal(valueAns), builder.literal(searchIn)));
    }


}