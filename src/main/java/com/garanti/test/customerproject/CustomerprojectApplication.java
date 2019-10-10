package com.garanti.test.customerproject;

import com.garanti.test.customerproject.business.ReportProcessor;
import com.garanti.test.customerproject.error.ParameterException;
import com.garanti.test.customerproject.util.DataValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class CustomerprojectApplication {

    static Logger logger = LoggerFactory.getLogger(CustomerprojectApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(CustomerprojectApplication.class, args);

        ReportProcessor.getInstance().run(args);
    }
}
