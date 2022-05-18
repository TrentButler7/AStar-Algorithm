import java.awt.Point;
import java.util.ArrayList;

public class Path implements Comparable<Path>, Cloneable {
    /**
     * The points contained in this path
     */
    private ArrayList<Point> _points; 
    private double _heuristic;
    private double _cost;
    private Point _goal;

    /**
     * Creates a new path with a single point
     * @param first The starting point of the ship
     */
    public Path(Point first, Point goal){
        _points = new ArrayList<>();
        _points.add(first);
        _heuristic = Util.getEuclidean(first, goal);
        _cost = 0; //added only the start points, have not travelled
        _goal = goal;
    }

    /**
     * Gets the the list of points in the path
     * @return The ArrayList of points in the current path
     */
    public ArrayList<Point> getPoints() {
        return _points;
    }

    public Point getGoal() {
        return (Point)_goal.clone();
    }

    public Double getFValue() {
        return _cost + _heuristic;
    }

    /**
     * Gets the last (most recent) point in the path.
     * @return The last point to be added to the path.
     */
    public Point getLastPoint() {
        return _points.get(_points.size() - 1);
    }

    /**
     * Adds a new point to a path and calculates the new cost and heuristic
     * @param point
     */
    public void add(Point point) {
        _points.add(point);
        _heuristic = Util.getEuclidean(point, _goal);
        _cost += Util.getEuclidean(point, _points.get(_points.size() - 2)); //-2 since we want the latest point and the previous point since that was the last distance travelled
    }

    /**
     * Checks to see if the final node in the path is
     * equivalent to the goal, thereby indicating that
     * the path is complete.
     * @return True if the path has reached the goal, otherwise false.
     */
    public boolean isAtGoal() {
        return getLastPoint().equals(_goal);
    }

    /**
     * Clones this Path object, with a shallow copy of the point list.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object clone() {
        try {
            Path clone = (Path)super.clone();
            clone._points = (ArrayList<Point>)this.getPoints().clone();

            return clone;
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int compareTo(Path comparePath) {
        // Negate the comparison as smaller F-values are more desirable
        return 0 - this.getFValue().compareTo(comparePath.getFValue());
    }

    public void print() {
        for (Point point : _points) {
            double x = point.getX() / 100.0;
            double y = point.getY() / 100.0;
            System.out.println(x + "," + y);
        }
        _cost /= 100;
        System.out.println("C: " + _cost);
    }
}