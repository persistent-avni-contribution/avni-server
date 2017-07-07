package org.openchs.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "catchment")
public class Catchment extends CHSEntity {

    @Column
    @NotNull
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}