import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stars
 */
public class Stars {

    private final List<Point> _allPoints;

    private Point _goal;
    private int _maxD = 0;

    public Stars(List<Point> starPoints) {
        _allPoints = starPoints;
    }
    
    public static void main(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Usage: java Stars <galaxy_csv_filename> <start_index> <end_index> <D>");
        }

        Stars stars = Stars.CreateFromFile(args[0]);
        if (stars == null) {
            return;
        }

        int startIndex, endIndex, maxTravelDistance;
        try {
            startIndex = Integer.parseInt(args[1]);
            endIndex = Integer.parseInt(args[2]);
            maxTravelDistance = Integer.parseInt(args[3]);
        }
        catch (Exception ex) {
            System.err.println("Failed to parse one of either: start_index, end_index, D. Please subsitute these parameters with valid integers.");
            ex.printStackTrace();

            return;
        }

        Path finalPath = stars.search(startIndex, endIndex, maxTravelDistance);
        if (finalPath != null) {
            finalPath.print();
        }
        else {
            System.out.println("No Path found");
        }
    }

    /**
     * Reads a list of star points from the given CSV file
     * and creates a new instance of the Stars object.
     * @param fileName The path of the file containing the star points.
     * @return A new Stars instance, or null if the creation failed.
     */
    public static Stars CreateFromFile(String fileName) {
        ArrayList<Point> starPoints = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double d1 = Double.parseDouble(values[0]) * 100;
                double d2 = Double.parseDouble(values[1]) * 100;
                starPoints.add(new Point((int)d1,(int)d2));
            }

            return new Stars(starPoints);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Starts the search for the goal using the A* algorithm
     * @param startIndex THe index of the starting point
     * @return The value of the optimal path if one is found
     */
    private Path search(int startIndex, int goalIndex, int maxTravelDistance) {
        if (startIndex < 0) {
            throw new IllegalArgumentException("startIndex must not be negative.");
        }

        if (goalIndex < 0) {
            throw new IllegalArgumentException("goalIndex must not be negative.");
        }

        if (maxTravelDistance <= 0) {
            throw new IllegalArgumentException("maxTravelDistance must be greater than zero.");
        }

        _goal = _allPoints.get(goalIndex);
        _maxD = maxTravelDistance;

        TreeMap<Double, Path> pathMap = initializePath(_allPoints, startIndex);
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
        pathMap.remove(currentPath.getfValue());
        for (int i = 0; i < _allPoints.size(); i++) {
            Point newPoint = _allPoints.get(i);
            //Checks if the current path includes the point, if so move on since we dont want to get into a loop
            if (currentPath.getList().contains(newPoint)) {
                continue;
            }
            //Checks if the point is within the Max distance
            else if (Util.getEuclidean(currentPath.getList().get(currentPath.getList().size() - 1), newPoint) > _maxD) {
                continue;
            }

            Path newPath = null;

            //Adds the new point to a path
            newPath = (Path)currentPath.clone();
            newPath.add(newPoint);
            
            //Checks through every path to see if we already have a path leading to this point
            // for ( mapPath : pathMap.entrySet()) {
                
            // }

            Iterator<Map.Entry<Double, Path>> iterator = pathMap.entrySet().iterator();

            while(iterator.hasNext()){
                Map.Entry<Double, Path> entry = iterator.next();
                ArrayList<Point> pathList = entry.getValue().getList();
                if(pathList.get(pathList.size() - 1).equals(newPoint)){
                    if (newPath.getfValue() < entry.getValue().getfValue()) { //If the new path is better, add it to the map and remove the old path
                        pathMap.put(newPath.getfValue(), newPath);
                        pathMap.remove(entry.getKey());
                        break;
                    }
                    else break; //If the old path is better, move on without adding the new path
                }
            }

            pathMap.put(newPath.getfValue(), newPath);
        }

        return pathMap;
    }

    /**
     * Initializes the first path, containing only the start point
     * @param points A list off all the points on the graph
     * @param startIndex The index of the starting point
     * @return A map to be used to contain all the paths and keep them in sorted order
     */
    private TreeMap<Double, Path> initializePath(List<Point> points, int startIndex){
        TreeMap<Double, Path> pathMap = new TreeMap<>();
        Path firstPath = new Path(points.get(startIndex), _goal);
        pathMap.put(firstPath.getfValue(), firstPath);
        return pathMap;
    }
}
