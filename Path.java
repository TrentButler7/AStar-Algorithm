import java.awt.Point;
import java.util.ArrayList;

public class Path implements Comparable<Path> {
    /**
     * The points contaied in this path
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
     * Creates a new path, cloning another path
     * @param path The path to be copied
     */
    public Path(Path path) { //! might need to be changed to add new point in this method
        try {
            _points = ((Path)path.clone()).getList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _heuristic = Util.getEuclidean(_points.get(_points.size() - 1), _goal);
        _cost = Util.getEuclidean(_points.get(_points.size() - 1) , _points.get(_points.size() - 2)); 
    }

    /**
     * Gets the the list of points in the path
     * @return The ArrayList of points in the current path
     */
    public ArrayList<Point> getList() {
        return _points;
    }

    /**
     * Allows for the Path to be cloneed correctly including the list of the path
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Path clone = null;
        try
        {
            clone = (Path) super.clone();
            //Copy new List to cloned method
            clone.setList((ArrayList<Point>) this.getList().clone());
        } 
        catch (CloneNotSupportedException e) 
        {
            e.printStackTrace();
        }
        return clone;
    }

    /**
     * Sets the list of current path
     * @param list The list that the current one will match
     */
    private void setList(ArrayList<Point> list) {
        _points = list;
    }

    /**
     * Adds a new point to a path and calculates the new cost and heuristic
     * @param point
     */
    public void add(Point point){
        _points.add(point);
        _heuristic = Util.getEuclidean(point, _goal);
        _cost += Util.getEuclidean(point, _points.get(_points.size() - 2)); //-2 since we want the latest point and the previous point since that was the last distance travelled
    }

    public double getfValue() {
        return _cost + _heuristic;
    }

    @Override
    public int compareTo(Path comparePath) {
        if (this.getfValue() > comparePath.getfValue()) {
            return -1; //Returns -1 if the f value for this path is smaller than that of the given path (to give ascending order)
        }
        else if (this.getfValue() == comparePath.getfValue()){
            return 0;
        }
        else return 1; //Returns 1 if the f value for this path is larger than that of the given path (to give ascending order)
    }
}