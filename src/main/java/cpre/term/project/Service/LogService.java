package cpre.term.project.Service;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class LogService {

    private static final Logger LOGGER = Logger.getLogger(LogService.class.getName());

    public void saveLogsToFile(String logMessage) {
        try {
            String logFilePath = "src/main/resources/application.log";
            File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(logFile, true);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            System.setErr(ps);
            LOGGER.log(Level.INFO, logMessage);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while saving logs to file", e);
        }
    }

    public Resource getLogsAsResource() {
        try {
            // Load log file from resources folder
            InputStream inputStream = new ClassPathResource("application.log").getInputStream();
            // Convert InputStream to String
            String logs = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            // Convert String back to InputStream
            InputStream resourceInputStream = IOUtils.toInputStream(logs, StandardCharsets.UTF_8);
            return new InputStreamResource(resourceInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

