package com.hp.cloudprint.printjobs.config;

import com.hp.cloudprint.printjobs.dao.JobDAO;
import com.hp.cloudprint.printjobs.dao.JobDAOImpl;
import com.hp.cloudprint.printjobs.dao.JobLinkDAO;
import com.hp.cloudprint.printjobs.dao.JobLinkDAOImpl;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by prabhash on 6/30/2014.
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@PropertySource(value = "classpath:db.properties")
@ComponentScan(value = {"com.hp.cloudprint.printjobs.dao"})
public class PersistenceConfig {

    @Autowired
    Environment env;

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setDataSource(dataSource());
        txManager.setSessionFactory(sessionFactory().getObject());
        return txManager;
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        ds.setUrl(env.getProperty("jdbc.databaseurl"));
        ds.setUsername(env.getProperty("jdbc.username"));
        ds.setPassword(env.getProperty("jdbc.password"));
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
        sfb.setDataSource(dataSource());
        sfb.setPackagesToScan(new String[] { "com.hp.cloudprint.printjobs.model" });
        sfb.setHibernateProperties(hibernateProperties());
        return sfb;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public JobDAO jobDAO() {
        return new JobDAOImpl();
    }

    @Bean
    public JobLinkDAO jobLinkDAO() { return new JobLinkDAOImpl(); }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", env.getProperty("jdbc.dialect"));
        props.setProperty("hibernate.show_sql", env.getProperty("jdbc.show_sql"));
        return props;
    }
}
