package org.openchs.web;

import org.openchs.builder.BuilderException;
import org.openchs.builder.LocationBuilder;
import org.openchs.dao.AddressLevelTypeRepository;
import org.openchs.dao.LocationRepository;
import org.openchs.dao.OrganisationRepository;
import org.openchs.domain.AddressLevel;
import org.openchs.domain.AddressLevelType;
import org.openchs.domain.Organisation;
import org.openchs.framework.security.UserContextHolder;
import org.openchs.web.request.Lineage;
import org.openchs.web.request.LocationContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//@BasePathAwareController
@RepositoryRestController
public class LocationController implements RestControllerResourceProcessor<AddressLevel> {

    private final AddressLevelTypeRepository addressLevelTypeRepository;
    private OrganisationRepository organisationRepository;
    private LocationRepository locationRepository;
    private Logger logger;

    @Autowired
    public LocationController(OrganisationRepository organisationRepository,
                              LocationRepository locationRepository,
                              AddressLevelTypeRepository addressLevelTypeRepository) {
        this.organisationRepository = organisationRepository;
        this.locationRepository = locationRepository;
        this.addressLevelTypeRepository = addressLevelTypeRepository;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @RequestMapping(value = "/locations", method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyAuthority('admin','organisation_admin')")
    @Transactional
    public ResponseEntity<?> save(@RequestBody List<LocationContract> locationContracts) {
        try {
            Map<String, AddressLevelType> typeMap = new HashMap<>();
            for (LocationContract locationContract : locationContracts) {
                logger.info(String.format("Processing location request: %s", locationContract.toString()));
                AddressLevelType type = typeMap.compute(locationContract.getType(), (k, v) -> v == null ? saveType(k) : v);
                saveLocation(locationContract, type);
            }
        } catch (BuilderException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping(value = "locations")
    @ResponseBody
    public PagedResources<Resource<AddressLevel>> getAll(Pageable pageable) {
        return wrap(locationRepository.findAll(pageable));
    }

    @GetMapping(value = "locations/search/findByParentLocation")
    @ResponseBody
    public PagedResources<Resource<AddressLevel>> findByParent(
            @RequestParam(value = "q", required = false) String title,
            @RequestParam(value = "parentLocationId", required = false) Long parentLocationId,
            Pageable pageable) {
        if (title == null && parentLocationId != null)
            return wrap(locationRepository.findByParentLocationMappingsParentLocationIdIs(parentLocationId, pageable));
        if (title != null && parentLocationId != null) {
            return wrap(locationRepository.findByTitleIgnoreCaseContainingAndParentLocationMappingsParentLocationIdIs(title, parentLocationId, pageable));
        }
        Double topLevel = locationRepository.getTopLevel();
        if (title != null && parentLocationId == null) {
            return wrap(locationRepository.findByTitleIgnoreCaseContainingAndLevel(title, topLevel, pageable));
        }
        if (title == null && parentLocationId == null) {
            return wrap(locationRepository.findByLevel(topLevel, pageable));
        }
        return null;
    }

    @GetMapping(value = "locations/search/find")
    @ResponseBody
    public PagedResources<Resource<AddressLevel>> find(
            @RequestParam(value = "lineage", required = false) String _lineage,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "q", required = false) String title,
            @RequestParam(value = "parentLocationId", required = false) Long parentLocationId,
            Pageable pageable) {
        Lineage lineage = Lineage.parse(_lineage);
        Double defaultedLevel = locationRepository.getTopLevel();
        if(id !=null) {
            return wrap(locationRepository.findById(id, pageable));
        }
        if (title != null && parentLocationId == null) {
            return wrap(locationRepository.findByTitleIgnoreCaseContainingAndLevel(title, defaultedLevel, pageable));
        }
        if (title != null && parentLocationId != null) {
            return wrap(locationRepository.findByTitleIgnoreCaseContainingAndParentLocationMappingsParentLocationIdIs(title, parentLocationId, pageable));
        }
        if (title == null && parentLocationId != null) {
            return wrap(locationRepository.findByParentLocationMappingsParentLocationIdIs(parentLocationId, pageable));
        }
        if (title == null && parentLocationId == null) {
            return wrap(locationRepository.findByLevel(defaultedLevel, pageable));
        }
        return null;
    }


    private AddressLevelType saveType(String type) {
        if (type == null) return null;
        AddressLevelType existingType = addressLevelTypeRepository.findByNameAndOrganisationId(type,
                UserContextHolder.getUserContext().getOrganisation().getId());
        if (existingType == null) {
            AddressLevelType addressLevelType = new AddressLevelType();
            addressLevelType.setName(type);
            addressLevelType.setUuid(UUID.randomUUID().toString());
            return addressLevelTypeRepository.save(addressLevelType);
        }
        return existingType;
    }

    private void saveLocation(LocationContract contract, AddressLevelType type) throws BuilderException {
        LocationBuilder locationBuilder = new LocationBuilder(locationRepository.findByUuid(contract.getUuid()), type);
        locationBuilder.copy(contract);
        AddressLevel location = locationBuilder.build();
        updateOrganisationIfNeeded(location, contract);
        try {
            locationRepository.save(location);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuilderException(String.format("Unable to create Location{name='%s',level='%s',orgUUID='%s',..}: '%s'", contract.getName(), contract.getLevel(), contract.getOrganisationUUID(), e.getMessage()));
        }
    }

    private boolean possibleDuplicate(LocationContract locationRequest) {
        List<AddressLevel> locations = locationRepository.findByTitleAndLevelAndUuidNot(locationRequest.getName(), locationRequest.getLevel(), locationRequest.getUuid());
        return !locations.isEmpty();
    }

    private void updateOrganisationIfNeeded(AddressLevel location, @NotNull LocationContract contract) {
        String organisationUuid = contract.getOrganisationUUID();
        if (organisationUuid != null) {
            Organisation organisation = organisationRepository.findByUuid(organisationUuid);
            if (organisation == null) {
                throw new RuntimeException(String.format("Organisation not found with uuid :'%s'", organisationUuid));
            }
            location.setOrganisationId(organisation.getId());
        }
    }
}
