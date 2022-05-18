import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
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
            return;
        }

        new UI(stars._allPoints, finalPath, maxTravelDistance * 100);
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
        _maxD = maxTravelDistance * 100;

        TreeMap<Double, Path> pathMap = initializePath(_allPoints, startIndex);
        while (pathMap.size() > 0) {
            Path currentPath = pathMap.firstEntry().getValue();
            if(currentPath.isAtGoal()) {
                return currentPath;
            }

            // We haven't reached the goal yet, so expand the frontier
            pathMap = expand(pathMap);
        }

        // The pathmap emptied without the goal being reached
        return null;
    }

    /**
     * Expands the search of the graph
     * @param pathMap The map of paths to be expanded
     * @return The expanded pathMap
     */
    private TreeMap<Double, Path> expand(TreeMap<Double, Path> pathMap) {
        // Dequeue the path to investigate
        Path currentPath = pathMap.firstEntry().getValue();
        pathMap.remove(currentPath.getFValue());

        for (Point newPoint: _allPoints) {
            // Ensure that the point isn't already in the path.
            // Prevents us from creating a looping path
            if (currentPath.getPoints().contains(newPoint)) {
                continue;
            }

            // Check that this point is within range of the edge of the path
            if (Util.getEuclidean(currentPath.getLastPoint(), newPoint) > _maxD) {
                continue;
            }

            // At this point we can investigate a new valid path
            Path newPath = (Path)currentPath.clone();
            newPath.add(newPoint);

            for (Path element : pathMap.values()) {
                // Attempt to find an existing path that leads to the new point
                if (!element.getLastPoint().equals(newPoint)) {
                    continue;
                }

                // Is our new path better than the old path?
                if (element.getFValue() < newPath.getFValue()) {
                    break;
                }

                // Replace with the more efficient path
                pathMap.put(newPath.getFValue(), newPath);
                pathMap.remove(element.getFValue());
                break;
            }

            pathMap.put(newPath.getFValue(), newPath);
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
        pathMap.put(firstPath.getFValue(), firstPath);
        return pathMap;
    }
}
