import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FormParser {
    private static final Logger logger = LogManager.getLogger("xyz.cheesetron.email.form");

    /**
     * Given a URL, unsubscribes from that email's list.
     * @param hot actually unsubscribe or TODO
     * @param unsubscribeLink link of unsubscribe page
     */
    public static void unsubscribe(boolean hot, String unsubscribeLink) {
        logger.info("Unsubscribed from [" + unsubscribeLink + "]."); // TODO
    }
}
