package com.ftcs.main;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.ftcs.*"},
        entityManagerFactoryRef = "ftcsEntityManagerFactory",
        transactionManagerRef = "ftcsTransactionManager"
)
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean(name = "ftcsDataSource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName("net.sourceforge.jtds.jdbc.Driver")
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "ftcsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("ftcsDataSource") DataSource dataSource) {
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;

        hikariDataSource.setConnectionTestQuery("SELECT 1");

        return builder
                .dataSource(hikariDataSource)
                .packages("com.ftcs.*")
                .persistenceUnit("ftcs")
                .build();
    }

    @Bean(name = "ftcsTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("ftcsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

