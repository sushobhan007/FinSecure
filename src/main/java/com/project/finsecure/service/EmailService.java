package com.project.finsecure.service;

import com.project.finsecure.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
