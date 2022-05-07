import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Stars
 */
public class Stars {

    public static void main(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Usage: java Stars <galaxy_csv_filename> <start_index> <end_index> <D>");
        }
        Stars stars = new Stars();
        stars.search(stars.readFile(args[0]));
    }

    private ArrayList<Point> readFile(String filename) {
        ArrayList<Point> points = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                points.add(new Point(Integer.parseInt(values[0]),Integer.parseInt(values[1])));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return points;
    }

    private void search(ArrayList<Point> points) {
        
    }
}