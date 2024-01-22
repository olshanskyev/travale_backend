package net.travale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${gui-urls.passwordResetLink}")
    private String resetPasswordLink;

    void sendMail(String to, String subject, String text){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        javaMailSender.send(msg);
    }

    public void sendNewUserPasswordResetLink(String to, String token){
        final String link = resetPasswordLink + "?token=" + token;
        sendMail(to, "Пользователь создан", "Пользователь " + to + " добавлен на портал Балтийская Слобода 2. Для завершения регистрации перейдите по ссылке " + link + " и задайте пароль. ");
    }

    public void sendPasswordResetLink(String to, String token){
        final String link = resetPasswordLink + "?token=" + token;
        sendMail(to, "Новый пароль", "От вашего имени был отправлен запрос на сброс пароля на портале Балтийская Слобода 2. Для того, чтобы это сделать, перейдите по ссылке  " + link + " и задайте новый пароль. ");
    }

}
