package com.garanti.test.customerproject.util;

import com.github.davidmoten.bigsorter.Sorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class FileWriter {

    Logger logger = LoggerFactory.getLogger(getClass());

    short MAX_FILENUM = 1000;
    final String OUTPUT_FILE = "RAPOR_";
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static FileWriter fileWriter;

    public static FileWriter getInstance(){
        if(fileWriter == null) {
            fileWriter = new FileWriter();
        }

        return fileWriter;
    }

    public String createFile(String path, String fileName, long reportType) {

        StringBuilder stringBuilder = new StringBuilder(OUTPUT_FILE);
        String resultFile = null;

        short initialVersion = 0;
        StringBuilder fileSS = stringBuilder.append("_"+ reportType + "_")
                .append("_" + FORMATTER.format(LocalDate.now()) + "_V_");

        while (initialVersion <  MAX_FILENUM) {

            String filePathString = fileSS.toString().concat(String.format("%03d",initialVersion)).concat(".TXT").toString();
            Path currentPath = Paths.get(new File(path,filePathString).getPath());
            if(Files.exists(currentPath)){
                initialVersion ++;
            } else {
                resultFile = currentPath.toString();
                break;
            }

        }

        return  resultFile;

    }

    public void writeFile(String filePath, String data, boolean bSort) {
        logger.info("Write to output file {}", filePath);
        Path path = Paths.get(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path))
        {
            try {
                writer.write(data);
            } catch (IOException e) {
                logger.error("Error on write file {}",filePath);
            }
        } catch (IOException e) {
            logger.error("Error on write file {}",filePath);
        }


        if(bSort) {
            logger.info("Sorting process start..");
            sortFile(filePath);
        }
    }

    public void sortFile(String filePath) {

        File in = new File(filePath);
        File out = new File(filePath + ".sorted");

        Sorter
                // set both serializer and natural comparator
                .serializerLinesUtf8()
                .comparator( Comparator.comparingLong(s -> Long.parseLong(s.split(";")[0])))
                .input(in)
                .output(out)
                .maxFilesPerMerge(100) // default is 100
                .maxItemsPerFile(100000) // default is 100,000
                .bufferSize(8192) // default is 8192
                .sort();


        logger.info("Sorting process complete..");
        in.delete();
        out.renameTo(in);
    }
}
