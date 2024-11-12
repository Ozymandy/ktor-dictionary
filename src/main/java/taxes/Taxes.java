package taxes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.TreeMap;

public class Taxes {

    private static final BigDecimal PERCENTAGE = BigDecimal.valueOf(100);

    private TreeMap<BigDecimal, BigDecimal> taxBands;

    public Taxes() {
        this.taxBands = new TreeMap<>();
    }

    public void addBand(BigDecimal max, BigDecimal taxation) {
        if (max == null || max.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Wrong max bound");
        }
        if (taxation == null || taxation.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Wrong taxation");
        }
        taxBands.put(max, taxation.divide(PERCENTAGE, RoundingMode.HALF_EVEN));
    }

    public void addBand(Integer max, Double taxation) {
        addBand(new BigDecimal(max), new BigDecimal(taxation).setScale(4, RoundingMode.HALF_EVEN));
    }

    public PoundPence calculatePoundPence(final PoundPence salary) {
        if (taxBands.isEmpty()) {
            return PoundPence.ZERO;
        }
        BigDecimal salaryInBigDecimal = poundPenceToBigDecimal(salary);
        BigDecimal taxationValue = Optional.ofNullable(taxBands.floorEntry(salaryInBigDecimal))
                .orElseGet(() -> taxBands.firstEntry()).getValue();
        BigDecimal result = salaryInBigDecimal.multiply(taxationValue).setScale(2, RoundingMode.HALF_EVEN);
        return new PoundPence(result.intValue(), result.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).byteValue());
    }

    private BigDecimal poundPenceToBigDecimal(final PoundPence poundPence) {
        return new BigDecimal(poundPence.toString());
    }
}
