import java.awt.Point;

public class Util {
    /**
     * Fast Euclidean estimate calculation, using https://www.flipcode.com/archives/Fast_Approximate_Distance_Functions.shtml
     * @param point1 The first point
     * @param point2 The second point
     * @return The estimate of the Euclidean distance
     */
    static public double getEuclidean(Point point1, Point point2) {
        //return Math.sqrt((currentPoint.getY() - goal.getY()) * (currentPoint.getY() - goal.getY()) + (currentPoint.getX() - goal.getX()) * (currentPoint.getX() - goal.getX())); Actual Euclidean distace calc
        int x = (int)point1.getX() - (int)point2.getX();
        int y = (int)point1.getY() - (int)point2.getY();
        x = Math.abs(x);
        y = Math. abs(y);
        int max = 0, min = 0;
        if(x > y){
            max = x;
            min = y;
        }
        else{
            max = y;
            min = x;
        }
        double result = (1007.0 / 1024.0) * max + (441.0 / 1024.0) * min;
        return result;
    }

}
