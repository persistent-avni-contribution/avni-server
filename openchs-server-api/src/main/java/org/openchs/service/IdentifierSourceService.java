package org.openchs.service;

import org.openchs.dao.CatchmentRepository;
import org.openchs.dao.IdentifierSourceRepository;
import org.openchs.domain.Catchment;
import org.openchs.domain.IdentifierSource;
import org.openchs.domain.JsonObject;
import org.openchs.web.request.webapp.IdentifierSourceContractWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdentifierSourceService {
    private final CatchmentRepository catchmentRepository;
    private final IdentifierSourceRepository identifierSourceRepository;

    @Autowired
    public IdentifierSourceService(CatchmentRepository catchmentRepository, IdentifierSourceRepository identifierSourceRepository) {
        this.catchmentRepository = catchmentRepository;
        this.identifierSourceRepository = identifierSourceRepository;
    }

    public IdentifierSource saveIdSource(IdentifierSourceContractWeb request) {
        IdentifierSource identifierSource = identifierSourceRepository.findByUuid(request.getUUID());
        if (identifierSource == null) {
            identifierSource = new IdentifierSource();
            identifierSource.setUuid(request.getUUID() == null ? UUID.randomUUID().toString() : request.getUUID());
        }
        return identifierSourceRepository.save(createIdSource(identifierSource, request));
    }

    public IdentifierSource updateIdSource(IdentifierSource identifierSource, IdentifierSourceContractWeb request) {
        return identifierSourceRepository.save(createIdSource(identifierSource, request));
    }

    private IdentifierSource createIdSource(IdentifierSource identifierSource, IdentifierSourceContractWeb request) {
        identifierSource.setBatchGenerationSize(request.getBatchGenerationSize());
        identifierSource.setCatchment(getCatchment(request.getCatchmentId(), request.getCatchmentUUID()));
        identifierSource.setMinimumBalance(request.getMinimumBalance());
        identifierSource.setName(request.getName());
        identifierSource.setOptions(request.getOptions() == null ? new JsonObject() : request.getOptions());
        identifierSource.setType(request.getType());
        identifierSource.setVoided(request.isVoided());
        identifierSource.setMinLength(request.getMinLength());
        identifierSource.setMaxLength(request.getMaxLength());
        return identifierSource;
    }

    private Catchment getCatchment(Long catchmentId, String catchmentUUID) {
        if (catchmentId == null && catchmentUUID == null) {
            return null;
        }
        return catchmentUUID != null ? catchmentRepository.findByUuid(catchmentUUID) : catchmentRepository.findOne(catchmentId);
    }
}
