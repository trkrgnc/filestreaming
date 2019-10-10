package com.garanti.test.customerproject.business;

import com.garanti.test.customerproject.constants.JobType;
import com.garanti.test.customerproject.error.ParameterException;
import com.garanti.test.customerproject.util.FileWriter;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.garanti.test.customerproject.constants.CustomerConstants.DELIMITER;
import static com.garanti.test.customerproject.constants.CustomerConstants.READ_BUFFER_SIZE;
import static com.garanti.test.customerproject.constants.JobType.DEFAULT;

public abstract class Process {

    public Process() {
        this.jobType = DEFAULT;
    }

    Logger logger = LoggerFactory.getLogger(getClass());

    private static ProcessService process;
    protected int reportType;
    protected String outputFile;
    protected FileWriter fileWriter = new FileWriter();
    protected JobType jobType;

    public static ProcessService getInstance(List<String> files, int reportType) throws ParameterException {
        if (process == null) {
            process = createProcess(files, reportType);
        }

        return process;
    }


    public static ProcessService createProcess(List<String> files, int reportType) throws ParameterException {

        switch (reportType) {
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                return new CustomerProcess();
            case 30:
            case 31:
                return new MailProcess();
            case 20:
            case 21:
            case 22:
                return new PhoneProcess();
            default:
                throw new ParameterException("Unknown report type, valid report types: 10, 11, 12, 13, 14, 20, 21, 22, 30, 31 ");
        }
    }

    protected void prepareTotalNumber(String fileName) {

        String totalCustomerNumber = "";

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

            totalCustomerNumber = String.valueOf(stream.skip(1).count());

        } catch (IOException e) {
            logger.error("Error on file {}",fileName);
        }

        String outFile = this.fileWriter.createFile(this.outputFile, fileName, this.reportType);
        this.fileWriter.writeFile(outFile, totalCustomerNumber, false);

    }

    protected abstract List<String> processTotalCustomerPhoneMail(String[] readBuffer, String fileNamePhone,
                                                                  String fileNameMail);


    protected void prepareData(String fileNameMaster, String fileNameSecond, String fileNameThird) {
        FileInputStream inputStream = null;
        String[] readBuffer = new String[READ_BUFFER_SIZE];
        int loopcounter = 1;
        long linenumber = 0;
        int index = 0;
        List<String> totalResult = new ArrayList<String>();
        Map<String, Long> combinedMap1 = new HashMap<String, Long>();
        Map<List<String>, Long> combinedMap2 = new HashMap<List<String>, Long>();
        boolean mappedJob = false;
        boolean bSort = false;

        try {
            inputStream = new FileInputStream(fileNameMaster);
        } catch (FileNotFoundException e) {
            logger.error("Error on file {}",fileNameMaster);
        }

        try(Scanner sc = new Scanner(inputStream, "UTF-8")){
            while (sc.hasNextLine()) {

                String line = sc.nextLine();
                //skip line 0
                if (linenumber == 0 || line.trim().isEmpty()) {
                    linenumber++;
                    continue;
                }

                readBuffer[index] = line;
                linenumber++;
                index++;

                if (linenumber >= (READ_BUFFER_SIZE * loopcounter) || !sc.hasNextLine()) {
                    logger.info("Start loop counter:  {}", loopcounter);

                    List<String> producedData = null;
                    switch (this.jobType) {
                        case CUSTOMER_PHONE_MAIL:
                            producedData = processTotalCustomerPhoneMail(readBuffer, fileNameSecond, fileNameThird);
                            bSort = true;
                            break;
                        case CUSTOMER_PHONE:
                            producedData = processTotalCustomerPhoneData(readBuffer, linenumber, fileNameSecond);
                            bSort = true;
                            break;
                        case CUSTOMER_MAIL:
                            producedData = processTotalCustomerMailData(readBuffer, linenumber, fileNameSecond);
                            bSort = true;
                            break;
                        case PHONE_GROUP:
                            combinedMap2 = processTotalPhoneCustomerData(readBuffer, linenumber, combinedMap2);
                            mappedJob = true;
                            bSort = true;
                            break;
                        default:
                            combinedMap1 = processTotalTypeData(readBuffer, linenumber, combinedMap1);
                            mappedJob = true;
                    }

                    if (!mappedJob) {
                        totalResult.addAll(producedData);
                    }

                    loopcounter++;
                    index = 0;

                    readBuffer = new String[READ_BUFFER_SIZE];

                }

            }
        }

        logger.info("Process of file completed; total number of lines: {}", linenumber);

        String outputData = "";
        if (!mappedJob) {
            outputData = totalResult.parallelStream().collect(Collectors.joining(System.lineSeparator()));
        } else {

            switch (jobType) {
                case PHONE_GROUP:
                    outputData = (String) combinedMap2.entrySet().parallelStream().map(k ->
                            {
                                return k.getKey().get(0) + DELIMITER +
                                        k.getKey().get(1) + DELIMITER +
                                        k.getValue();
                            }
                    ).collect(Collectors.joining(System.lineSeparator()));

                    break;
                default:
                    outputData = (String) combinedMap1.entrySet().parallelStream().map(k ->
                            k.getKey() + DELIMITER + k.getValue()).collect(Collectors.joining(System.lineSeparator()));
            }

        }

        String outFile = this.fileWriter.createFile(this.outputFile, fileNameMaster, this.reportType);
        this.fileWriter.writeFile(outFile, outputData, bSort);

    }


    protected void prepareMixedDataList(String fileNameMaster, String fileNameSecond, String fileNameThird) {

        prepareData(fileNameMaster, fileNameSecond, fileNameThird);
    }

    protected abstract List<String> processTotalCustomerPhoneData(String[] readBuffer, long linenumber, String fileNameSecond);

    protected abstract List<String> processTotalCustomerMailData(String[] readBuffer, long linenumber, String fileNameSecond);

    protected void prepareSingleMappedData(String fileName) {

        prepareData(fileName, "", "");
    }

    protected abstract Map<List<String>, Long> processTotalPhoneCustomerData(String[] readBuffer, long linenumber, Map<List<String>, Long> combinedMap2);


    protected Map<String, Long> processTotalTypeData(String[] readBuffer, long lineNumber, Map<String, Long> map) {

        List<String> types = Arrays.asList(readBuffer).parallelStream().filter(line -> line != null && !line.isEmpty())
                .map(line ->
                {
                    String type = "";
                    try {
                        type = line.split(";")[1];
                    } catch (Exception e) {
                        logger.error("Error on line " + lineNumber);
                    }

                    return type;
                }).collect(Collectors.toList());

        Map<String, Long> tempTypes = types.parallelStream().collect(Collectors.groupingBy(String::toString, Collectors.counting()));

        return Stream.concat(map.entrySet().stream(), tempTypes.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.summingLong(Map.Entry::getValue)));

    }
}
