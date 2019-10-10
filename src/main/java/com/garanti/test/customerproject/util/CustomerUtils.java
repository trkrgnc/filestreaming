package com.garanti.test.customerproject.util;

import com.garanti.test.customerproject.constants.FileType;

import java.io.File;
import java.util.List;
import java.util.Optional;

public final class CustomerUtils {

    public static boolean checkFile(List<String> files, FileType fileType){
        Optional<String> file = files.stream().filter(f ->
                new File(f).getName().equals(fileType.toString()))
                .findAny();


        return file.isPresent();
    }

    public static String getFile(List<String> files, FileType fileType) {

        Optional<String> file = files.stream().filter(f ->
                new File(f).getName().equalsIgnoreCase(fileType.toString()))
                .findAny();

        if(file.isPresent()){
            return file.get();
        }

        return "";
    }
}
