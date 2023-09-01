import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CompareCSVFiles {
    public static void main(String[] args) {
        String file1Path = "CSVFiles/abc1.csv";
        String file2Path = "CSVFiles/xyz1.csv";
        String outputFilePath = "CSVFiles/differences.csv";

        try (
                Reader reader1 = new FileReader(file1Path);
                Reader reader2 = new FileReader(file2Path);
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))
        ) {
            Iterable<CSVRecord> records1 = CSVFormat.DEFAULT.parse(reader1);
            Iterable<CSVRecord> records2 = CSVFormat.DEFAULT.parse(reader2);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Line Number", "File1 Content", "File2 Content"));

            Iterator<CSVRecord> iterator1 = records1.iterator();
            Iterator<CSVRecord> iterator2 = records2.iterator();

            int lineCount = 0;

            while (iterator1.hasNext() || iterator2.hasNext()) {
                lineCount++;
                CSVRecord record1 = null;
                CSVRecord record2 = null;

                try {
                    record1 = iterator1.hasNext() ? iterator1.next() : null;
                } catch (NoSuchElementException | IllegalStateException e) {
                    System.err.println("Error reading line " + lineCount + " from file1: " + e.getMessage());
                }

                try {
                    record2 = iterator2.hasNext() ? iterator2.next() : null;
                } catch (NoSuchElementException | IllegalStateException e) {
                    System.err.println("Error reading line " + lineCount + " from file2: " + e.getMessage());
                }

                // Handle empty or missing lines
                String line1 = (record1 == null || !record1.iterator().hasNext()) ? "N/A" : String.join(",", record1);
                String line2 = (record2 == null || !record2.iterator().hasNext()) ? "N/A" : String.join(",", record2);

                if (line1.equals(line2)) {
                    continue;
                }

                csvPrinter.printRecord(lineCount, line1, line2);
            }

            csvPrinter.flush();

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("An I/O error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}
