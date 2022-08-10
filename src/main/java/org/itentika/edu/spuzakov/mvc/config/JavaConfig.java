package org.itentika.edu.spuzakov.mvc.config;

import liquibase.integration.spring.SpringLiquibase;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "org.itentika.edu.spuzakov.mvc.persistence.dao")
@EnableTransactionManagement
@ComponentScan(basePackages = "org.itentika.edu.spuzakov.mvc")
@PropertySource(value = {"classpath:application.properties"}, encoding = "UTF-8")
public class JavaConfig {

    @Bean(name = "embeddedDataSource")
    public DataSource getEmbeddedDataSource(@Value("${sto.datasource.url}") String dataSourceUrl,
                                            @Value("${sto.datasource.driverClassName}") String driverClassName,
                                            @Value("${sto.datasource.username}") String userName,
                                            @Value("${sto.datasource.password}") String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dataSourceUrl);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setAutoCommitOnReturn(false);
        dataSource.setDefaultAutoCommit(false);
        return dataSource;
    }

    @DependsOn("embeddedDataSource")
    @Bean
    public SpringLiquibase liquibase(@Qualifier("embeddedDataSource") DataSource dataSource) {

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:changelog/changelog-master.yaml");
        liquibase.setDataSource(dataSource);
        return liquibase;
    }

    @DependsOn("liquibase")
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("embeddedDataSource") DataSource dataSource,
                                                                       @Value("${sta.datasource.dialect}") String dialect) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("org.itentika.edu.spuzakov.mvc.persistence.domain");

        //set up Spring with JPA, using Hibernate as a persistence provider
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties(dialect));

        return em;
    }

    @DependsOn("entityManagerFactory")
    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return jpaTransactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties additionalProperties(String dialect) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        properties.setProperty("hibernate.dialect", dialect);

        return properties;
    }


}
