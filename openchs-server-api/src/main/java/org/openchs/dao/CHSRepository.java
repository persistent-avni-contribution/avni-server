package org.openchs.dao;

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
    /*default Predicate toPredicate(Root<Individual> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        final Predicate appPredicate = root.join("programEnrolments", JoinType.LEFT)
                .get("individualId").isNotNull();
        query.distinct(true);
        return appPredicate;
    }*/
}