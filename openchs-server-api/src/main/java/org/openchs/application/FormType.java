package org.openchs.application;

import java.util.Arrays;

public enum FormType {
    BeneficiaryIdentification,
    IndividualProfile,
    Encounter,
    ProgramEncounter,
    ProgramEnrolment,
    ProgramExit,
    ProgramEncounterCancellation,
    ChecklistItem,
    IndividualRelationship;

    static FormType[] linkedToEncounterType = {Encounter, ProgramEncounter, ProgramEncounterCancellation};
    static FormType[] linkedToProgram = {ProgramEncounter, ProgramExit, ProgramEnrolment, ProgramEncounterCancellation};

    public boolean isLinkedToEncounterType() {
        return Arrays.asList(linkedToEncounterType).contains(this);
    }

    public boolean isLinkedToProgram() {
        return Arrays.asList(linkedToProgram).contains(this);
    }
}
