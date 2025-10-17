import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class MtsTableTest {

    private MtsTable mtsTable;

    @Before
    public void setUp() {
        mtsTable = new MtsTable();
    }

    @Test
    public void testCreateTimeNode() {
        // Setup
        int start = 0;
        int end = 5;

        // Exercise
        mtsTable.createTimeNode(start, end);

        // Verify
        assertEquals("TimeListHead size should be equal to end-start + 1", end - start + 1, mtsTable.TimeListHead.size());

        for (int i = 0; i < mtsTable.TimeListHead.size(); i++) {
            TimeList current = mtsTable.TimeListHead.get(i);
            assertEquals("Time value should match", i + start, current.time);

            if (i > 0) {
                assertNotNull("Previous time node should not be null", current.preTime);
                assertEquals("Previous time node should match", mtsTable.TimeListHead.get(i - 1), current.preTime);
            }

            if (i < mtsTable.TimeListHead.size() - 1) {
                assertNotNull("Next time node should not be null", current.nextTime);
                assertEquals("Next time node should match", mtsTable.TimeListHead.get(i + 1), current.nextTime);
            }
        }
    }

    @Test
    public void testCreateTimeNodeWithNegativeStart() {
        // Setup
        int start = -2;
        int end = 0;

        // Exercise
        mtsTable.createTimeNode(start, end);

        // Verify
        assertEquals("TimeListHead size should be equal to end-start + 1, adjusting for negative start", end - start + 1, mtsTable.TimeListHead.size());
    }

    @Test
    public void testCreateTimeNodeWithSameStartAndEnd() {
        // Setup
        int point = 5;

        // Exercise
        mtsTable.createTimeNode(point, point);

        // Verify
        assertEquals("TimeListHead should contain one node for identical start and end", 1, mtsTable.TimeListHead.size());
        assertNotNull("The single node should not be null", mtsTable.TimeListHead.get(0));
        assertEquals("Node time should match the point", point, mtsTable.TimeListHead.get(0).time);
    }

    @Test
    public void testCreateTimeNodeWithEndLessThanStart() {
        // Setup
        int start = 5;
        int end = 3;

        // Exercise
        mtsTable.createTimeNode(start, end);

        // Verify
        assertTrue("TimeListHead should be empty when end is less than start", mtsTable.TimeListHead.isEmpty());
    }

    public static void main(String[] args) {
        MtsTableTest ts = new MtsTableTest();
        ts.setUp();
        ts.testCreateTimeNode();
        ts.testCreateTimeNodeWithNegativeStart();
        ts.testCreateTimeNodeWithSameStartAndEnd();
        ts.testCreateTimeNodeWithEndLessThanStart();
    }

}
