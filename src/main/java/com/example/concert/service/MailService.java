package com.example.concert.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendVerificationMail(String to, String token) {
        String subject = "[콘서트 서비스] 이메일 인증을 완료해주세요.";
        String verificationLink = "https://your-domain.com/auth/verify?token=" + token;

        String text = """
                안녕하세요.

                콘서트 서비스 이용을 위해 이메일 인증이 필요합니다.
                아래 링크를 클릭하여 인증을 완료해주세요.

                %s

                만약 본인이 요청한 것이 아니라면 이 메일을 무시하셔도 됩니다.
                """.formatted(verificationLink);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
