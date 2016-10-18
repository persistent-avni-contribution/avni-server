package org.openchs.server.dao;

import org.openchs.server.domain.Concept;
import org.openchs.server.domain.Individual;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;

@Transactional
public interface ConceptRepository extends PagingAndSortingRepository<Concept, Long> {
}