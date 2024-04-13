package test.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.petrovoleh.parser.Parser;
import org.junit.jupiter.api.Test;

public class MultiThreadTest {

    private static final String DIRECTORY_PATH = "./test_jsons/many_files"; // Replace with actual directory path
    private static final String ATTRIBUTE = "amount"; // Replace with actual attribute

    @Test
    public void testParsingSpeed() {
        JsonFactory jsonFactory = new JsonFactory();
        int[] threadCounts = {1, 1, 2, 4, 8, 10}; // Test with different numbers of threads

        for (int threadCount : threadCounts) {
            long startTime = System.currentTimeMillis();
            Parser.parseOrders(jsonFactory, DIRECTORY_PATH, ATTRIBUTE, threadCount);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Time taken with " + threadCount + " thread(s): " + duration + " ms");
        }
    }
}
