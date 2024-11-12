import com.katemedia.taxes.PoundPence;
import com.katemedia.taxes.Taxes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaxesTest {

    private static final Taxes taxes = new Taxes();
    @BeforeAll
    public static void setUp() {
        taxes.addBand(0, 1.89);
        taxes.addBand(10001, 2.5);
        taxes.addBand(20001, 4.75);
        taxes.addBand(50001, 8.25);
        taxes.addBand(100001, 10.5);
    }

    @Test
    public void test() {
        assertEquals(taxes.calculatePoundPence(new PoundPence(10002, (byte) 68)).toString(), new PoundPence(250, (byte) 7).toString());
    }
}
