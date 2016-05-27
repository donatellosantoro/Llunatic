package it.unibas.lunatic.test.persistence;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import junit.framework.TestCase;

public class TestCSV extends TestCase {

    public void test() throws IOException {
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        CsvSchema schema = CsvSchema.emptySchema();
//        schema = schema.withHeader();
//        schema = schema.withQuoteChar('\'');
//        File csvFile = new File("/Temp/llunatic/doctors/10k/doctor.csv");
        File csvFile = new File("/Users/donatello/Temp/chaseBench-workspace/LUBM/data/01k/src-emailAddress.csv");
        long start = new Date().getTime();
        MappingIterator<String[]> it = mapper.readerFor(String[].class).with(schema).readValues(csvFile);
        String[] row = it.next();
        System.out.println(Arrays.asList(row));
        long end = new Date().getTime();
        System.out.println("**** " + (end - start) + " ms");
//        while (it.hasNext()) {
//            String[] row = it.next();
//            System.out.println(Arrays.asList(row));
//        }
    }

}
