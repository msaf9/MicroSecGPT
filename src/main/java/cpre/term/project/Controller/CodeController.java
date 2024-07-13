package cpre.term.project.Controller;

import cpre.term.project.Service.CodeScannerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/code")
public class CodeController {

    private final CodeScannerService codeScanner;

    public CodeController(CodeScannerService codeScanner) {
        this.codeScanner = codeScanner;
    }

    @GetMapping("/{fileName}")
    @ApiOperation(value = "Get the file content")
    public String getJavaFileContent(@PathVariable String fileName) {
        return codeScanner.getJavaFileContent(fileName);
    }

}

