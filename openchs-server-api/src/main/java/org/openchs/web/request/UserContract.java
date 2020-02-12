package org.openchs.web.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openchs.domain.OperatingIndividualScope;
import org.openchs.domain.JsonObject;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserContract extends ReferenceDataContract {
    private String username;
    private long catchmentId;
    private List<UserFacilityMappingContract> facilities;
    private String phoneNumber;
    private String email;
    private boolean orgAdmin = false;
    private boolean admin = false;
    private String operatingIndividualScope = OperatingIndividualScope.None.toString();
    private JsonObject settings;
    private Long organisationId;
    private List<Long> accountIds;

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getCatchmentId() {
        return catchmentId;
    }

    public void setCatchmentId(long catchmentId) {
        this.catchmentId = catchmentId;
    }

    public List<UserFacilityMappingContract> getFacilities() {
        return facilities == null ? new ArrayList<>() : facilities;
    }

    public void setFacilities(List<UserFacilityMappingContract> facilities) { this.facilities = facilities; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public boolean isOrgAdmin() { return orgAdmin; }

    public void setOrgAdmin(boolean orgAdmin) { this.orgAdmin = orgAdmin; }

    public boolean isAdmin() { return admin; }

    public void setAdmin(boolean admin) { this.admin = admin; }

    public String getOperatingIndividualScope() { return operatingIndividualScope; }

    public void setOperatingIndividualScope(String operatingIndividualScope) {
        this.operatingIndividualScope = operatingIndividualScope;
    }

    public JsonObject getSettings() {
        return settings;
    }

    public void setSettings(JsonObject settings) {
        this.settings = settings;
    }

    public List<Long> getAccountIds() {
        return accountIds == null ? new ArrayList<>() : accountIds;
    }

    public void setAccountIds(List<Long> accountIds) {
        this.accountIds = accountIds;
    }
}