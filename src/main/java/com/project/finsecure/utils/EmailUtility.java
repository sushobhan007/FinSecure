package com.project.finsecure.utils;

import com.project.finsecure.dto.EmailDetails;
import com.project.finsecure.entity.User;
import com.project.finsecure.service.EmailService;

import java.math.BigDecimal;

public class EmailUtility {
    public static void sendAccountCreditEmail(User user, BigDecimal amount, EmailService emailService) {
        String accountName = UserUtility.getUserFullName(user);
        String emailBody = String.format("""
                Hello %s,
                                
                INR %s has been credited to A/C No. %s on %s. 
                                
                For any concerns regarding this transaction, please call customer care.
                                
                Always open to help you.
                                
                Regards,
                FinSecure
                """, accountName, amount, user.getAccountNumber(), user.getCreatedOn());

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("Credit Notification from FinSecure!")
                .messageBody(emailBody)
                .build();

        emailService.sendEmailAlert(emailDetails);
        System.out.println("Email sent successfully to: " + user.getEmail());
    }

    public static void sendAccountDebitEmail(User user, BigDecimal amount, EmailService emailService) {
        String accountName = UserUtility.getUserFullName(user);
        String emailBody = String.format("""
                Hello %s,
                                
                We wish to inform you that INR %s has been debited from your A/C No. %s on %s 
                                
                Please call customer care if this transaction is not initiated by you.
                                
                Thank you for using our service.
                                
                Regards,
                FinSecure
                """, accountName, amount, user.getAccountNumber(), user.getCreatedOn());

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("Debit Notification Alert!")
                .messageBody(emailBody)
                .build();

        emailService.sendEmailAlert(emailDetails);
        System.out.println("Email sent successfully to: " + user.getEmail());
    }

    public static void sendAccountCreationEmail(User user, EmailService emailService) {
        String accountName = UserUtility.getUserFullName(user);
        String emailBody = String.format("""
                Hello user,
                                
                Your account has been created successfully at our bank.
                Here are your details. Please save this email and keep the information safe.
                                
                Name: %s
                Account Number: %s
                Account Activation Date: %s
                                
                Thank you for using our service.
                Regards,
                FinSecure
                """, accountName, user.getAccountNumber(), user.getCreatedOn());

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("Account Created Successfully!")
                .messageBody(emailBody)
                .build();

        emailService.sendEmailAlert(emailDetails);
        System.out.println("Email sent successfully to: " + user.getEmail());
    }
}
