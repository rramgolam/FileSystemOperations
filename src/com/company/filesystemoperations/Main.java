package com.company.filesystemoperations;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Main {

    public static void main(String[] args) {

        // Typical operations
        // Files.exists(filePath);
        // Files.copy(sourceFilePath, copyFilePath);    also overloaded (,,StandardCopyOption.REPLACE_EXISTING)
        // Files.move(sourceFilePath, moveFilePath);
        // Files.delete(filePath);  also .deleteIfExists(path) available
        // Files.createDirectory(dirPath)
        // Files.size(filePath) Files.getModifiedTime(filePath)

        // Get attributes
        Path attrPath = FileSystems.getDefault().getPath("FileTree\\file1.txt");
        try {
            BasicFileAttributes attrs = Files.readAttributes(attrPath, BasicFileAttributes.class);
            System.out.println(attrs.size());
            System.out.println(attrs.isRegularFile());
            // etc... lots of attrs available
        } catch (IOException e) {
            e.printStackTrace();
        }



// 		  Alternate way to use use directory filter rather than lambda expression
//        DirectoryStream.Filter<Path> filter =
//                new DirectoryStream.Filter<Path>() {
//                    public boolean accept(Path path) throws IOException {
//                        return (Files.isRegularFile(path));
//                    }
//                };

        // Listing files in a directory (only files)
        DirectoryStream.Filter<Path> filter = p -> Files.isRegularFile(p);

        Path directory = FileSystems.getDefault().getPath("FileTree" + File.separator + "Dir2");
//        Path directory = FileSystems.getDefault().getPath("FileTree/Dir2");  // FileTree\\Dir2 (windows)
        try (DirectoryStream<Path> contents = Files.newDirectoryStream(directory, filter)) {
            for (Path file : contents) {
                System.out.println(file.getFileName());
            }

        } catch (IOException | DirectoryIteratorException e) {
            System.out.println(e.getMessage());
        }


        // Two ways to specify directory seperators \ for unix // for win
        String separator = File.separator;
        System.out.println(separator);
        separator = FileSystems.getDefault().getSeparator();
        System.out.println(separator);

        // Creating a Temp file (you can also specify a temp folder, uses system temp by default)
        try {
            Path tempFile = Files.createTempFile("myapp", ".appext");  // C:\Users\???\AppData\Local\Temp\myapp1797805585146820741.appext
            System.out.println("Temporary file path = " + tempFile.toAbsolutePath());

        } catch(IOException e) {
            System.out.println(e.getMessage());
        }

        // Lists Drives
        Iterable<FileStore> stores = FileSystems.getDefault().getFileStores();
        for(FileStore store : stores) {
            System.out.println("Volume name/Drive letter = " + store);
            System.out.println("file store = " + store.name());
        }

        // List root drives (e.g. \ for mac and linux
        System.out.println("*******************");
        Iterable<Path> rootPaths = FileSystems.getDefault().getRootDirectories();
        for(Path path : rootPaths) {
            System.out.println(path);
        }

        // Walking a tree - printing out file and folder names
        System.out.println("---Walking Tree for Dir2---");
        Path dir2Path = FileSystems.getDefault().getPath("FileTree" + File.separator + "Dir2");
        try {
            Files.walkFileTree(dir2Path, new PrintNames());
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }


        // Copying a directory to a new directory
        System.out.println("---Copy Dir2 to Dir4/Dir2Copy---");
        Path copyPath = FileSystems.getDefault().getPath("FileTree" + File.separator + "Dir4" + File.separator + "Dir2Copy");
        //    FileTree/Dir4/Dir2Copy
        try {
            Files.walkFileTree(dir2Path, new CopyFiles(dir2Path, copyPath));

        } catch(IOException e) {
            System.out.println(e.getMessage());
        }


        // Converting between .io and .nio - file and path can be interchanged respectively
        File file = new File("/Examples/file.txt");   // C:\\Examples\file.txt
        Path convertedPath = file.toPath();
        System.out.println("convertedPath = " + convertedPath);

        // Other way of joining paths
        File parent = new File("/Examples");  // C:\\Examples
        File resolvedFile = new File(parent, "dir/file.txt");  // dir\\file.txt
        System.out.println(resolvedFile.toPath());

        // Other way of joining paths
        resolvedFile = new File("/Examples", "dir/file.txt");  // C:\\Examples   dir\\file.txt
        System.out.println(resolvedFile.toPath());

        // Other way of joining paths
        Path parentPath = Paths.get("/Examples");  // C:\\Examples
        Path childRelativePath = Paths.get("dir/file.txt");  // dir\\file.txt
        System.out.println(parentPath.resolve(childRelativePath));

        // Hack to get working path in nio
        File workingDirectory = new File("").getAbsoluteFile();
        System.out.println("Working directory = " + workingDirectory.getAbsolutePath());

        // .nio returns a list of strings
        System.out.println("--- print Dir2 contents using list() ---");
        File dir2File = new File(workingDirectory, "/FileTree/Dir2");   // \\FileTree\Dir2
        String[] dir2Contents = dir2File.list();
        for(int i=0; i< dir2Contents.length; i++) {
            System.out.println("i= " + i + ": " + dir2Contents[i]);
        }

        // .io returns a list of files
        System.out.println("--- print Dir2 contents using listFiles() ---");
        File[] dir2Files = dir2File.listFiles();
        for(int i=0; i< dir2Files.length; i++) {
            System.out.println("i= " + i + ": " + dir2Files[i].getName());
        }
    }
}
