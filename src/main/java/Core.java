import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Core {
    private static final Logger logger = LogManager.getLogger("xyz.cheesetron.email.core");

    public static ArrayList<String> unsubscribeLinkList = new ArrayList<>();

    public static void main(String[] args) {

        String startFolder;
        if (args.length > 0) startFolder = args[0];
        else {
            logger.warn("No folder provided, assuming working directory.");
            startFolder = System.getProperty("user.dir");
        }

        EmailFileVisitor visitor = new EmailFileVisitor();
        try {
            Files.walkFileTree(Path.of(startFolder), visitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info(unsubscribeLinkList.size() + " links found.");
        logger.debug(unsubscribeLinkList);

        for (String link : unsubscribeLinkList) {
            FormParser.unsubscribe(false, link);
        }
    }
}