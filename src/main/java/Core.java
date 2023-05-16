import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Core {
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));

        String startFolder = args[0];

        ArrayList<String> unsubscribeLinkList = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of(startFolder))) {
            for (Path file : stream) {
                System.out.println(file.getFileName());
                unsubscribeLinkList.add(EmailParser.processEmail(Files.newInputStream(file)));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String link : unsubscribeLinkList) {
            FormParser.unsubscribe(false, link);
        }
    }
}