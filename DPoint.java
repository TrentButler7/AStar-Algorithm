/**
 * Represents a Euclidian coordinate point,
 * with double floating point precision.
 * 
 * @author Carl Stephens (1505521)
 * @author Trent Butler (1522993)
 */
public class DPoint implements Cloneable {
    public double x;
    public double y;

    public DPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * Performs a fast Euclidean estimate calculation, using https://www.flipcode.com/archives/Fast_Approximate_Distance_Functions.shtml
     * This has between a 2.5-5% average error compared to the true value.
     * @param point The point to find the distance to.
     * @return The estimate of the Euclidean distance
     */
    public double fastDistance(DPoint point) {
        double x = getX() - point.getX();
        double y = getY() - point.getY();
        x = Math.abs(x);
        y = Math. abs(y);

        double max = 0, min = 0;
        if (x > y) {
            max = x;
            min = y;
        }
        else {
            max = y;
            min = x;
        }

        return (1007.0 / 1024.0) * max + (441.0 / 1024.0) * min;
    }

    /**
     * Creates a new object of the same class and with the
     * same contents as this object.
     * @return     A clone of this instance.
     * @see        java.lang.Cloneable
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // This shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }

    /**
     * Returns the hashcode for this {@code DPoint}.
     * @return A hash code for this {@code DPoint}.
     */
    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(getX());
        bits ^= java.lang.Double.doubleToLongBits(getY()) * 31;
        return (((int)bits) ^ ((int)(bits >> 32)));
    }

    /**
     * Determines whether or not two points are equal. Two instances of
     * {@code DPoint} are equal if the values of their
     * {@code x} and {@code y} member fields, representing
     * their position in the coordinate space, are the same.
     * @param obj an object to be compared with this {@code DPoint}
     * @return {@code true} if the object to be compared is
     *         an instance of {@code DPoint} and has
     *         the same values; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DPoint) {
            DPoint p = (DPoint)obj;
            return (getX() == p.getX()) && (getY() == p.getY());
        }

        return super.equals(obj);
    }
}
