package org.openchs.web;

import org.junit.Before;
import org.junit.Test;
import org.openchs.common.AbstractControllerIntegrationTest;
import org.openchs.dao.LocationRepository;
import org.openchs.domain.AddressLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@Sql({"/test-data-openchs-organisation.sql"})
public class SampleControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private LocationRepository locationRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void getHello() throws Exception {
//        List<AddressLevel> kotma = locationRepository.findByTitleIgnoreCaseContainingAndLevelAndParentLocationMappingsIdIs("Kotma", 1.0, 1.0);
        ResponseEntity<String> response = template.getForEntity(base.toString() + "/hello",
                String.class);
        assertThat(response.getBody(), equalTo("world"));
    }

    @Test
    public void getPing() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString() + "/ping",
                String.class);
        assertThat(response.getBody(), equalTo("pong"));
    }
}