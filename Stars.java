import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Stars
 */
public class Stars {

    public Point goal;
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
        goal = points.get(points.size() - 1);

        return points;
    }

    private void search(ArrayList<Point> points) {
        
    }

    public class Path {
        private ArrayList<Point> _points;
        private double _heuristic;

        public Path(Point first){
            _points = new ArrayList<>();
            _points.add(first);
            _heuristic = getHeuristic(first);
        }

        public Path(Path path) {
            _points = path.getList();
            _heuristic = getHeuristic(_points.get(_points.size() - 1));
        }

        public ArrayList<Point> getList() {
            return _points;
        }

        public double getHeuristic(Point currentPoint) {
            return Math.sqrt((currentPoint.getY() - goal.getY()) * (currentPoint.getY() - goal.getY()) + (currentPoint.getX() - goal.getX()) * (currentPoint.getX() - goal.getX()));
        }

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

        public void setList(ArrayList<Point> list) {
            _points = list;
        }

        public void add(Point point){
            _points.add(point);
        }
    }
}