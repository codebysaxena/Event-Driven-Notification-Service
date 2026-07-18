package com.projects.notificationService.dto;

public class DashboardResponse {
    private Long total;

    private Long sent;

    private Long processing;

    private Long pending;

    private Long failed;

    private Long dead;

    private Long emailSent;
    private Long emailFailed;

    private Long smsSent;
    private Long smsFailed;

    private Long pushSent;
    private Long pushFailed;

    public DashboardResponse(){}

    public DashboardResponse(Long total, Long sent, Long processing, Long pending, Long failed,
                             Long dead, Long emailSent, Long emailFailed, Long smsSent,
                             Long smsFailed, Long pushSent, Long pushFailed) {
        this.total = total;
        this.sent = sent;
        this.processing = processing;
        this.pending = pending;
        this.failed = failed;
        this.dead = dead;
        this.emailSent = emailSent;
        this.emailFailed = emailFailed;
        this.smsSent = smsSent;
        this.smsFailed = smsFailed;
        this.pushSent = pushSent;
        this.pushFailed = pushFailed;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSent() {
        return sent;
    }

    public void setSent(Long sent) {
        this.sent = sent;
    }

    public Long getProcessing() {
        return processing;
    }

    public void setProcessing(Long processing) {
        this.processing = processing;
    }

    public Long getPending() {
        return pending;
    }

    public void setPending(Long pending) {
        this.pending = pending;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }

    public Long getDead() {
        return dead;
    }

    public void setDead(Long dead) {
        this.dead = dead;
    }

    public Long getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(Long emailSent) {
        this.emailSent = emailSent;
    }

    public Long getEmailFailed() {
        return emailFailed;
    }

    public void setEmailFailed(Long emailFailed) {
        this.emailFailed = emailFailed;
    }

    public Long getSmsSent() {
        return smsSent;
    }

    public void setSmsSent(Long smsSent) {
        this.smsSent = smsSent;
    }

    public Long getSmsFailed() {
        return smsFailed;
    }

    public void setSmsFailed(Long smsFailed) {
        this.smsFailed = smsFailed;
    }

    public Long getPushSent() {
        return pushSent;
    }

    public void setPushSent(Long pushSent) {
        this.pushSent = pushSent;
    }

    public Long getPushFailed() {
        return pushFailed;
    }

    public void setPushFailed(Long pushFailed) {
        this.pushFailed = pushFailed;
    }
}
