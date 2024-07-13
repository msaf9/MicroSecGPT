package cpre.term.project.Service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class CodeScannerService {

    public String getJavaFileContent(String fileName) {
        try {
            // Specify the root directory
            String rootDirectory = "src/main";

            // Recursively search for the file in all packages under root directory
            File file = findJavaFileInPackages(new File(rootDirectory), fileName);

            // Check if the file is found
            if (file != null) {
                byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                return new String(bytes);
            } else {
                return "Error: File not found.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Unable to read file.";
        }
    }

    // Recursive method to find the Java file within packages
    private File findJavaFileInPackages(File directory, String fileName) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursive call for subdirectories
                    File found = findJavaFileInPackages(file, fileName);
                    if (found != null) {
                        return found;
                    }
                } else if (file.getName().equals(fileName + ".java")) {
                    // Found the Java file
                    return file;
                }
            }
        }
        return null; // File not found
    }

}

