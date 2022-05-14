import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Stars
 */
public class Stars {

    static public Point goal;
    public static void main(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Usage: java Stars <galaxy_csv_filename> <start_index> <end_index> <D>");
        }
        Stars stars = new Stars();
        stars.search(stars.readFile(args[0]), Integer.parseInt(args[1]));
    }

    /**
     * Reads in the points from a CSV file
     * @param filename The name of the file containing the points
     * @return A list off all the points in the file
     */
    private ArrayList<Point> readFile(String filename) {
        ArrayList<Point> allPoints = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                allPoints.add(new Point(Integer.parseInt(values[0]),Integer.parseInt(values[1]))); 
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        goal = allPoints.get(allPoints.size() - 1);

        return allPoints;
    }

    private void search(ArrayList<Point> allPoints, int startIndex) {
        TreeMap<Double, Path> pathMap = initializePath(allPoints, startIndex);
        //TODO: actually search
    }

    /**
     * Initializes the first path, containing only the start point
     * @param points A list off all the points on the graph
     * @param startIndex The index of the starting point
     * @return A map to be used to contain all the paths and keep them in sorted order
     */
    private TreeMap<Double, Path> initializePath(ArrayList<Point> points, int startIndex){
        TreeMap<Double, Path> pathMap = new TreeMap<>();
        goal = points.get(points.size() - 1);
        Path firstPath = new Path(points.get(startIndex));
        pathMap.put(firstPath.getfValue(), firstPath);
        return pathMap;
    }

    public class Path implements Comparable<Path> {
        /**
         * The points contaied in this path
         */
        private ArrayList<Point> _points; 
        private double _heuristic;
        private double _cost;

        /**
         * Creates a new path with a single point
         * @param first The starting point of the ship
         */
        public Path(Point first){
            _points = new ArrayList<>();
            _points.add(first);
            _heuristic = getEuclidean(first, goal);
            _cost = 0; //added only the start points, have not travelled
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
            _heuristic = getEuclidean(_points.get(_points.size() - 1), goal);
            _cost = getEuclidean(_points.get(_points.size() - 1) , _points.get(_points.size() - 2)); 
        }

        /**
         * Gets the the list of points in the path
         * @return The ArrayList of points in the current path
         */
        public ArrayList<Point> getList() {
            return _points;
        }

        /**
         * Fast Euclidean estimate calculation, using https://www.flipcode.com/archives/Fast_Approximate_Distance_Functions.shtml
         * @param point1 The first point
         * @param point2 The second point
         * @return The estimate of the Euclidean distance
         */
        public int getEuclidean(Point point1, Point point2) {
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
            return (1007 / 1024) * max + (441/1024) * min;
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
            _heuristic = getEuclidean(point, goal);
            _cost += getEuclidean(point, _points.get(_points.size() - 2)); //-2 since we want the latest point and the previous point since that was the last distance travelled
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
}