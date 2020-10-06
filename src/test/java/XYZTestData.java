import com.riscure.trs.parameter.trace.TraceParameter;

public class XYZTestData implements TraceParameter {
    private int x;
    private int y;
    private int z;

    public XYZTestData() {
    }

    public XYZTestData(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public byte[] serialize() {
        return new byte[] {(byte) (x >> 24 & 0xFF),
                (byte) (x >> 16 & 0xFF),
                (byte) (x >> 8 & 0xFF),
                (byte) (x & 0xFF),
                (byte) (y >> 24 & 0xFF),
                (byte) (y >> 16 & 0xFF),
                (byte) (y >> 8 & 0xFF),
                (byte) (y & 0xFF),
                (byte) (z >> 24 & 0xFF),
                (byte) (z >> 16 & 0xFF),
                (byte) (z >> 8 & 0xFF),
                (byte) (z & 0xFF)};
    }

    @Override
    public void deserialize(byte[] bytes) {
        setX(bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3]);
        setY(bytes[4] << 24 | bytes[5] << 16 | bytes[6] << 8 | bytes[7]);
        setZ(bytes[8] << 24 | bytes[9] << 16 | bytes[10] << 8 | bytes[11]);
    }

    @Override
    public int length() {
        return 3 * Integer.BYTES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XYZTestData that = (XYZTestData) o;
        return x == that.x &&
                y == that.y &&
                z == that.z;
    }
}
