package main.java;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * main.java.MainController class to send mails
 * using SMTP using GMail
 */
public class MailController {
    // SMTP connection details
    static final String FROM = "vms.wwi17sca@gmail.com";
    static final String FROMNAME = "VMS - Mobile Apps";
    static final String SMTP_USERNAME = "vms.wwi17sca@gmail.com";
    static final String SMTP_PASSWORD = "PASSWORD";
    static final String HOST = "smtp.gmail.com";
    static final int PORT = 587;

    // Properties object for connection config
    public static Properties props;

    public static void setProps() {
        // Create a Properties object to contain connection configuration information.
        props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
    }

    /**
     * Send a mail via GMail
     *
     * @param recipient to send the mail to
     * @param subject   the specify the subject of the mail
     * @param body      the set the content of the mail
     */
    public static void send(String recipient, String subject, String body) {
        // set config
        setProps();

        // Session session = Session.getDefaultInstance(props, null);
        Session session = Session.getDefaultInstance(props);

        Message msg = new MimeMessage(session);
        try {
            // prepare "header"
            msg.setFrom(new InternetAddress(FROM, FROMNAME));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            msg.setSubject(subject);

            // prepare content
            Multipart multipart = new MimeMultipart();
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(body, "text/html");

            // add content to mail
            multipart.addBodyPart(textBodyPart);
            msg.setContent(multipart);

            // create new transport object from session
            Transport transport = session.getTransport();
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error message: " + e.getMessage());
        }
    }

    /**
     * Send a mail via GMail
     * attach file to send
     *
     * @param recipient to send the mail to
     * @param subject   the specify the subject of the mail
     * @param body      the set the content of the mail
     * @param file_path path to file that should be attached
     * @param file_name name of file in sent mail
     */
    public static void send(String recipient, String subject, String body, String file_path, String file_name) {
        // set config
        setProps();

        // Session session = Session.getDefaultInstance(props, null);
        Session session = Session.getDefaultInstance(props);

        Message msg = new MimeMessage(session);
        try {
            // prepare "header"
            msg.setFrom(new InternetAddress(FROM, FROMNAME));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            msg.setSubject(subject);

            // prepare content
            Multipart multipart = new MimeMultipart();
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(body, "text/html");

            // prepare attachment content
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(file_path); // ex : "C:\\test.pdf"
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(file_name); // ex : "test.pdf"

            // add content to mail
            multipart.addBodyPart(textBodyPart);  // add text
            multipart.addBodyPart(attachmentBodyPart); // add attachement
            msg.setContent(multipart);

            // create new transport object from session
            Transport transport = session.getTransport();
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error message: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        // mail send
        MailController.send("name@domain.com", "Test Mail from MailController.java", "<h1>Test</h1><p>some text content</p>");

        // with attachment
        MailController.send("name@domain.com", "Test Mail from MailController.java", "<h1>Test</h1><p>some text content with content attached</p>", "/Users/user/Documents/text_file.txt", "attachment.txt");
    }

}
