package test.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@MapperScan(basePackages = "mapper1", sqlSessionFactoryRef = "sqlSessionFactory1")
public class MyBatisConfig1 {

  @Bean(name = { "dataSourceProperties1" })
  @ConfigurationProperties("spring.datasource.1")
  public DataSourceProperties dataSourceProperties1() {
    return new DataSourceProperties();
  }

  @Primary
  @Bean(name = { "dataSource1" })
  public DataSource dataSource1(@Qualifier("dataSourceProperties1") DataSourceProperties properties) {
    return properties.initializeDataSourceBuilder().build();
  }

  @Bean(name = { "txManager1" })
  public PlatformTransactionManager txManager1(@Qualifier("dataSource1") DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public DataSourceInitializer dataSourceInitializer1(@Qualifier("dataSource1") DataSource dataSource) {
    ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
    resourceDatabasePopulator.addScript(new ClassPathResource("schema1.sql"));
    resourceDatabasePopulator.addScript(new ClassPathResource("data1.sql"));
    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
    dataSourceInitializer.setDataSource(dataSource);
    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
    return dataSourceInitializer;
  }

  @Primary
  @Bean(name = { "sqlSessionFactory1" })
  public SqlSessionFactory sqlSessionFactory1(@Qualifier("dataSource1") DataSource dataSource,
      MybatisProperties mybatisProperties) throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSource);
    sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
    org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
    if (mybatisProperties.getConfiguration() != null) {
      BeanUtils.copyProperties(mybatisProperties.getConfiguration(), config);
    }
    sqlSessionFactoryBean.setConfiguration(config);
    return sqlSessionFactoryBean.getObject();
  }
}
