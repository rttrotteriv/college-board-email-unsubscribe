import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class FormParser {
    private static final Logger logger = LogManager.getLogger("xyz.cheesetron.email.form");


    /**
     * Given a URL, unsubscribes from that email's list.
     *
     * @param hot             actually unsubscribe or TODO
     * @param unsubscribeLink link of unsubscribe page
     */
    public static void unsubscribe(boolean hot, String unsubscribeLink) {

        if (hot) {
            if (unsubscribeLink.contains("technolutions")) {
                technolutions(unsubscribeLink);
            } else if (unsubscribeLink.contains("sendgrid.net")) {
                sendgrid(unsubscribeLink);
            } else if (unsubscribeLink.contains("learn.")){
                learnBees(unsubscribeLink);
            } else if (unsubscribeLink.contains("capturehighered.net")) {
                simpleButtonUnsub(unsubscribeLink);
            } else if (unsubscribeLink.contains("tuftsuc") || unsubscribeLink.contains("https://go.")) {
                justLoad(unsubscribeLink);
            } else {
                // load page
                // heck it
                try {
                    Runtime.getRuntime().exec("curl " + unsubscribeLink);
                } catch (IOException e) {
                    logger.error(e);
                }
                logger.warn("Assumed that loading \"" + unsubscribeLink + "\" would unsubscribe, may require manual intervention.");
            }
        } else logger.info("Unsubscribed from [" + unsubscribeLink + "].");
    }

    public static void technolutions(String unsubscribeLink){
        /*
        // submit post to unsubscribeLink with group=
        HttpPost request = new HttpPost(unsubscribeLink);

        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("param-1", "12345"));
        params.add(new BasicNameValuePair("param-2", "Hello!"));
        request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        try {
            HttpResponse response = httpclient.execute(request);
        } catch (IOException e) {

        }
        HttpEntity entity = response.getEntity();

        if (entity != null) {

        }
        */

        try {
            Runtime.getRuntime().exec("curl -X POST \"" + unsubscribeLink + "\" -d \"group=\"");
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private static void sendgrid(String unsubscribeLink) {
        try {
            Runtime.getRuntime().exec("curl -X POST \"" + unsubscribeLink + "\" -d \"preferences[unsubscribe]=true\"");
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private static void learnBees(String unsubscribeLink) {
        // nope.
        logger.error("Bees.");
    }

    private static void simpleButtonUnsub(String unsubscribeLink) {
        try {
            Runtime.getRuntime().exec("curl -X POST \"" + unsubscribeLink + "\" -d \"group=\"");
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private static void justLoad(String unsubscribeLink) {
        try {
            Runtime.getRuntime().exec("curl " + unsubscribeLink);
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
