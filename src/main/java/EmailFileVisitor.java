import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class EmailFileVisitor implements FileVisitor<Path> {

    private static final Logger logger = LogManager.getLogger("xyz.cheesetron.email.files");

    /**
     * Invoked for a directory before entries in the directory are visited.
     *
     * <p> If this method returns {@link FileVisitResult#CONTINUE CONTINUE},
     * then entries in the directory are visited. If this method returns {@link
     * FileVisitResult#SKIP_SUBTREE SKIP_SUBTREE} or {@link
     * FileVisitResult#SKIP_SIBLINGS SKIP_SIBLINGS} then entries in the
     * directory (and any descendants) will not be visited.
     *
     * @param dir   a reference to the directory
     * @param attrs the directory's basic attributes
     * @return the visit result
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs){
        return FileVisitResult.CONTINUE;
    }

    /**
     * Invoked for a file in a directory.
     *
     * @param file  a reference to the file
     * @param attrs the file's basic attributes
     * @return the visit result
     * @throws IOException if an I/O error occurs
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        logger.debug(file);
        logger.debug(file.getName(file.getNameCount() - 1));
        String filename = file.getName(file.getNameCount() - 1).toString();
        if (filename.endsWith(".emlx") || filename.endsWith(".eml")) {
            try {
                String link = EmailParser.processEmail(Files.newInputStream(file));
                Core.unsubscribeLinkList.add(link);

                // people I really don't want to unsubscribe from
                if (link.contains("collegeboard") || link.contains("cyberstart") || link.contains("bncollege")) {
                    logger.fatal("Critical sender's email: " + filename);
                    throw new RuntimeException("Unsubscribe from critical sender attempted.");
                }

            } catch (LinkNotFoundException e) {
                logger.warn("Couldn't find unsubscribe link in \"" + filename + "\". Message is likely malformed or requires manual removal.");
            }
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * Invoked for a file that could not be visited. This method is invoked
     * if the file's attributes could not be read, the file is a directory
     * that could not be opened, and other reasons.
     *
     * @param file a reference to the file
     * @param exc  the I/O exception that prevented the file from being visited
     * @return the visit result
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc){
        logger.error(exc);
        return FileVisitResult.CONTINUE;
    }

    /**
     * Invoked for a directory after entries in the directory, and all of their
     * descendants, have been visited. This method is also invoked when iteration
     * of the directory completes prematurely (by a {@link #visitFile visitFile}
     * method returning {@link FileVisitResult#SKIP_SIBLINGS SKIP_SIBLINGS},
     * or an I/O error when iterating over the directory).
     *
     * @param dir a reference to the directory
     * @param exc {@code null} if the iteration of the directory completes without
     *            an error; otherwise the I/O exception that caused the iteration
     *            of the directory to complete prematurely
     * @return the visit result
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return FileVisitResult.CONTINUE;
    }
}
