package org.openchs.server.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "concept")
public class Concept extends CHSEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String name;

    @NotNull
    private String uuid;

    @NotNull
    private String dataType;

    private double lowAbsolute;
    private double highAbsolute;
    private double lowNormal;
    private double highNormal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public double getLowAbsolute() {
        return lowAbsolute;
    }

    public void setLowAbsolute(double lowAbsolute) {
        this.lowAbsolute = lowAbsolute;
    }

    public double getHighAbsolute() {
        return highAbsolute;
    }

    public void setHighAbsolute(double highAbsolute) {
        this.highAbsolute = highAbsolute;
    }

    public double getLowNormal() {
        return lowNormal;
    }

    public void setLowNormal(double lowNormal) {
        this.lowNormal = lowNormal;
    }

    public double getHighNormal() {
        return highNormal;
    }

    public void setHighNormal(double highNormal) {
        this.highNormal = highNormal;
    }

    public static Concept create(String name, String dataType) {
        Concept concept = new Concept();
        concept.name = name;
        concept.dataType = dataType;
        concept.uuid = UUID.randomUUID().toString();
        return concept;
    }
}