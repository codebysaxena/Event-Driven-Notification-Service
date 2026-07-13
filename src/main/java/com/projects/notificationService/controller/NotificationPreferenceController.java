package com.projects.notificationService.controller;
 
import com.projects.notificationService.dto.MessageResponse;
import com.projects.notificationService.dto.PreferenceRequest;
import com.projects.notificationService.dto.PreferenceResponse;
import com.projects.notificationService.service.NotificationPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
 
@RestController
@RequestMapping("/api/preferences")
public class NotificationPreferenceController {
    private final NotificationPreferenceService notificationPreferenceService;
 
    public NotificationPreferenceController(NotificationPreferenceService notificationPreferenceService) {
        this.notificationPreferenceService = notificationPreferenceService;
    }
 
    @GetMapping
    public ResponseEntity<PreferenceResponse> getPreferences(Principal principal){
        PreferenceResponse res = notificationPreferenceService.getPreferencesByEmail(principal.getName());
        return ResponseEntity.ok(res);
    }
 
    @PutMapping
    public ResponseEntity<MessageResponse> updatePreference(Principal principal,
                                                            @RequestBody PreferenceRequest request){
        MessageResponse res = notificationPreferenceService.updatePreferencesByEmail(principal.getName(), request);
        return ResponseEntity.ok(res);
    }
}
