package com.codimen.lendit.configuration;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket postsApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);

        docket.groupName("public-api")
                .apiInfo(apiInfo())
                .select()
                .paths(postPaths())
                .build();

        docket.globalResponseMessage(RequestMethod.GET, ImmutableList.of(new ResponseMessageBuilder()
                .code(400)
                .message("Bad Request")
                .responseModel(new ModelRef("Error")).build(), new ResponseMessageBuilder()
                .code(500)
                .message("Internal Server Error")
                .responseModel(new ModelRef("Error")).build()));

        return docket;
    }

    private Predicate<String> postPaths() {
        return or(regex("/api/.*"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Demo API")
                .description("API Reference")
                .license("License v1")
                .licenseUrl("https://licenceurl.com").version("1.0").build();
    }

}