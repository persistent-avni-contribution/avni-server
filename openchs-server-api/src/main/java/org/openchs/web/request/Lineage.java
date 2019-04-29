package org.openchs.web.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class Lineage {
    private Map<String, Long> lineage = new HashMap<>();

    public Lineage() {
    }

    public static Lineage parse(String lineage) {
        return new Lineage();
    }

    public Map<String, Long> getLineage() {
        return lineage;
    }
}
