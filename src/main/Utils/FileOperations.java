import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileOperations {
    public static List<String> getFileAsList(String filePath){
        List<String> filewrapper = new ArrayList<>();
        try {
            FileReader fr = new FileReader(filePath, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while((line= br.readLine()) != null){
                line = line.replaceAll("\\b(Given|When|Then|And)\\b","*").replaceAll("#","");
                if (!line.isEmpty())
                    filewrapper.add(line.trim());
            }
            br.close();
            fr.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return filewrapper;
    }

    public static void writeToCsv(List<String> list, int indx) {
        try {
            String directoryPath = System.getProperty("user.home").concat("/Downloads/Scenarios/");
            String filePath = directoryPath.concat("scenario").concat(String.valueOf(indx)).concat(".csv");

            // Create directory if it doesn't exist
            Path directory = Paths.get(directoryPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            FileWriter fw = new FileWriter(filePath, StandardCharsets.UTF_8);
            BufferedWriter bw = new BufferedWriter(fw);
            list.forEach(x -> {
                try {
                    bw.write(x.substring(0,x.lastIndexOf(';')));
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            bw.close();
            fw.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void createCSV(List<String> files){
        for (int i = 0; i < files.size() ; i++) {
            List<String> fileAsList = FileOperations.getFileAsList(files.get(i));
            //List<String> list = TextConverter.getResult(fileaslist);
            //FileOperations.writeToCsv(list,(i+1));
            try {
                createExcelFile(fileAsList,(i+1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Boolean getMessage(int indx){
        boolean message=false;

        try {
            for (int i = 1; i <= indx; i++) {
                String desktopPath = System.getProperty("user.home") + "/Downloads/Scenarios/";
                String fileName = "scenario".concat(String.valueOf(i)).concat(".xlsx");

                File file = new File(desktopPath, fileName);

                for (int j = 0; j < 10; j++) {
                    if (file.exists()) {
                        message =true;
                        String rootPath = System.getProperty("user.home");
                        String directoryPathBase = rootPath + "/Downloads/Scenarios/Scenario%s.xlsx";
                        String directoryPathNew = rootPath + "/Downloads/Scenarios/Scenario%s.csv";
                        com.aspose.cells.Workbook w1 = w1 = new com.aspose.cells.Workbook(String.format(directoryPathBase, String.valueOf(i)));
                        w1.save(String.format(directoryPathNew, String.valueOf(i)));
                        break;
                    }else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }}
        } catch (Exception e) {
            e.printStackTrace();
        }

            return message;
    }

    public static void createExcelFile(List<String> list, int indx) throws Exception {
        String directoryPath = System.getProperty("user.home").concat("/Downloads/Scenarios/");
        String filePath = directoryPath.concat("scenario").concat(String.valueOf(indx)).concat(".xlsx");
        // Sample string list
        List<String> headers = TextConverter.headers;

        // Sample list of maps
        List<Map<String, String>> data = TextConverter.getScenarioMaps(list);

        // Create a new Excel workbook
        Workbook workbook = new XSSFWorkbook();
        try {
            // Create a sheet
            Sheet sheet = workbook.createSheet("Feature".concat(String.valueOf(indx)));

            // Write headers
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }

            // Write data
            int rowNum = 1;
            for (Map<String, String> map : data) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (String header : headers) {
                    String value = map.get(header);
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(value);
                }
            }

            Path directory = Paths.get(directoryPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            // Write the workbook to a file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            }
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
