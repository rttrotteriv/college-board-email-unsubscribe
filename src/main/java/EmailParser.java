import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class EmailParser {
    private static final Logger logger = LogManager.getLogger("xyz.cheesetron.email.parse");

    /**
     * Finds the unsubscribe link in a file.
     *
     * @param inputStream stream for email file
     * @return the email's unsubscribe link
     */
    public static String processEmail(InputStream inputStream) throws LinkNotFoundException {
        MimeMessage message;
        try {
            message = new MimeMessage(Session.getInstance(System.getProperties()), inputStream);
        } catch (MessagingException e) {
            logger.error(e);
            return "";
        }

        return findUnsubscribeLink(toHTML(message));
    }

    /**
     * Converts an email message to an HTML String.
     *
     * @param message Email message to process
     * @return HTML in form of a string
     */
    private static String toHTML(MimeMessage message) {
        try {
            Object content = message.getContent();
            if (content instanceof String) {
                logger.debug("content is string");
                return (String) content;
            } else if (content instanceof MimeMultipart) {
                logger.debug("Multipart message in " + ((MimeMultipart) content).getCount() + " parts");
                // the first body part is plain text formatted message, second is html document
                if (((MimeMultipart) content).getCount() > 1 && ((MimeMultipart) content).getBodyPart(1).getContent() instanceof String)
                    return (String) ((MimeMultipart) content).getBodyPart(1).getContent();
                else {
                    logger.error("One part message occurred where multipart message expected, or unexpected content type.");
                    return "";
                }
            } else {
                logger.error("Unexpect content type of message.");
                return "";
            }
        } catch (IOException | MessagingException e) {
            logger.error(e);
            return "";
        }
    }

    /**
     * Ugly helper method - phrasing of unsubscribe link's text found in the wild through brute force
     *
     * @param content HTML message content
     * @return unsubscribe link
     */
    @SuppressWarnings("DataFlowIssue")
    private static String findUnsubscribeLink(String content) throws LinkNotFoundException {
        Document doc = Jsoup.parse(content);
        // CSS selector: "a" -> link elements ":matches()" -> selector getting any element containing the text within
        if (doc.root().select("a:matches((?i)unsubscribe)").isEmpty()) {
            if (doc.root().select("a:matches((?i)let .* know)").isEmpty()) {
                if (doc.root().select("a:matches((?i)click here)").isEmpty()) {
                    throw new LinkNotFoundException();
                } else return doc.root().select("a:matches((?i)click here)").last().attr("href");
            } else return doc.root().select("a:matches((?i)let .* know)").last().attr("href");
        } else return doc.root().select("a:matches((?i)unsubscribe)").last().attr("href");
    }
}
