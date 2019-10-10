package com.garanti.test.customerproject.business;

import com.garanti.test.customerproject.error.ParameterException;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ReportProcessorTest {


    //@Test
    public void testAll() throws ParameterException {

        testRun("14");

    }

    //@Test
    public void testRun(String type) {

        List<String> params = new ArrayList<>();

        params.add("C:\\DEVEL\\Temp\\MUSTERI.txt");
        params.add("C:\\DEVEL\\Temp\\TELEFON.txt");
        params.add("C:\\DEVEL\\Temp\\MAIL.txt");

        params.add(type);

        params.add("C:\\DEVEL\\Temp\\Output");

        String[] arr = new String[params.size()];
        ReportProcessor.getInstance().run(params.toArray(arr));
    }


    public void processCustomer() throws ParameterException {

        List<String> files = new ArrayList<>();
        files.add("C:\\DEVEL\\Temp\\MUSTERI.txt");
        files.add("C:\\DEVEL\\Temp\\TELEFON.txt");
        files.add("C:\\DEVEL\\Temp\\MAIL.txt");

        ReportProcessor.getInstance().process(10, files, "C:\\DEVEL\\Temp\\Output");
        ReportProcessor.getInstance().process(11, files, "C:\\DEVEL\\Temp\\Output");
        ReportProcessor.getInstance().process(12, files, "C:\\DEVEL\\Temp\\Output");
        ReportProcessor.getInstance().process(13, files, "C:\\DEVEL\\Temp\\Output");
        ReportProcessor.getInstance().process(14, files, "C:\\DEVEL\\Temp\\Output");
    }


    public void processPhone() throws ParameterException {

        List<String> files = new ArrayList<>();
        files.add("C:\\DEVEL\\Temp\\MUSTERI.txt");
        files.add("C:\\DEVEL\\Temp\\TELEFON.txt");
        files.add("C:\\DEVEL\\Temp\\MAIL.txt");

        ReportProcessor.getInstance().process(20, files, "C:\\DEVEL\\Temp\\Output");
        ReportProcessor.getInstance().process(21, files, "C:\\DEVEL\\Temp\\Output");
        ReportProcessor.getInstance().process(22, files, "C:\\DEVEL\\Temp\\Output");
    }


    public void processMail() throws ParameterException {

        List<String> files = new ArrayList<>();
        files.add("C:\\DEVEL\\Temp\\MUSTERI.txt");
        files.add("C:\\DEVEL\\Temp\\TELEFON.txt");
        files.add("C:\\DEVEL\\Temp\\MAIL.txt");

        ReportProcessor.getInstance().process(30, files, "C:\\DEVEL\\Temp\\Output");
        ReportProcessor.getInstance().process(31, files, "C:\\DEVEL\\Temp\\Output");
    }

    @Ignore
    @Test
    public void start() {
    }

}