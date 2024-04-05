package test;

import org.example.util.XmlWriter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlWriterTest {
    @Test
    void testWriteStatisticsToXML_ValidData() throws IOException {
        // Prepare test data
        Map<String, Integer> orderStatistics = new HashMap<>();
        orderStatistics.put("value1", 10);
        orderStatistics.put("value2", 20);

        // Call the method under test
        XmlWriter.writeStatisticsToXML(orderStatistics, "attribute");

        // Read the content of the generated XML file
        String fileName = "order_statistics_by_attribute.xml";
        File outputFile = new File(fileName);
        assertTrue(outputFile.exists(), "Output file doesn't exist");

        // Read content of the file
        byte[] fileContentBytes = java.nio.file.Files.readAllBytes(outputFile.toPath());
        String actualXmlString = new String(fileContentBytes, StandardCharsets.UTF_8);

        // Define the expected XML content
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

        // Assert that the content of the file matches the expected XML string
        assertEquals(expectedXmlString.strip(), actualXmlString.strip(), "XML content mismatch");
    }


    @Test
    void testWriteStatisticsToXML_Exception() {
        // Redirect System.err to capture printed error messages
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorStream));

        // Call the method under test with invalid file name to trigger IOException
        Map<String, Integer> orderStatistics = new HashMap<>();
        XmlWriter.writeStatisticsToXML(orderStatistics, ""); // Empty file name
        // Verify error message
        assertTrue(errorStream.toString().contains("Error writing order statistics to XML file: The attribute cannot be empty"));

        // Restore original System.err
        System.setErr(System.err);
    }
}
