package cpre.term.project.Controller;

import cpre.term.project.Service.LogService;
import cpre.term.project.Service.ParseBuildGradle;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ParseBuildGradleController {

    private final ParseBuildGradle parseBuildGradle;

    @Autowired
    private LogService logService;

    @Autowired
    public ParseBuildGradleController(ParseBuildGradle parseBuildGradle) {
        this.parseBuildGradle = parseBuildGradle;
    }

    @GetMapping("/getBuildGradle")
    @ApiOperation(value = "Retrieve the 'build.gradle' file as a resource.")
    public ResponseEntity<Resource> getBuildGradle() throws IOException {
        logService.saveLogsToFile("Requested build.gradle file.");
        Resource buildGradleResource = parseBuildGradle.getBuildGradle();
        if (buildGradleResource != null) {
            return ResponseEntity.ok().body(buildGradleResource);
        } else {
            logService.saveLogsToFile("build.gradle file not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/getDependenciesVersion")
    @ApiOperation(value = "Retrieve dependencies and their versions from the 'build.gradle' file.")
    public ResponseEntity<?> getDependencies() {
        try {
            Resource buildGradleResource = parseBuildGradle.getBuildGradle();
            if (buildGradleResource != null) {
                return ResponseEntity.ok(parseBuildGradle.getDependencies(buildGradleResource));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("build.gradle file not found");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving dependencies");
        }
    }

}

