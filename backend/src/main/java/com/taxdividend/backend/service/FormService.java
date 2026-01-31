package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.GeneratedForm;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormService {
    List<GeneratedForm> listForms(UUID userId, Integer taxYear, String formType);

    Optional<GeneratedForm> getForm(UUID id, UUID userId);

    ResponseEntity<Resource> downloadForm(UUID id, UUID userId);

    Optional<String> getDownloadUrl(UUID id, UUID userId, int expirationHours);

    void deleteForm(UUID id, UUID userId);

    List<GeneratedForm> getFormsByStatus(UUID userId, String status);
}
