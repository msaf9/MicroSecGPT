package cpre.term.project.Controller;

import cpre.term.project.Model.ChatRequest;
import cpre.term.project.Model.ChatResponse;
import cpre.term.project.Service.LogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.api.url}")
    private String openAIUrl;

    @GetMapping("/view")
    @ApiOperation(value = "View Logs of the application")
    public ResponseEntity<Resource> viewLogs() {
        Resource logFileResource = logService.getLogsAsResource();
        if (logFileResource != null) {
            return ResponseEntity.ok().body(logFileResource);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/analyze")
    @ApiOperation(value = "Analyze application logs")
    private String sendPromptToChatGPT() {
        ResponseEntity<String> promptResponse = formPrompt();
        if (promptResponse.getStatusCode() == HttpStatus.OK) {
            String prompt = "Analyze these logs from the spring boot application: " + promptResponse.getBody();
            ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo-0125", prompt);
//            ChatRequest chatRequest = new ChatRequest("gpt-4-turbo", prompt);
            ChatResponse response = restTemplate.postForObject(openAIUrl, chatRequest, ChatResponse.class);
            return response.getChoices().get(0).getMessage().getContent();
        } else {
            return "Failed to form prompt for ChatGPT API";
        }
    }

    public ResponseEntity<String> formPrompt() {
        try {
            // Get logs from the application.log file
            Resource logResource = logService.getLogsAsResource();
            if (logResource != null) {
                // Convert logs to String
                String logs = new String(logResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                // Form prompt message
                String prompt = "Could you perform analysis on the following logs from the Spring Boot application along with the application.log information?\n\n" + logs;
                return ResponseEntity.ok(prompt);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve logs from application.log");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while forming prompt");
        }
    }

}

