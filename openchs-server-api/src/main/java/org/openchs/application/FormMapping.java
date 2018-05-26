package org.openchs.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openchs.domain.CHSEntity;
import org.openchs.domain.OrganisationAwareEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "form_mapping")
public class FormMapping extends OrganisationAwareEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "form_id")
    private Form form;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "observations_type_entity_id")
    private Long observationsTypeEntityId;

    @Column(nullable = false)
    private boolean isVoided = false;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    @JsonIgnore
    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    @JsonIgnore
    public Long getObservationsTypeEntityId() {
        return observationsTypeEntityId;
    }

    public void setObservationsTypeEntityId(Long observationsTypeEntityId) {
        this.observationsTypeEntityId = observationsTypeEntityId;
    }

    public boolean isVoided() {
        return isVoided;
    }

    public void setVoided(Boolean voided) {
        isVoided = voided;
    }

}