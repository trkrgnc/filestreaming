package com.garanti.test.customerproject.business;

import com.garanti.test.customerproject.constants.FileType;
import com.garanti.test.customerproject.constants.JobType;
import com.garanti.test.customerproject.model.Phone;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.garanti.test.customerproject.constants.CustomerConstants.DELIMITER;

public class PhoneProcess extends Process implements ProcessService {

    public void process(List<String> files, int reportType, String outputDir) {
        this.reportType = reportType;
        this.outputFile = outputDir;

        Optional<String> fileNameStream = files.stream().filter(f ->
                new File(f).getName().equalsIgnoreCase(FileType.PHONE.toString()))
                .findFirst();


        if (fileNameStream.isPresent()) {
            String fileName = fileNameStream.get();


            switch (reportType) {
                case 20:
                    prepareTotalPhoneNumber(fileName);
                    break;
                case 21:
                    prepareTotalPhoneType(fileName);
                    break;
                case 22:
                    prepareTotalPhoneCustomer(fileName);
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

    private void prepareTotalPhoneNumber(String phoneFileName) {
        prepareTotalNumber(phoneFileName);
    }

    private Map<String, Long> ProcessTotalPhoneTypeData(String[] readBuffer, long lineNumber, Map<String, Long> typesMap) {

        return processTotalTypeData(readBuffer, lineNumber, typesMap);
    }

    private void prepareTotalPhoneType(String phoneFileName) {

        prepareSingleMappedData(phoneFileName);
    }

    protected void prepareTotalPhoneCustomer(String phoneFileName) {

        this.jobType = JobType.PHONE_GROUP;
        prepareSingleMappedData(phoneFileName);
    }

    Function<Phone, List<String>> compositeKey = phoneRecord ->
            Arrays.<String>asList(phoneRecord.getCustomerNumber(), phoneRecord.getPhoneType());


    @Override
    protected Map<List<String>, Long> processTotalPhoneCustomerData(String[] readBuffer, long lineNumber, Map<List<String>, Long> customerMap) {

        List<Phone> customers = Arrays.asList(readBuffer).parallelStream().filter(line -> line != null && !line.isEmpty())
                .map(line ->
                {
                    String customerNo = "";
                    String type = "";
                    try {
                        customerNo = line.split(DELIMITER)[0];
                        type = line.split(DELIMITER)[1];
                    } catch (Exception e) {
                        logger.error("Error on line " + lineNumber);
                    }

                    return new Phone(customerNo, type);
                }).collect(Collectors.toList());

        Map<List<String>, Long> tempMap = customers.stream().collect(Collectors.groupingBy(compositeKey, Collectors.counting()));

        return Stream.concat(customerMap.entrySet().stream(), tempMap.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.summingLong(Map.Entry::getValue)));
    }


}
