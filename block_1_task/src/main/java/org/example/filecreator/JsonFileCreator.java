package org.example.filecreator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Order;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Цей клас призначений для створення JSON файлу для проєкту.
 * Він отримує один цілочисельний аргумент, який визначає кількість сутностей в JSON файлі.
 * Зберігає файл в ./test_jsons/big_file/orders.json, що може бути змінено в коді.
 */
public class JsonFileCreator {
    private static final ObjectMapper MAPPER = new ObjectMapper(); // Об'єкт ObjectMapper для серіалізації об'єктів в JSON
    private static final JsonFactory FACTORY = new JsonFactory(); // Фабрика JsonFactory для створення JsonGenerator
    private static final String[] NAMES = {"John", "Jane", "Michael", "Emma", "William", "Olivia", "James", "Sophia", "Benjamin", "Isabella"}; // Масив імен клієнтів
    private static final long START_DATE_MILLIS = 1672531200000L; // Початкова дата у мілісекундах з января 2023 року
    private static final long END_DATE_MILLIS = 1704067199000L;   // Кінцева дата у мілісекундах з грудня 2023 року

    /**
     * Генерує випадкову дату між заданими початковою та кінцевою датами.
     * @return випадкова дата
     */
    private static Date generateRandomDate() {
        long randomMillisSinceEpoch = ThreadLocalRandom.current().nextLong(START_DATE_MILLIS, END_DATE_MILLIS);
        return new Date(randomMillisSinceEpoch);
    }

    /**
     * Генерує список випадкових товарів.
     * @return список випадкових товарів
     */
    private static List<String> generateRandomItems() {
        List<String> items = new ArrayList<>();
        int numberOfItems = ThreadLocalRandom.current().nextInt(1, 11); // Випадкова кількість товарів від 1 до 10
        IntStream.rangeClosed(1, numberOfItems)
                .forEach(i -> items.add("Item " + ThreadLocalRandom.current().nextInt(1, 1001))); // Додавання випадкового товару до списку
        return items;
    }

    /**
     * Вибирає випадкове ім'я клієнта.
     * @return випадкове ім'я клієнта
     */
    private static String getRandomClientName() {
        return NAMES[ThreadLocalRandom.current().nextInt(0, NAMES.length)];
    }

    /**
     * Записує замовлення в формат JSON.
     * @param generator генератор JSON
     * @param order замовлення для запису
     * @throws IOException якщо виникає помилка запису
     */
    private static void writeOrderToJson(JsonGenerator generator, Order order) throws IOException {
        synchronized (generator) {
            MAPPER.writeValue(generator, order);
        }
    }

    /**
     * Обробляє замовлення: генерує дані та записує у формат JSON.
     * @param i номер замовлення
     * @param numberOfEntries загальна кількість замовлень
     * @param generator генератор JSON
     */
    private static void processOrder(int i, int numberOfEntries, JsonGenerator generator) {
        Order order = new Order();
        order.setOrderId(i);
        order.setOrderDate(generateRandomDate());
        order.setAmount(ThreadLocalRandom.current().nextInt(1, 101)); // Випадкова сума замовлення від 1 до 100
        order.setItems(generateRandomItems());
        order.setClient(getRandomClientName());

        try {
            writeOrderToJson(generator, order); // Запис замовлення у формат JSON
            printProgress(i, numberOfEntries); // Вивід прогресу
        } catch (IOException e) {
            throw new RuntimeException("Помилка запису замовлення у формат JSON", e);
        }
    }

    /**
     * Виводить прогрес створення JSON файлу.
     * @param i номер замовлення
     * @param numberOfEntries загальна кількість замовлень
     */
    private static void printProgress(int i, int numberOfEntries) {
        if (i % (numberOfEntries / 10) == 0) {
            int percentDone = (int) (((double) i / numberOfEntries) * 100);
            System.out.println("Прогрес: " + percentDone + "%");
        }
    }

    /**
     * Головний метод програми, який створює JSON файл.
     * @param args аргументи командного рядка (кількість записів)
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Будь ласка, вкажіть кількість записів у вигляді аргументу командного рядка.");
        }

        int numberOfEntries = Integer.parseInt(args[0]);
        if (numberOfEntries <= 0) {
            throw new IllegalArgumentException("Кількість записів повинна бути позитивним числом.");
        }

        try (FileOutputStream fos = new FileOutputStream("./test_jsons/big_file/orders.json");
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             JsonGenerator generator = FACTORY.createGenerator(bos)) {

            DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
            printer.indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance);
            generator.setPrettyPrinter(printer);

            generator.writeStartArray();
            IntStream.rangeClosed(1, numberOfEntries)
                    .parallel()
                    .forEach(i -> processOrder(i, numberOfEntries, generator));
            generator.writeEndArray();

            System.out.println("JSON файл успішно створено.");
        } catch (IOException e) {
            throw new RuntimeException("Помилка створення JSON файлу", e);
        }
    }
}
