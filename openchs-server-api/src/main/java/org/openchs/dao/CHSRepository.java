package org.openchs.dao;

import org.openchs.domain.AddressLevel;
import org.openchs.domain.CHSEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CHSRepository<T extends CHSEntity> {
    T findByUuid(String uuid);
    List<T> findAll();
    Page<T> findById(Long id, Pageable pageable);
}