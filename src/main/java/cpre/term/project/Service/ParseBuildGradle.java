package cpre.term.project.Service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParseBuildGradle {

    public Resource getBuildGradle() throws IOException {
        String currentDirectory = new File(".").getCanonicalPath();
        String buildGradlePath = currentDirectory + File.separator + "build.gradle";

        File buildGradleFile = new File(buildGradlePath);
        if (buildGradleFile.exists()) {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            return resolver.getResource("file:" + buildGradlePath);
        } else {
            return null;
        }
    }

    private static final Logger logger = Logger.getLogger(ParseBuildGradle.class.getName());

    public Map<String, String> getDependencies(Resource buildGradleResource) {
        Map<String, String> dependencies = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(buildGradleResource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("implementation") || line.contains("compile")) {
                    if (line.contains("group:") && line.contains("name:") && line.contains("version:")) {
                        Pattern pattern = Pattern.compile("group: '(.*?)', name: '(.*?)', version: '(.*?)'");
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            String dependency = matcher.group(2);
                            String version = matcher.group(3);
                            dependencies.put(dependency, version);
                        }
                    } else {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 2) {
                            String dependency = parts[1].replaceAll("[',]", "");
                            String version = "";
                            for (int i = 2; i < parts.length; i++) {
                                if (parts[i].contains("version")) {
                                    version = parts[i + 1].replaceAll("[',]", "");
                                    break;
                                }
                            }
                            if (!dependency.isEmpty() && !version.isEmpty()) {
                                dependencies.put(dependency, version);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading build.gradle file", e);
        }

        logger.info("Dependencies: " + dependencies);
        return dependencies;
    }

}
