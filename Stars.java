import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stars
 */
public class Stars {

    private Point _goal;
    static private ArrayList<Point> _allPoints;
    static private int _maxD = 0;
    
    public static void main(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Usage: java Stars <galaxy_csv_filename> <start_index> <end_index> <D>");
        }
        Stars stars = new Stars();
        _allPoints = stars.readFile(args[0]);
        _maxD = Integer.parseInt(args[3]);
        stars.search(_allPoints, Integer.parseInt(args[1]));
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
        _goal = allPoints.get(allPoints.size() - 1);

        return allPoints;
    }

    /**
     * Starts the search for the goal using the A* algorithm
     * @param allPoints A list of all of the possible points on the graph
     * @param startIndex THe index of the starting point
     * @return The value of the optimal path if one is found
     */
    private Path search(ArrayList<Point> allPoints, int startIndex) {
        TreeMap<Double, Path> pathMap = initializePath(allPoints, startIndex);
        while (pathMap.size() > 0) {
            Path currentPath = pathMap.firstEntry().getValue();
            if(checkPath(currentPath)){
                return currentPath; //if the Path has reached the goal, return the path
            }
            pathMap = expand(pathMap); //otherwise, expand the search
        }

        return null;
    }

    /**
     * Checks if a path has reached the goal
     * @param path The path to be checked
     * @return If the Path did indeed contain a goal
     */
    private Boolean checkPath(Path path){
        ArrayList<Point> pathList = path.getList();
        if (pathList.get(pathList.size() - 1) == _goal) {
            return true;
        }
        else return false;
    }

    /**
     * Expands the search of the graph
     * @param pathMap The map of paths to be expanded
     * @return The expanded pathMap
     */
    private TreeMap<Double, Path> expand(TreeMap<Double, Path> pathMap){
        Path currentPath = pathMap.firstEntry().getValue();
        for (int i = 0; i < _allPoints.size(); i++) {
            Point newPoint = _allPoints.get(i);
            //Checks if the current path includes the point, if so move on since we dont want to get into a loop
            if (currentPath.getList().contains(newPoint)) {
                break;
            }
            //Checks if the point is within the Max distance
            else if (Util.getEuclidean(currentPath.getList().get(currentPath.getList().size() - 1), newPoint) > _maxD) {
                break;
            }

            Path newPath = null;

            //Adds the new point to a path
            try {
                newPath = (Path)currentPath.clone();
                newPath.add(newPoint);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            
            //Checks through every path to see if we already have a path leading to this point
            for (Map.Entry<Double, Path> mapPath : pathMap.entrySet()) {
                ArrayList<Point> pathList = mapPath.getValue().getList();
                if(pathList.get(pathList.size() - 1).equals(newPoint)){
                    if (newPath.getfValue() < mapPath.getValue().getfValue()) { //If the new path is better, add it to the map and remove the old path
                        pathMap.put(newPath.getfValue(), newPath);
                        pathMap.remove(mapPath.getKey());
                        break;
                    }
                    else break; //If the old path is better, move on without adding the new path
                }
            }
        }

        return pathMap;
    }

    /**
     * Initializes the first path, containing only the start point
     * @param points A list off all the points on the graph
     * @param startIndex The index of the starting point
     * @return A map to be used to contain all the paths and keep them in sorted order
     */
    private TreeMap<Double, Path> initializePath(ArrayList<Point> points, int startIndex){
        TreeMap<Double, Path> pathMap = new TreeMap<>();
        _goal = points.get(points.size() - 1);
        Path firstPath = new Path(points.get(startIndex), _goal);
        pathMap.put(firstPath.getfValue(), firstPath);
        return pathMap;
    }
}
