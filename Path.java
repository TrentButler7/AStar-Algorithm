import java.util.ArrayList;

public class Path implements Comparable<Path>, Cloneable {
    /**
     * The points contained in this path
     */
    private ArrayList<DPoint> _points; 
    private double _heuristic;
    private double _cost;
    private DPoint _goal;

    /**
     * Creates a new path with a single point
     * @param first The starting point of the ship
     */
    public Path(DPoint first, DPoint goal){
        _points = new ArrayList<DPoint>();
        _points.add(first);
        _heuristic = first.fastDistance(goal);
        _cost = 0; //added only the start points, have not travelled
        _goal = goal;
    }

    /**
     * Gets the the list of points in the path
     * @return The ArrayList of points in the current path
     */
    public ArrayList<DPoint> getPoints() {
        return _points;
    }

    public DPoint getGoal() {
        return (DPoint)_goal.clone();
    }

    public Double getFValue() {
        return _cost + _heuristic;
    }

    /**
     * Gets the last (most recent) point in the path.
     * @return The last point to be added to the path.
     */
    public DPoint getLastPoint() {
        return _points.get(_points.size() - 1);
    }

    /**
     * Adds a new point to a path and calculates the new cost and heuristic
     * @param point
     */
    public void add(DPoint point) {
        _points.add(point);
        _heuristic = point.fastDistance(_goal);

        // -2 since we want the latest point and the previous point since that was the last distance travelled
        _cost += point.fastDistance(_points.get(_points.size() - 2));
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
            clone._points = (ArrayList<DPoint>)this.getPoints().clone();

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
        for (DPoint point : _points) {
            System.out.println(point.x + "," + point.y);
        }

        System.out.println("C: " + _cost);
    }
}