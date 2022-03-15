package org.umcn.tml.msea.clustering;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.umcn.tml.shared.util.TimeMethods;

@Configuration
@ComponentScan(basePackages = {
		"org.umcn.tml.msea", 
		"org.umcn.tml.generic", 
		"org.umcn.tml.shared"})

public class MSEAClusteringConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public String appRunDate() {
		return TimeMethods.getCurrentDate("yyyy-MM-dd_HH-mm-ss");
	}
	
}