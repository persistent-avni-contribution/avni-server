package org.openchs.web;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openchs.common.AbstractControllerIntegrationTest;
import org.openchs.dao.ConceptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Sql({"/test-data.sql"})
public class ConceptDirtyCheckingIntegrationTest extends AbstractControllerIntegrationTest {
    @Autowired
    private ConceptRepository conceptRepository;

    private void post(Object json) {
        super.post("/concepts", json);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setUser("demo-admin");
    }

    @Test
    public void conceptUpdateShouldModifyLastModifiedDateTime() {
        try {
            Object json = mapper.readValue(this.getClass().getResource("/ref/concepts/concepts.json"), Object.class);
            post(json);

            String[] conceptsThatShouldntHaveAnyChange = new String[]{"b82d4ed8-6e9f-4c67-bfdc-b1a04861bc20", "c4e3facf-7594-434b-80d9-01b694758afc", "7d4c79a6-2ad7-452d-839f-6c7cbd6ec5c8", "31f1d3e9-d1e0-4645-947f-8b9fcaa17e01", "c4e3facf-7594-434b-80d9-01b694758afc"};
            String[] conceptsThatShouldHaveChanged = new String[]{"90e44d61-97c3-471d-964d-44b2488d0204", "1f7c6df7-5260-439a-8221-28319e8fb98b"};

            List<DateTime> dateTimesExpectedToNotChangeBefore = dateTimesForConceptUuids(conceptsThatShouldntHaveAnyChange);
            List<DateTime> dateTimesExpectedToChangeBefore = dateTimesForConceptUuids(conceptsThatShouldHaveChanged);

            json = mapper.readValue(this.getClass().getResource("/ref/concepts/conceptsWithModifications.json"), Object.class);
            post(json);

            List<DateTime> dateTimesExpectedToNotChangeAfter = dateTimesForConceptUuids(conceptsThatShouldntHaveAnyChange);
            List<DateTime> dateTimesExpectedToChangeAfter = dateTimesForConceptUuids(conceptsThatShouldHaveChanged);

            assertNoChangeInDates(conceptsThatShouldntHaveAnyChange, dateTimesExpectedToNotChangeBefore, dateTimesExpectedToNotChangeAfter);

            for (int i = 0; i < conceptsThatShouldHaveChanged.length; i++) {
                Assert.assertNotEquals("" + i, dateTimesExpectedToChangeBefore.get(i), dateTimesExpectedToChangeAfter.get(i));
            }
        } catch (IOException e) {
            Assert.fail();
        }
    }

    private void assertNoChangeInDates(String[] conceptUuids, List<DateTime> before, List<DateTime> after) {
        for (int i = 0; i < conceptUuids.length; i++) {
            Assert.assertEquals("" + i, before.get(i), after.get(i));
        }
    }

    @Test
    public void conceptUpdateShouldNotModifyLastModifiedDateTime2() throws IOException {
        Object json = mapper.readValue(this.getClass().getResource("/ref/concepts/conceptUsedAsCodedButAlsoAsAnswer.json"), Object.class);
        String[] conceptUuids = new String[]{"d78edcbb-2034-4220-ace2-20b445a1e0ad", "60f284a6-0240-4de8-a6a1-8839bc9cc219"};
        post(json);
        List<DateTime> before = dateTimesForConceptUuids(conceptUuids);
        post(json);
        List<DateTime> after = dateTimesForConceptUuids(conceptUuids);
        assertNoChangeInDates(conceptUuids, before, after);
    }

    private List<DateTime> dateTimesForConceptUuids(String[] conceptUuids) {
        return Arrays.stream(conceptUuids).map(conceptUuid -> conceptRepository.findByUuid(conceptUuid).getLastModifiedDateTime()).collect(Collectors.toList());
    }
}