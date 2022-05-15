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

    private Path search(ArrayList<Point> allPoints, int startIndex) {
        TreeMap<Double, Path> pathMap = initializePath(allPoints, startIndex);
        while (pathMap.size() > 0) {
            Path currentPath = pathMap.firstEntry().getValue();
            if(checkPath(currentPath)){
                return currentPath;
            }
            expand(pathMap);
        }

        return null;
    }

    private Boolean checkPath(Path path){
        ArrayList<Point> pathList = path.getList();
        if (pathList.get(pathList.size() - 1) == goal) {
            return true;
        }
        else return false;
    }

    private TreeMap<Double, Path> expand(TreeMap<Double, Path> pathMap){
        return null;
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
        Path firstPath = new Path(points.get(startIndex), goal);
        pathMap.put(firstPath.getfValue(), firstPath);
        return pathMap;
    }
}
