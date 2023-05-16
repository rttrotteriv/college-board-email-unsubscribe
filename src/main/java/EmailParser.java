import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;

public class EmailParser {

    /**
     * @param inputStream stream for email file
     * @return the email's unsubscribe link
     */
    public static String processEmail(InputStream inputStream) {
        MimeMessage message = null;
        try {
            message = new MimeMessage(Session.getInstance(System.getProperties()), inputStream);
        } catch (MessagingException e) {
            System.out.println("Something's gone wrong.");
            System.exit(1);
        }

        // TODO
        try {
            return (String) message.getContent();
        } catch (IOException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
