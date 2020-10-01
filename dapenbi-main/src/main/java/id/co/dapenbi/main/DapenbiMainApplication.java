package id.co.dapenbi.main;

import java.io.IOException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import id.co.dapenbi.core.property.FileStorageProperties;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"id.co.dapenbi.*"})
@ServletComponentScan
@EntityScan(basePackages = {"id.co.dapenbi.*"})
@EnableJpaRepositories(basePackages = {"id.co.dapenbi.*"}, repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@EnableConfigurationProperties({
    FileStorageProperties.class
})
@EnableAutoConfiguration(exclude = { //
        DataSourceAutoConfiguration.class, //
        DataSourceTransactionManagerAutoConfiguration.class, //
        HibernateJpaAutoConfiguration.class })
public class DapenbiMainApplication extends SpringBootServletInitializer {
	
	@Autowired
    private Environment env;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DapenbiMainApplication.class);
	}
	 
	public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException {
		//loadLibrary();
		SpringApplication.run(DapenbiMainApplication.class, args);
	}
	
	@Autowired
	@Bean(name = "dataSource")
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
 
        // See: application.properties
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
 
        System.out.println("## getDataSource: " + dataSource);
 
        return dataSource;
    }
	
	@Bean
	@ConditionalOnBean(name = "dataSource")
	@ConditionalOnMissingBean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
	    LocalContainerEntityManagerFactoryBean em
	      = new LocalContainerEntityManagerFactoryBean();
	    em.setDataSource(getDataSource());
	    em.setPackagesToScan("id.co.dapenbi");
	    em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
	    
	    if (additionalProperties() != null) {
            em.setJpaProperties(additionalProperties());
        }
	    return em;
	}
		
	@Bean
	@ConditionalOnMissingBean(type = "JpaTransactionManager")
	JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
	    JpaTransactionManager transactionManager = new JpaTransactionManager();
	    transactionManager.setEntityManagerFactory(entityManagerFactory);
	    return transactionManager;
	}
	
	@ConditionalOnResource(resources = "classpath:application.properties")
    final Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();

        hibernateProperties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        hibernateProperties.put("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
        hibernateProperties.put("current_session_context_class", //
                env.getProperty("spring.jpa.properties.hibernate.current_session_context_class"));
        hibernateProperties.put("hibernate.id.new_generator_mappings", //
                env.getProperty("spring.jpa.properties.hibernate.id.new_generator_mappings"));

        return hibernateProperties;
    }
}