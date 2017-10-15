/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.document_store.Swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * @author waffaa
 */
@Configuration
@EnableSwagger2
public class Swagger {
   @Bean

    public Docket Api() {

        return new Docket(DocumentationType.SWAGGER_2)
        		
                .select().apis(RequestHandlerSelectors.basePackage("com.document_store.controller")).build();

    }  
}
