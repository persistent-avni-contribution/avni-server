package org.openchs.builder;

import org.openchs.dao.LocationRepository;
import org.openchs.domain.AddressLevel;
import org.openchs.domain.Organisation;
import org.openchs.framework.ApplicationContextProvider;
import org.openchs.web.request.LocationContract;

public class LocationBuilder extends BaseBuilder<AddressLevel, LocationBuilder> {

    private LocationRepository locationRepository;

    public LocationBuilder(AddressLevel existingEntity) {
        super(existingEntity, new AddressLevel());
        locationRepository = ApplicationContextProvider.getContext().getBean(LocationRepository.class);
    }

    public LocationBuilder copy(LocationContract locationRequest) throws LocationBuilderException {
        get().setTitle(locationRequest.getName());
        get().setType(locationRequest.getType());
        get().setLevel(locationRequest.getLevel());
        for (String parentUuid : locationRequest.getParents()) {
            AddressLevel parentLocation = locationRepository.findByUuid(parentUuid);
            if (parentLocation == null) {
                throw new LocationBuilderException(String.format("Location with uuid '%s' not found. Unable to set Parent location for '%s'",
                        parentUuid, locationRequest.getUuid()));
            }
            get().addParentAddressLevel(parentLocation);
        }
        return this;
    }

    public LocationBuilder withOrganisation(Organisation organisation){
        get().setOrganisationId(organisation.getId());
        return this;
    }
}
