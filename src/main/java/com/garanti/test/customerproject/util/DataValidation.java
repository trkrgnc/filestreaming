package com.garanti.test.customerproject.util;

import com.garanti.test.customerproject.business.CustomerProcess;
import com.garanti.test.customerproject.business.MailProcess;
import com.garanti.test.customerproject.business.PhoneProcess;
import com.garanti.test.customerproject.constants.FileType;
import com.garanti.test.customerproject.error.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class DataValidation {

    static Logger logger = LoggerFactory.getLogger(DataValidation.class);

    public static int checkReportType(List<String> a) throws ParameterException {

        try {
            int argSize = a.size();
                    
            String r = a.get(argSize-2);
            return Integer.parseInt(r);
        } catch (NumberFormatException e) {

            throw new ParameterException("Invalid report type, valid report types: 10, 11, 12, 13, 14, 20, 21, 22, 30, 31 ");
        }

    }

    public static String checkOutputDirectory(List<String> a) throws ParameterException {

        int argSize = a.size();

        File f = new File(a.get(argSize-1));

        if(!f.isDirectory()){
            throw new ParameterException("Last paramater should be a valid directory path.");

        }

        return a.get(argSize-1);

    }

    public static List<String> checkInputFiles(List<String> a) throws ParameterException{

        int argSize = a.size();

        List<String> inputFiles = a.subList(0, argSize-2);

        boolean b = inputFiles.stream().anyMatch(inf ->  !new File(inf).isFile());
        boolean e = inputFiles.stream().anyMatch(inf -> !new File(inf).exists());


        inputFiles.forEach( (inf) ->
                {
                    File f = new File(inf);
                    if(!f.isFile()) {
                        logger.error("Required input file does not exist or not valid " + inf);
                    } else if (!f.exists()) {
                        logger.error("Input file does not exist: " + inf);
                    }
                }
        );

        if(b) {
            throw new ParameterException("Required input file does not exist or not valid");
        } else if(e) {
            throw new ParameterException("One or more input files does not exist!");
        }

        checkFiles(a);

        return inputFiles;
    }

    static boolean findEndsWith(List<String> list, String str){

        Stream<String> any = list.stream().filter(
                input -> input.toLowerCase().endsWith(str.toLowerCase())
        );

        if(any.count() > 0) {
            return true;
        }


        return false;
    }

    private static void checkFiles(List<String> a) throws ParameterException {
        int reportType = checkReportType(a);
        int argSize = a.size();
        List<String> inputFiles = a.subList(0, argSize-2);
        boolean correct = true;
        String missingFile = "";

        switch (reportType) {
            case 10:
            case 11:
                correct = findEndsWith(inputFiles,FileType.CUSTOMER.toString()) ? true : false;
                break;
            case 12:
                correct = findEndsWith(inputFiles, FileType.CUSTOMER.toString())
                        && findEndsWith(inputFiles, FileType.PHONE.toString()) ?
                        true : false;
                break;
            case 13:
                correct = findEndsWith(inputFiles, FileType.CUSTOMER.toString())
                        && findEndsWith(inputFiles, FileType.MAIL.toString()) ?
                        true : false;
                break;
            case 14:
                correct =  findEndsWith(inputFiles,FileType.CUSTOMER.toString())
                        && findEndsWith(inputFiles, FileType.PHONE.toString())
                        &&  findEndsWith(inputFiles,FileType.MAIL.toString()) ?
                        true : false;
                break;

            case 30:
                correct = findEndsWith(inputFiles,FileType.MAIL.toString()) ? true : false;
                break;
            case 31:
                correct = findEndsWith(inputFiles,FileType.MAIL.toString()) ? true : false;
                break;

            case 20:
                correct = findEndsWith(inputFiles,FileType.PHONE.toString()) ? true : false;
                break;
            case 21:
                correct = findEndsWith(inputFiles,FileType.PHONE.toString()) ? true : false;
                break;
            case 22:
                correct = findEndsWith(inputFiles,FileType.PHONE.toString()) ? true : false;
                break;

            default:
                throw new ParameterException("Unknown report type, valid report types: 10, 11, 12, 13, 14, 20, 21, 22, 30, 31 ");
        }

        if(!correct){
            throw  new ParameterException("Missing one or more required input file for report " + reportType);
        }
    }
}
