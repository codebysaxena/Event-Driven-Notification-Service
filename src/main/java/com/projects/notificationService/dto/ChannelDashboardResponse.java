package com.projects.notificationService.dto;

import com.projects.notificationService.constants.NotificationChannel;

public class ChannelDashboardResponse {
    private NotificationChannel channel;

    private Long total;

    private Long sent;

    private Long processing;

    private Long pending;

    private Long failed;

    private Long dead;

    public ChannelDashboardResponse(){}

    public ChannelDashboardResponse(Long total, Long sent, Long processing, Long pending, Long failed, Long dead,
                                    NotificationChannel channel) {
        this.total = total;
        this.sent = sent;
        this.processing = processing;
        this.pending = pending;
        this.failed = failed;
        this.dead = dead;
        this.channel = channel;
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

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }
}
