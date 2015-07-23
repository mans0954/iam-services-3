package org.openiam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;

@Configuration
public class JMXConfig {

	@Bean(name="exporter")
	public MBeanExporter exporter() {
		final MBeanExporter exporter = new MBeanExporter();
		exporter.setAutodetect(true);
		exporter.setNamingStrategy(namingStrategy());
		exporter.setAssembler(assembler());
		return exporter;
	}
	
	@Bean(name="attributeSource")
	public AnnotationJmxAttributeSource attributeSource() {
		return new AnnotationJmxAttributeSource();
	}
	
	@Bean(name="assembler")
	public MetadataMBeanInfoAssembler assembler() {
		final MetadataMBeanInfoAssembler assembler = new MetadataMBeanInfoAssembler();
		assembler.setAttributeSource(attributeSource());
		return assembler;
	}
	
	@Bean(name="namingStrategy")
	public MetadataNamingStrategy namingStrategy() {
		final MetadataNamingStrategy strategy = new MetadataNamingStrategy();
		strategy.setAttributeSource(attributeSource());
		return strategy;
	}
}
