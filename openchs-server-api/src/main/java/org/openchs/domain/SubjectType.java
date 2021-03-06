package org.openchs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.openchs.application.projections.BaseProjection;
import org.openchs.web.request.webapp.GroupRoleContract;
import org.springframework.data.rest.core.config.Projection;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "subject_type")
@JsonIgnoreProperties({"operationalSubjectTypes"})
@DynamicInsert
public class SubjectType extends OrganisationAwareEntity {
    @NotNull
    @Column
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subjectType")
    private Set<OperationalSubjectType> operationalSubjectTypes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groupSubjectType")
    private Set<GroupRole> groupRoles = new HashSet<>();

    @Column
    private boolean isGroup;

    @Column
    private boolean isHousehold;

    private Boolean active;

    public Set<GroupRole> getGroupRoles() {
        return groupRoles;
    }

    public void setGroupRoles(Set<GroupRole> groupRoles) {
        this.groupRoles = groupRoles;
    }

    public boolean isHousehold() {
        return isHousehold;
    }

    public void setHousehold(boolean household) {
        isHousehold = household;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<OperationalSubjectType> getOperationalSubjectTypes() {
        return operationalSubjectTypes;
    }

    public void setOperationalSubjectTypes(Set<OperationalSubjectType> operationalSubjectTypes) {
        this.operationalSubjectTypes = operationalSubjectTypes;
    }

    @JsonIgnore
    public String getOperationalSubjectTypeName() {
        return operationalSubjectTypes.stream()
                .map(OperationalSubjectType::getName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @JsonIgnore
    public List<GroupRoleContract> getGroupRolesContract() {
        return groupRoles.stream().map(GroupRoleContract::fromEntity).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<String> getMemberSubjectUUIDs() {
        return isGroup() ? groupRoles.stream()
                .filter(gr -> !gr.getMemberSubjectType().isVoided())
                .map(gr -> gr.getMemberSubjectType().getUuid()).collect(Collectors.toList()) : Collections.emptyList();
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = Optional.ofNullable(active).orElse(true);
    }

    @Projection(name = "SubjectTypeProjection", types = {SubjectType.class})
    public interface SubjectTypeProjection extends BaseProjection {
        String getName();

        String getOperationalSubjectTypeName();

        boolean isGroup();

        String getMemberSubjectUUIDs();
    }
}
