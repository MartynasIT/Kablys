package com.example.kablys;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailSender  {

    public void sendMail(String email, String password, String username) {
        //googles smtp
        Properties props = new Properties();
        props.put("mail.smtp.host", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        String body = "Sveiki, sveikinu prisiregistravus prie 'Kablys' programėlės." +
               "\n" + "Jūsų slapyvardis yra: " + username +"\n" +"Slaptažodis: " + password
                + "\n" + "\n"  +"Pagarbiai, Kablys komanda.";

        //uzkuriame sesija su mano emailu
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("kablysapp@gmail.com", "kablyshelp");
            }
        });
        try {
            MimeMessage msg = new MimeMessage(session);
            InternetAddress[] address = InternetAddress.parse(email, true);
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("Jūs užsiregistravote!");
            msg.setSentDate(new Date());
            msg.setText(body);
            Transport.send(msg);
        } catch (MessagingException mex) {

        }
    }
}