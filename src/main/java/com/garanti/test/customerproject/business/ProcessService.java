package com.garanti.test.customerproject.business;

import java.util.List;

public interface ProcessService {
    void process(List<String> files, int reportType, String outputDir);

}
