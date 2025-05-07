package it.generation.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {



   private static final String CONFIRMATION_URL = "http://localhost:8080/giocacongigi/api/auth/confirm?token=%s";


    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String toEmail, String name, String token) {
        String subject = "Conferma la tua registrazione";
        String confirmationLink = String.format(CONFIRMATION_URL, token);
        String content = "Ciao " + name + ",\n\n"
                + "Grazie per esserti registrato. Usa il link qui sotto per confermare la tua registrazione:\n"
                + confirmationLink + "\n\n"
                + "Se non hai richiesto questa registrazione, ignorala.\n";
             System.out.println(content);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false); // Non invio HTML, solo testo semplice

            System.out.println(helper);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content); // Testo semplice
            mailSender.send(message);

            System.out.println("e-mail inviata con successo " + message);

        } catch (Exception e) {
            System.out.println("Errore durante l'invio dell'email: " + e.getMessage());
            throw new RuntimeException("Errore durante l'invio dell'email: " + e.getMessage(), e);
        }
    }

}

