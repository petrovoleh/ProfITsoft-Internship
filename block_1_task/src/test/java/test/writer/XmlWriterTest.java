package test.writer;

import com.petrovoleh.util.XmlWriter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Клас для тестування класу XmlWriter.
 */
public class XmlWriterTest {

    /**
     * Тест для перевірки запису статистики у XML з валідними даними.
     *
     * @throws IOException у випадку помилки вводу/виводу
     */
    @Test
    void testWriteStatisticsToXML_ValidData() throws IOException {
        // Підготування тестових даних
        Map<String, Integer> orderStatistics = new HashMap<>();
        orderStatistics.put("value1", 10);
        orderStatistics.put("value2", 20);

        // Виклик методу, який тестується
        XmlWriter.writeStatisticsToXML("./test",orderStatistics, "attribute");

        // Читання вмісту згенерованого XML-файлу
        String fileName = "./test/order_statistics_by_attribute.xml";
        File outputFile = new File(fileName);
        assertTrue(outputFile.exists(), "Output file doesn't exist");

        // Читання вмісту файлу
        byte[] fileContentBytes = java.nio.file.Files.readAllBytes(outputFile.toPath());
        String actualXmlString = new String(fileContentBytes, StandardCharsets.UTF_8);

        // Очікуваний вміст XML
        String expectedXmlString = """
                <statistics>
                  <item>
                    <value>value2</value>
                    <count>20</count>
                  </item>
                  <item>
                    <value>value1</value>
                    <count>10</count>
                  </item>
                </statistics>
                """;
        expectedXmlString = expectedXmlString.replace("\n", "\r\n");

        // Перевірка відповідності вмісту файлу очікуваному XML-рядку
        assertEquals(expectedXmlString.strip(), actualXmlString.strip(), "XML content mismatch");
    }

    /**
     * Тест для перевірки винятку під час запису у XML з порожнім іменем атрибуту.
     */
    @Test
    void testWriteStatisticsToXML_Exception() {
        // Виклик методу, який тестується з неправильним ім'ям файлу, щоб спричинити IllegalArgumentException
        Map<String, Integer> orderStatistics = new HashMap<>();

        // Використання assertThrows для перевірки, що IllegalArgumentException виникає
        try {
            XmlWriter.writeStatisticsToXML("./test",orderStatistics, ""); // Порожнє ім'я файлу
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            System.err.println(message);
            // Перевірка повідомлення про виняток
            assertTrue(message.contains("The attribute cannot be empty"));
            return;
        }
        fail();
    }

    /**
     * Тест для перевірки винятку під час запису у XML з порожнім списком статистики.
     */
    @Test
    void testWriteStatisticsToXML_InvalidData() {
        // Підготування тестових даних
        Map<String, Integer> orderStatistics = new HashMap<>();

        try {
            XmlWriter.writeStatisticsToXML("./test",orderStatistics, "attribute");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            // Перевірка повідомлення про виняток
            assertTrue(e.getMessage().contains("The orderStatistics cannot be empty"));
            return;
        }
        fail();
    }
}
