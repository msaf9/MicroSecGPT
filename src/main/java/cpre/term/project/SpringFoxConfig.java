package cpre.term.project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
class SpringFoxConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cpre.term.project"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Verification And Potential Security Issues In Microservices")
                .description("Microservices represent a distributed architectural pattern utilized for designing and developing services or APIs, facilitating secure communication between clients and servers. Despite the availability of several distributed architectures, microservices stand out for their scalability and loose coupling, enhancing resource management and utilization efficiency. However, as applications expand, various challenges arise that hinder effective communication between different services, potentially exposing vulnerabilities that malicious actors could exploit.\n" +
                        "This research aims to identify and address security issues inherent in microservices architecture, proposing a model for verifying the architecture's robustness. The primary objective is to develop solutions that mitigate security risks by leveraging verification mechanisms to assess and enhance system integrity.")
                .version("1.0.0")
                .license("MIT License")
                .contact(new Contact("Sahil Afrid Farookhi Mohammad", "https://github.com/msaf9", "msaf@iastate.edu"))
                .build();
    }

}
