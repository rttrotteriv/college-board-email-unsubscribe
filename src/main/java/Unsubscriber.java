import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import static org.jsoup.Connection.Method.POST;

// A callable is a task, like a method, that returns a value, but you can do more things with it. It has one method
// call() that does something, and gives a Future that will contain the result.
public class Unsubscriber implements Callable<Boolean> {
    private static final Logger logger = LogManager.getLogger("xyz.cheesetron.email.unsubscriber");

    private final String unsubscribeLink;

    public Unsubscriber(String unsubscribeLink) {
        this.unsubscribeLink = unsubscribeLink;
    }

    @Override
    public Boolean call() {
        return unsubscribe(true, this.unsubscribeLink);
    }

    /**
     * Given a URL, unsubscribes from that email's list.
     *
     * @param hot             actually unsubscribe or just log the link
     * @param unsubscribeLink link of unsubscribe page
     * @return success or failure
     */
    public static boolean unsubscribe(boolean hot, String unsubscribeLink) {
        if (hot) {
            if (unsubscribeLink.contains("technolutions")) {
                return technolutions(unsubscribeLink);

            } else if (unsubscribeLink.contains("sendgrid.net")) {
                return sendgrid(unsubscribeLink);

            } else if (unsubscribeLink.contains("learn.")){
                return learnBees(unsubscribeLink);

            } else if (unsubscribeLink.contains("capturehighered.net")) {
                return captureHigherEd(unsubscribeLink);

            } else if (unsubscribeLink.contains("https://em.") || unsubscribeLink.contains("https://my.")) {
                return adobeMarketo(unsubscribeLink);

            } else if (unsubscribeLink.contains("tuftsuc") || unsubscribeLink.contains("://go.")) {
                return justLoad(unsubscribeLink);

            } else {
                justLoad(unsubscribeLink);
                logger.warn("Assumed that loading [" + unsubscribeLink + "] would unsubscribe, may require manual intervention.");
                return false;
            }

        } else {
            logger.info("Unsubscribed from [" + unsubscribeLink + "].");
            return true;
        }
    }

    static boolean technolutions(String unsubscribeLink) {
        try {
            String actualLink = Jsoup.connect(unsubscribeLink).execute().url().toString();
            return Jsoup.connect(actualLink).method(POST).data("group", "").execute().statusCode() / 100 == 2;
        } catch (IOException e) {
            logger.error(e);
            return false;
        }
    }

    static boolean sendgrid(String unsubscribeLink) {
        try {
            return Jsoup.connect(unsubscribeLink).method(POST).data("preferences[unsubscribe]", "true").execute().statusCode() / 100 == 2;

        } catch (IOException e) {
            logger.error(e);
            return false;
        }
    }

    static boolean learnBees(String unsubscribeLink) {
        try {
            //noinspection DataFlowIssue
            String token = Jsoup.connect(unsubscribeLink).get().getElementById("lead_contact_frequency_rules__token").attr("value");
            logger.debug(token);
            return Jsoup.connect(unsubscribeLink).method(POST).data("lead_contact_frequency_rules[lead_channels][subscribed_channels][0]", "").data("lead_contact_frequency_rules[lead_channels][subscribed_channels][1]", "").data("lead_contact_frequency_rules[_token]", token).execute().statusCode() / 100 == 2;

        } catch (IOException e) {
            logger.error(e);
            return false;
        }
    }

    static boolean captureHigherEd(String unsubscribeLink) {
        try {
            Pattern pattern = Pattern.compile("cid=(.*)&mid=(.*)");
            Matcher matcher = pattern.matcher(unsubscribeLink);
            logger.debug(matcher.find());
            return Jsoup.connect(unsubscribeLink).method(POST).data("campaign_id", matcher.group(0)).data("version_id", "").data("media_id", matcher.group(1)).execute().statusCode() / 100 == 2;

        } catch (IOException e) {
            logger.error(e);
            return false;
        }
    }

    static boolean adobeMarketo(String unsubscribeLink) {
        logger.error("Adobe Marketo forms are not supported. Link:[" + unsubscribeLink + "]");
        return false;

//        try {
//            return Jsoup.connect(unsubscribeLink).execute().statusCode() / 100 == 2;
//        } catch (IOException e) {
//            logger.error(e);
//            return false;
//        }
    }

    static boolean justLoad(String unsubscribeLink) {
        try {
            return Jsoup.connect(unsubscribeLink).execute().statusCode() / 100 == 2;

        } catch (IOException e) {
            logger.error(e);
            return false;
        }
    }
}
