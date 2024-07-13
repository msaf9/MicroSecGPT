package cpre.term.project.Controller;

import cpre.term.project.Model.ChatRequest;
import cpre.term.project.Model.ChatResponse;
import cpre.term.project.Service.CodeScannerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/code")
public class CodeAnalysisController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private CodeScannerService codeScannerService;

    @Value("${openai.api.url}")
    private String openAIUrl;

    @PostMapping("/staticCodeAnalysis")
    @ApiOperation(value = "Perform static code analysis on the provided code.")
    public ResponseEntity<String> performStaticCodeAnalysis(@RequestBody String fileName) {
        return performCodeAnalysis(fileName, "Perform static code analysis on the following Java code:\n");
    }

    @PostMapping("/codeReview")
    @ApiOperation(value = "Perform code review on the provided code for security vulnerabilities, potential bugs, and adherence to best practices.")
    public ResponseEntity<String> performCodeReview(@RequestBody String fileName) {
        return performCodeAnalysis(fileName, "Find security vulnerabilities, potential bugs, and adherence to best practices on the following Java code:\n");
    }

    @PostMapping("/codeDocumentation")
    @ApiOperation(value = "Generate documentation for the provided code.")
    public ResponseEntity<String> generateCodeDocumentation(@RequestBody String fileName) {
        return performCodeAnalysis(fileName, "Generate architecture and design Documentation for the following Java code:\n");
    }

    private ResponseEntity<String> performCodeAnalysis(String fileName, String analysisType) {
        String code = codeScannerService.getJavaFileContent(fileName);
        if (code != null && !code.isEmpty()) {
            String prompt = formPrompt(code, analysisType);
            if (prompt != null) {
                ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo-0125", prompt);
//                ChatRequest chatRequest = new ChatRequest("gpt-4-turbo", prompt);
                ChatResponse response = restTemplate.postForObject(openAIUrl, chatRequest, ChatResponse.class);
                return ResponseEntity.ok(response.getChoices().get(0).getMessage().getContent());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error forming prompt");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found or empty");
        }
    }

    private String formPrompt(String code, String analysisType) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(analysisType);
        promptBuilder.append("```java\n");
        promptBuilder.append(code);
        promptBuilder.append("\n```");
        return promptBuilder.toString();
    }

}

