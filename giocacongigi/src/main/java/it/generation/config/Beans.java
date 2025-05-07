package it.generation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class Beans {
    public static final String PROP_AUTH = "mail.smtp.auth";
    public static final String PROP_START_TLLS = "mail.smtp.starttls.enable";
    public static final String PROP_DEBUG = "mail.debug";
    @Value("${spring.mail.host}")
    public static String SMTP_HOST = "smtp.gmail.com";
    public static Integer SMTP_PORT = 587;
    public static String ACCOUNT = "newgenm4f@gmail.com";
    public static String APP_KEY = "nksb jqda kdnn pfbb";
    public static String PROP_PROTOCOL = "mail.transport.protocol";

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(SMTP_HOST);
        mailSender.setPort(SMTP_PORT);

        mailSender.setUsername(ACCOUNT);
        mailSender.setPassword(APP_KEY);

        Properties props = mailSender.getJavaMailProperties();
        props.put(PROP_PROTOCOL, "smtp");
        props.put(PROP_AUTH, "true");
        props.put(PROP_START_TLLS, "true");
        props.put(PROP_DEBUG, "true");

        return mailSender;
    }
}
