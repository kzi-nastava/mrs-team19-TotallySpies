package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.MailBody;

@Service
public class EmailService {
    //class responsible to send a mail
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendSimpleMessage(MailBody mailBody){
        //draft mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom("andjela.broceta.bn@gmail.com");
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());

        javaMailSender.send(message);
    }
    //for mobile app
    public void sendHtmlMessage(MailBody mailBody) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(mailBody.to());
            helper.setFrom("andjela.broceta.bn@gmail.com");
            helper.setSubject(mailBody.subject());
            String activationLink = mailBody.text();
            String html =
                    "<div style='font-family:Arial,sans-serif;font-size:14px;line-height:1.6'>" +
                            "<p>Click the link to activate your account (valid 24h):</p>" +

                            "<p>" +
                            "<a href='" + activationLink + "'>" +
                            activationLink +
                            "</a>" +
                            "</p>" +
                            "</div>";

            helper.setText(html, true);
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

}
