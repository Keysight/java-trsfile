import java.io.Serializable;

public class XYZDefaultTestData implements Serializable {
    private int x;
    private int y;
    private int z;

    public XYZDefaultTestData() {
    }

    public XYZDefaultTestData(int x, int y, int z) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XYZDefaultTestData that = (XYZDefaultTestData) o;
        return x == that.x &&
                y == that.y &&
                z == that.z;
    }
}
