package taxes;

public class PoundPence {

    public static final PoundPence ZERO = new PoundPence(0, (byte) 0);
    private final Integer pounds;
    private final Byte pences;

    public PoundPence(Integer pounds, Byte pences) {
        this.pounds = pounds;
        this.pences = pences;
    }

    public Byte getPences() {
        return pences;
    }

    public Integer getPounds() {
        return pounds;
    }

    @Override
    public String toString() {
        return pounds + "." + pences;
    }
}
