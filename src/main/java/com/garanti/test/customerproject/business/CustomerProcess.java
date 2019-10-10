package com.garanti.test.customerproject.business;

import com.garanti.test.customerproject.constants.FileType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.garanti.test.customerproject.error.FormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.garanti.test.customerproject.constants.CustomerConstants.DELIMITER;
import static com.garanti.test.customerproject.constants.JobType.*;
import static com.garanti.test.customerproject.util.CustomerUtils.getFile;

public class CustomerProcess extends Process implements ProcessService  {

    Logger logger = LoggerFactory.getLogger(getClass());


    public void process(List<String> files, int reportType, String outputDir) {
        this.reportType = reportType;
        this.outputFile = outputDir;

        Optional<String> fileNameStream = files.stream().filter(f ->
                new File(f).getName().equalsIgnoreCase(FileType.CUSTOMER.toString()))
                .findFirst();


        if (fileNameStream.isPresent()) {
            String fileName = fileNameStream.get();


            switch (reportType) {
                case 10:
                    prepareTotalCustomerNumber(fileName);
                    break;
                case 11:
                    prepareTotalCustomerType(fileName);
                    break;
                case 12:
                    prepareTotalCustomerPhone(fileName, getFile(files, FileType.PHONE));
                    break;
                case 13:
                    prepareTotalCustomerMail(fileName, getFile(files, FileType.MAIL));
                    break;
                case 14:
                    prepareTotalCustomerPhoneMail(fileName, getFile(files, FileType.PHONE),
                            getFile(files, FileType.MAIL));
                    break;
            }


        }
    }

    private void prepareTotalCustomerNumber(String fileName) {

        prepareTotalNumber(fileName);
    }

    private void prepareTotalCustomerType(String customerFileName) {

        prepareSingleMappedData(customerFileName);
    }

    protected void prepareTotalCustomerPhone(String fileNameCustomer, String fileNamePhone) {

        this.jobType = CUSTOMER_PHONE;

        prepareMixedDataList(fileNameCustomer, fileNamePhone, "");
    }

    private void prepareTotalCustomerMail(String fileNameCustomer, String fileNameMail) {

        this.jobType = CUSTOMER_MAIL;
        prepareMixedDataList(fileNameCustomer, fileNameMail, "");
    }

    private void prepareTotalCustomerPhoneMail(String fileNameCustomer, String fileNamePhone, String fileNameMail) {

        this.jobType = CUSTOMER_PHONE_MAIL;
        prepareMixedDataList(fileNameCustomer, fileNamePhone, fileNameMail);
    }


    private Map<String, Long> processTotalCustomerTypeData(String[] readBuffer, long lineNumber, Map<String, Long> typesMap) {

        return processTotalTypeData(readBuffer, lineNumber, typesMap);
    }

    @Override
    protected List<String> processTotalCustomerMailData(String[] readBuffer, long linenumber, String fileNameMail) {

        Path mailPath = Paths.get(fileNameMail);

        return Arrays.asList(readBuffer).parallelStream().filter(line -> line != null && !line.isEmpty())
                .map(customerLine -> {

                    long mailCount = 0;
                    try (Stream<String> filteredLines = Files.lines(mailPath).filter(s ->
                            s.startsWith(customerLine.split(DELIMITER)[0] + DELIMITER))) {

                        mailCount = filteredLines.filter(line -> line.split(DELIMITER)[1].equalsIgnoreCase("E")).count();

                    } catch (IOException e) {
                        logger.error("Error on line {}",linenumber);
                    }

                    return customerLine.split(DELIMITER)[0].concat(DELIMITER).concat(String.valueOf(mailCount));
                }).collect(Collectors.toList());
    }

    @Override
    protected Map<List<String>, Long> processTotalPhoneCustomerData(String[] readBuffer, long linenumber, Map<List<String>, Long> combinedMap2) {
        return null;
    }


    @Override
    protected List<String> processTotalCustomerPhoneMail(String[] readBuffer, String fileNamePhone,
                                                         String fileNameMail) {
        Path phonePath = Paths.get(fileNamePhone);
        Path mailPath = Paths.get(fileNameMail);

        return Arrays.asList(readBuffer).parallelStream().filter(line -> line != null && !line.isEmpty())
                .map(customerLine -> {
                    String[] clineSplit = customerLine.split(DELIMITER);
                    long mailCount = 0;
                    String res = "";
                    try (Stream<String> filteredLines = Files.lines(phonePath).filter(s -> s.startsWith(clineSplit[0] + DELIMITER))
                    ) {

                        List<String> filteredList = filteredLines.collect(Collectors.toList());

                        Optional<String> cell = filteredList.parallelStream()
                                .filter(fLine -> fLine.split(DELIMITER)[1].equals("C")).findFirst();
                        res += !cell.isPresent() ? ";" : cell.get().split(DELIMITER)[2] + cell.get().split(DELIMITER)[3] + cell.get().split(DELIMITER)[4] + DELIMITER;

                        Optional<String> tell = filteredList.parallelStream()
                                .filter(fLine -> fLine.split(DELIMITER)[1].equals("T")).findFirst();
                        res += !tell.isPresent() ? ";" : tell.get().split(DELIMITER)[2] + tell.get().split(DELIMITER)[3] + tell.get().split(DELIMITER)[4] + DELIMITER;


                    } catch (IOException e) {
                        logger.error("Error on file {}",phonePath);
                    }

                    try (Stream<String> filteredLines = Files.lines(mailPath).filter(s -> s.startsWith(clineSplit[0] + DELIMITER))
                    ) {

                        Optional<String> mail = filteredLines.filter(fLine -> fLine.split(DELIMITER)[1].equals("E")).findFirst();
                        res += !mail.isPresent() ? "" : mail.get().split(DELIMITER)[2];


                    } catch (IOException e) {
                        logger.error("Error on file {}",mailPath);
                    }

                    return clineSplit[0].concat(DELIMITER).concat(res);
                }).collect(Collectors.toList());
    }

    @Override
    protected List<String> processTotalCustomerPhoneData(String[] readBuffer, long linenumber, String fileNamePhone) {

        //When filteredLines is closed, it closes underlying stream as well as underlying file.
        Path phonePath = Paths.get(fileNamePhone);

        return Arrays.asList(readBuffer).parallelStream().filter(line -> line != null && !line.isEmpty())
                .map(customerLine -> {
                    String phones = "";
                    try (Stream<String> filteredLines = Files.lines(phonePath).filter(s ->
                            s.startsWith(customerLine.split(DELIMITER)[0] + DELIMITER))) {

                        List<String> filteredList = filteredLines.collect(Collectors.toList());
                        filteredList.addAll(Arrays.asList(new String[]{"0;C", "0;F", "0;T"}));

                        Map<String, Long> phonetypeMap =
                                filteredList.parallelStream().collect(Collectors.groupingBy(line -> line.split(DELIMITER)[1], Collectors.counting()));

                        phones = phonetypeMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
                                .map(t -> String.valueOf(t.getValue() - 1)).collect(Collectors.joining(DELIMITER));

                    } catch (IOException e) {
                        logger.error("Error on file {}",phonePath);
                    }

                    return customerLine.split(DELIMITER)[0].concat(DELIMITER).concat(phones);
                }).collect(Collectors.toList());

    }

}
