package org.openchs.web.request.rules.response;

import java.util.List;

public class ValidationResponse {
    private Boolean success;
    private String messageKey;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
