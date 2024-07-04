package org.example.springbatchdemo.config;


import com.thoughtworks.xstream.XStream;
import org.example.springbatchdemo.model.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class BatchConfiguration {

    @Bean
    public StaxEventItemReader<Employee> reader() {
        StaxEventItemReader<Employee> xmlFileReader = new StaxEventItemReader<>();
        xmlFileReader.setResource(new ClassPathResource("employees.xml"));
        xmlFileReader.setFragmentRootElementName("employee");
        Map<String, Class<?>> aliases = new HashMap<>();
        aliases.put("employee", Employee.class);

        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAliases(aliases);
        XStream xStream = xStreamMarshaller.getXStream();
        xStream.allowTypes(new Class[]{Employee.class});

        xmlFileReader.setUnmarshaller(xStreamMarshaller);

        return xmlFileReader;

    }



    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        JobBuilder jobBuilder = new JobBuilder("Coding", jobRepository);
        return jobBuilder.start(step).build();

    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, DataSource dataSource) {
        StepBuilder stepBuilder = new StepBuilder("StepBuilder", jobRepository);
        return stepBuilder.<Employee, Employee>chunk(2, platformTransactionManager).reader(reader())
                .writer(writer(dataSource)).build();
    }

    @Bean
    public JdbcBatchItemWriter<Employee> writer(DataSource dataSource) {
        JdbcBatchItemWriterBuilder<Employee> writer = new JdbcBatchItemWriterBuilder<>();
        return writer.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO EMPLOYEE VALUES(:empId,:empName,:designation,:department,:gender,:mobileNo)")
                .dataSource(dataSource)
                .build();

    }


}

