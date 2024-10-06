package com.phs.application.config.datasource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class ConfigDataSource {

    //public static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    public static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String URL = "jdbc:mysql://localhost:3306/shoes?allowPublicKeyRetrieval=true&useUnicode=yes&characterEncoding=UTF-8&useSSL=false&createDatabaseIfNotExist=true&serverTimezone=UTC";

    public static final String USERNAME = "root";

    public static final String PASSWORD = "123456";

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(MYSQL_DRIVER_CLASS_NAME);
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        return dataSource;
    }

    @Bean
    public NamedParameterJdbcTemplate template() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

}
