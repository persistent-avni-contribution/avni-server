package org.openchs.domain;

import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;

@Entity
@BatchSize(size = 100)
public class IndividualSearchV2 {

    String fullname;
    String lineage;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getLineage() {
        return lineage;
    }

    public void setLineage(String lineage) {
        this.lineage = lineage;
    }

    @Override
    public String toString() {
        return "IndividualSearchV2{" +
                ", fullname='" + fullname + '\'' +
                ", lineage='" + lineage + '\'' +
                '}';
    }
}
