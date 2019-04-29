package org.openchs.framework.rest;

import org.openchs.domain.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(Organisation.class);
        config.exposeIdsFor(Catchment.class);
        config.exposeIdsFor(Facility.class);
        config.exposeIdsFor(AddressLevel.class);
    }
}
