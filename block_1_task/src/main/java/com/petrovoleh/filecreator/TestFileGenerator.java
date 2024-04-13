package com.petrovoleh.filecreator;

public class TestFileGenerator {

    public static void main(String[] args) {
        // Генеруємо JSON-файли для тестування JsonParserTest

        // Директорія, де будуть збережені тестові JSON-файли
        String testJsonDirectory = "./test_jsons";

        // Кількість записів у кожному JSON-файлі
        int numberOfEntries = 100;

        // Кількість файлів, які потрібно створити для кожного сценарію
        int numberOfFiles = 100;

        try {
            // Генеруємо JSON-файли для сценарію коректного парсингу JSON
            generateValidJsonFiles(testJsonDirectory, numberOfEntries, numberOfFiles);

            // Генеруємо JSON-файли для сценарію багато файлів
            generateManyFilesJsonFiles(testJsonDirectory, numberOfEntries);

            // Генеруємо JSON-файли для сценарію великого файлу
            generateBigFileJsonFiles(testJsonDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateValidJsonFiles(String directory, int numberOfEntries, int numberOfFiles) throws Exception {
        System.out.println("Генеруємо коректні JSON-файли...");
        String[] args = {String.valueOf(numberOfEntries), directory + "/valid_json", String.valueOf(numberOfFiles)};
        JsonFileCreator.main(args);
    }

    private static void generateManyFilesJsonFiles(String directory, int numberOfEntries) throws Exception {
        System.out.println("Генеруємо JSON-файли для сценарію багато файлів...");
        String[] args = {String.valueOf(500000), directory + "/many_files", "100"};
        JsonFileCreator.main(args);
    }

    private static void generateBigFileJsonFiles(String directory) throws Exception {
        System.out.println("Генеруємо JSON-файли для сценарію великого файлу...");
        String[] args = {"10000000", directory + "/big_file", "10"};
        JsonFileCreator.main(args);
    }
}
