package com.garanti.test.customerproject.business;

import com.garanti.test.customerproject.constants.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MailProcess extends Process implements ProcessService {

    public void process(List<String> files, int reportType, String outputDir) {
        this.reportType = reportType;
        this.outputFile = outputDir;

        Optional<String> fileNameStream = files.stream().filter(f ->
                new File(f).getName().equalsIgnoreCase(FileType.MAIL.toString()))
                .findFirst();


        if (fileNameStream.isPresent()) {
            String fileName = fileNameStream.get();


            switch (reportType) {
                case 30:
                    prepareTotalMailNumber(fileName);
                    break;
                case 31:
                    prepareTotalMailType(fileName);
                    break;
            }


        }


    }

    @Override
    protected List<String> processTotalCustomerPhoneMail(String[] readBuffer, String fileNamePhone, String fileNameMail) {
        return null;
    }

    @Override
    protected List<String> processTotalCustomerPhoneData(String[] readBuffer, long linenumber, String fileNameSecond) {
        return null;
    }

    @Override
    protected List<String> processTotalCustomerMailData(String[] readBuffer, long linenumber, String fileNameSecond) {
        return null;
    }

    @Override
    protected Map<List<String>, Long> processTotalPhoneCustomerData(String[] readBuffer, long linenumber, Map<List<String>, Long> combinedMap2) {
        return null;
    }

    private void prepareTotalMailNumber(String mailFileName) {

        prepareTotalNumber(mailFileName);
    }

    private Map<String, Long> ProcessTotalMailTypeData(String[] readBuffer, long lineNumber, Map<String, Long> typesMap) {

        return processTotalTypeData(readBuffer, lineNumber, typesMap);
    }

    private void prepareTotalMailType(String mailFileName) {

        prepareSingleMappedData(mailFileName);
    }

}
