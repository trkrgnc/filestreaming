package com.garanti.test.customerproject.business;

import com.garanti.test.customerproject.error.ParameterException;
import com.garanti.test.customerproject.util.DataValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ReportProcessor {

    private static ReportProcessor reportProcessor;

    Logger logger = LoggerFactory.getLogger(getClass());

    public  static ReportProcessor getInstance() {
        if( reportProcessor == null ) {
            reportProcessor = new ReportProcessor();
        }

        return reportProcessor;
    }

    public void process(int reportType, List<String> inputs, String outputDir) throws ParameterException {

        ProcessService process = Process.getInstance(inputs,reportType);

        process.process(inputs, reportType, outputDir);

    }

    public void start(List<String> inputParams) throws ParameterException {
        String outputFile = DataValidation.checkOutputDirectory(inputParams);
        logger.info("Output directory: {}", outputFile);


        int repType = DataValidation.checkReportType(inputParams);
        logger.info("Report Type: {}", repType);

        List<String> inputFiles = DataValidation.checkInputFiles(inputParams);
        inputFiles.forEach(file -> {
            logger.info("Input File: {} ", file);
        });

        process(repType, inputFiles, outputFile);
    }

    public void run(String[] args) {
        int argSize = args.length;
        if (argSize >= 3 && argSize <= 5) {
            try {

                List<String> inputParams = Arrays.asList(args);
                start(inputParams);
            } catch (ParameterException pe) {
                logger.error(pe.getReason());
            } catch (Exception e) {
                logger.error("Corrupted input file, exit..");
            }
        } else {
            logger.error("Wrong number of inputs are entered!");
        }
    }
}
