package org.openchs.domain;

public class IndividualSearchV2 {

    String firstName;
    String lastName;
    String fullname;
    String lineage;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullname='" + fullname + '\'' +
                ", lineage='" + lineage + '\'' +
                '}';
    }
}
