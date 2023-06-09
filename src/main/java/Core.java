import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Core {
    private static final Logger logger = LogManager.getLogger("xyz.cheesetron.email.core");

    public static ArrayList<String> unsubscribeLinkList = new ArrayList<>();

    private static int successCount;

    public static void main(String[] args) {

        String startFolder = null;
        if (args.length == 0) {
            System.out.println("Usage:\n" +
                    "    emailunsubscribe [folder containing .eml files to unsubscribe from]");
            System.exit(1);
        } else startFolder = args[0];


        EmailFileVisitor visitor = new EmailFileVisitor();
        try {
            Files.walkFileTree(Path.of(startFolder), visitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info(unsubscribeLinkList.size() + " links found.");
        logger.debug(unsubscribeLinkList);

        // This is an executor, a high level object for managing Runnables/Callables which kind of work like threads
        Executor executor = Executors.newCachedThreadPool();

        // This uses the executor and basically makes two queues of tasks and results.
        ExecutorCompletionService<Boolean> cs = new ExecutorCompletionService<>(executor);

        for (String link : unsubscribeLinkList) {
            cs.submit(new Unsubscriber(link)); // adds a new Unsubscriber task to the queue, to be executed sometime
//            Unsubscriber.unsubscribe(true, link);
        }

        for (int i = unsubscribeLinkList.size(); i > 0; i--) {
            try {
                if (cs.take().get()) successCount++;
                // take() gets the next Future, waiting if there are none
                // and get() gets the result contained within, waiting if it's not ready
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e);
            }
        }
        logger.info("Unsubscribed successfully from " + successCount + " emails.");
        // it doesn't exit unless you explicitly tell it to as there are other threads that need to be told we're done
        System.exit(0);
    }
}