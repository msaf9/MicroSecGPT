package cpre.term.project.Controller;

import cpre.term.project.Model.ChatRequest;
import cpre.term.project.Model.ChatResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class ChatBotController {

    @Autowired
    RestTemplate restTemplate;

    @Value("${openai.api.url}")
    private String openAIUrl;

    @PostMapping("/chatWithOpenAiAPI")
    @ApiOperation(value = "Retrieve a response from OpenAI's GPT model based on the provided prompt.")
    public String getOpenAIResponse(@RequestBody String prompt) {
        ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo-0125", prompt);
//        ChatRequest chatRequest = new ChatRequest("gpt-4-turbo", prompt);
        ChatResponse response = restTemplate.postForObject(openAIUrl, chatRequest, ChatResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }

}
