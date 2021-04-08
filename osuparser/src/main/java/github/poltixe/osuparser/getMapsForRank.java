package github.poltixe.osuparser;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.*;
import java.util.*;
import java.util.regex.*;

class getMapsForRank {
    private static String toHexString(byte[] bytes) {
        char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v / 16];
            hexChars[j * 2 + 1] = hexArray[v % 16];
        }

        return new String(hexChars);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        File myFile = new File("rankeddatabase.txt");

        if (myFile.createNewFile()) {
            System.out.println("File created: " + myFile.getName());
        } else {
            myFile.delete();
            myFile.createNewFile();
            System.out.println("File created: " + myFile.getName());
        }

        final Path rootDir = Paths.get("/media/beyley/OsuSings/OsuSings");

        // Walk thru mainDir directory
        Files.walkFileTree(rootDir, new FileVisitor<Path>() {
            // First (minor) speed up. Compile regular expression pattern only one time.
            private Pattern pattern = Pattern.compile("^(.*?)");

            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes atts) throws IOException {

                boolean matches = pattern.matcher(path.toString()).matches();

                return (matches) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes mainAtts) throws IOException {
                boolean matches = pattern.matcher(path.toString()).matches();

                if (path.getFileName().toString().matches(".*\\.osu")) {
                    String filename = path.getFileName().toString();
                    String nameWithoutExtension = filename.substring(0, filename.length() - 4);

                    // BeatMap parsedMap = BeatMap.parseOsuFile(path.toAbsolutePath().toString());

                    String md5 = "";
                    String sha256 = "";

                    try {
                        MessageDigest digest = MessageDigest.getInstance("MD5");

                        // Get file input stream for reading the file content
                        FileInputStream fis = new FileInputStream(path.toAbsolutePath().toString());

                        // Create byte array to read data in chunks
                        byte[] byteArray = new byte[1024];
                        int bytesCount = 0;

                        // Read file data and update in message digest
                        while ((bytesCount = fis.read(byteArray)) != -1) {
                            digest.update(byteArray, 0, bytesCount);
                        }

                        // close the stream; We don't need it now.
                        fis.close();

                        // Get the hash's bytes
                        byte[] bytes = digest.digest();

                        // This bytes[] has bytes in decimal format;
                        // Convert it to hexadecimal format
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < bytes.length; i++) {
                            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                        }

                        md5 = sb.toString();
                    } catch (NoSuchAlgorithmException e) {
                    }

                    try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");

                        // Get file input stream for reading the file content
                        FileInputStream fis = new FileInputStream(path.toAbsolutePath().toString());

                        // Create byte array to read data in chunks
                        byte[] byteArray = new byte[1024];
                        int bytesCount = 0;

                        // Read file data and update in message digest
                        while ((bytesCount = fis.read(byteArray)) != -1) {
                            digest.update(byteArray, 0, bytesCount);
                        }

                        // close the stream; We don't need it now.
                        fis.close();

                        // Get the hash's bytes
                        byte[] bytes = digest.digest();

                        // This bytes[] has bytes in decimal format;
                        // Convert it to hexadecimal format
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < bytes.length; i++) {
                            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                        }

                        sha256 = sb.toString();
                    } catch (NoSuchAlgorithmException e) {
                        System.out.println("pain");
                    }

                    FileWriter fileWriter = new FileWriter("rankeddatabase.txt", true); // Set true for append mode
                    PrintWriter printWriter = new PrintWriter(fileWriter);
                    printWriter.print(md5 + ":" + sha256 + "\\");
                    printWriter.close();
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path path, IOException exc) throws IOException {
                // TODO Auto-generated method stub
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path path, IOException exc) throws IOException {
                exc.printStackTrace();

                // If the root directory has failed it makes no sense to continue
                return path.equals(rootDir) ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
            }
        });
    }
}