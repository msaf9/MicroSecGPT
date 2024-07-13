package cpre.term.project.Controller;

import cpre.term.project.Model.ChatRequest;
import cpre.term.project.Model.ChatResponse;
import cpre.term.project.Service.ParseBuildGradle;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@RestController
public class SecurityVulnerabilitiesController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ParseBuildGradle parseBuildGradle;

    @Value("${openai.api.url}")
    private String openAIUrl;

    @PostMapping("/getSecurityVulnerabilities")
    @ApiOperation(value = "Retrieve security vulnerabilities based on project dependencies.")
    public ResponseEntity<String> getSecurityVulnerabilities(@RequestBody String request) {
        ResponseEntity<?> responseEntity = getDependencies();
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Map<String, String> dependencies = (Map<String, String>) responseEntity.getBody();
            String prompt = formPrompt(request, dependencies);
            if (prompt != null) {
                ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo-0125", prompt);
//                ChatRequest chatRequest = new ChatRequest("gpt-4-turbo", prompt);
                ChatResponse response = restTemplate.postForObject(openAIUrl, chatRequest, ChatResponse.class);
                return ResponseEntity.ok(response.getChoices().get(0).getMessage().getContent());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error forming prompt");
            }
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody().toString());
        }
    }

    private ResponseEntity<?> getDependencies() {
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

    private String formPrompt(String request, Map<String, String> dependencies) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Provide security vulnerabilities in the following dependencies (also state what issues they might cause). Suggest some stable versions:\n");
        for (Map.Entry<String, String> entry : dependencies.entrySet()) {
            promptBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        promptBuilder.append("\n");
        promptBuilder.append(request);
        return promptBuilder.toString();
    }

}
