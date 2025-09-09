package com.example.concert.service.notification;

import com.example.concert.web.dto.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotification implements NotificationSender {

    private final JavaMailSender mailSender;

    @Override
    public void sendNotification(PaymentEvent event, String status) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(event.getUserEmail());
        mail.setSubject(event.getUserName() + "님 결재에 성공했습니다!");
        mail.setText(event.getUserEmail() + " 메일에 결재 성공 완료했습니다." );
        mailSender.send(mail);
    }

}
