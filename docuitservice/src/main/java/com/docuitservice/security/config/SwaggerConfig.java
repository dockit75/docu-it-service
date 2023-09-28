package com.docuitservice.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//import springfox.documentation.service.SecurityReference;
//import com.google.common.collect.Lists;
//import springfox.documentation.spi.service.contexts.SecurityContext;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.ApiKey;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//import springfox.documentation.service.AuthorizationScope;

@Configuration
//@EnableSwagger2
//@EnableWebMvc
public class SwaggerConfig extends WebMvcConfigurationSupport {

	/*
	 * ApiInfo apiInfo() { return new ApiInfoBuilder().title("DocuItService")
	 * .license("Apache 2.0").licenseUrl(
	 * "http://www.apache.org/licenses/LICENSE-2.0.html")
	 * .termsOfServiceUrl("http://swagger.io/terms/").version("1.0.0") .build(); }
	 * 
	 * @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
	 * 
	 * System.out.
	 * println("******************************Configuring swagger resource handler******************************"
	 * ); registry.addResourceHandler("swagger-ui.html")
	 * .addResourceLocations("classpath:/META-INF/resources/");
	 * registry.addResourceHandler("/webjars/**")
	 * .addResourceLocations("classpath:/META-INF/resources/webjars/"); }
	 * 
	 * @Bean public Docket api() { return new Docket(DocumentationType.SWAGGER_2)
	 * .select() .apis(RequestHandlerSelectors.any()) .paths(PathSelectors.any())
	 * .build() .enable(true) .apiInfo(apiInfo())
	 * .securityContexts(Lists.newArrayList(securityContext()))
	 * .securitySchemes(Lists.newArrayList(apiKey())); }
	 * 
	 * private SecurityContext securityContext() { return SecurityContext.builder()
	 * .securityReferences(defaultAuth())
	 * .forPaths(PathSelectors.regex("/anyPath.*")) .build(); }
	 * 
	 * List<SecurityReference> defaultAuth() { AuthorizationScope authorizationScope
	 * = new AuthorizationScope("global", "accessEverything"); AuthorizationScope[]
	 * authorizationScopes = new AuthorizationScope[1]; authorizationScopes[0] =
	 * authorizationScope; return Lists.newArrayList(new
	 * SecurityReference("AUTHORIZATION", authorizationScopes)); } private ApiKey
	 * apiKey() { return new ApiKey("apiKey", "Authorization", "header"); //`apiKey`
	 * is the name of the APIKey, `Authorization` is the key in the request header }
	 */

}
