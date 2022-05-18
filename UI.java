import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class UI extends Frame {
    /**
     * Gets the size of the galaxy.
     */
    private Dimension _galaxyDimension;

    /**
     * Gets the amount by which each star should be shifted
     * when painting, in order to fit on a (0, 0) rooted canvas
     */
    private Point _galaxyOffset;

    public UI(List<Point> starList, Path path, int maxD) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                dispose();
            }
        });

        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenDimension);

        add(new MyCanvas(starList, path, maxD));
        calculateGalaxyDimensions(starList);
        setTitle("Pathfind Visualisation");
        setVisible(true);
    }

    private void calculateGalaxyDimensions(List<Point> starList) {
        if (starList.size() <= 0) {
            return;
        }

        Point initial = starList.get(0);
        int left = initial.x;
        int top = initial.y;
        int width = initial.x;
        int height = initial.y;

        // Find the outermost bounds of the star positions
        for (Point p : starList) {
            if (p.x < left) {
                left = p.x;
            }
            else if (p.x > width) {
                width = p.x;
            }

            if (p.y < top) {
                top = p.y;
            }
            else if (p.y > height) {
                height = p.y;
            }
        }

        // If the top-left corner does not begin at (0, 0),
        // this corrects it so we get the right width/height
        width -= left;
        height -= top;

        _galaxyDimension = new Dimension(width, height);
        _galaxyOffset = new Point(left, top);
    }

    private class MyCanvas extends Canvas {
        private final List<Point> _starPoints;
        private final Path _path;
        private final int _maxTravelDistance;

        private double _scaleFactor;

        public MyCanvas(List<Point> starPoints, Path path, int maxTravelDistance) {
            _starPoints = starPoints;
            _path = path;
            _maxTravelDistance = maxTravelDistance;
        }

        @Override
        public Dimension getSize() {
            Dimension d = super.getSize();
            d.width -= 20;
            d.height -=20;

            return d;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            Dimension d = this.getSize();

            double scaleX = d.getWidth() / _galaxyDimension.getWidth();
            double scaleY = d.getHeight() / _galaxyDimension.getHeight();
            _scaleFactor = scaleY;
            if (scaleX < scaleY) {
                _scaleFactor = scaleX;
            }

            // Draw each star
            for (Point star : _starPoints) {
                drawStar(g2, star, Color.RED, 10);
            }

            List<Point> pathPoints = _path.getPoints();
            if (pathPoints.size() == 0) {
                return;
            }

            // Draw the start and goal stars
            Point start = pathPoints.get(0);
            drawStar(g2, start, Color.PINK, 20);
            drawStar(g2, _path.getGoal(), Color.CYAN, 20);

            // Draw the maximum travel distance
            int mdx = (int)(_maxTravelDistance * _scaleFactor);
            int mdy = (int)(_maxTravelDistance * _scaleFactor);
            g2.setStroke(new BasicStroke(5));
            g2.setColor(Color.GREEN);
            g2.drawOval(getStarX(start) - mdx / 2, getStarY(start) - mdy / 2, mdx, mdy);

            // Draw the path
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            Point lastPathPoint = null;
            for (Point p : pathPoints) {
                if (lastPathPoint == null) {
                    lastPathPoint = p;
                    continue;
                }

                g2.drawLine(getStarX(lastPathPoint), getStarY(lastPathPoint), getStarX(p), getStarY(p));
                lastPathPoint = p;
            }
        }

        private void drawStar(Graphics2D g2, Point star, Color color, int diameter) {
            int x = getStarX(star) - diameter / 2;
            int y = getStarY(star) - diameter / 2;

            g2.setStroke(new BasicStroke(diameter / 10));
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, diameter, diameter);

            g2.setColor(color);
            g2.fillOval(x, y, diameter, diameter);
        }

        private int getStarX(Point star) {
            return ((int)((star.x - _galaxyOffset.x) * _scaleFactor)) + 10;
        }

        private int getStarY(Point star) {
            int canvasHeight = getSize().height;
            return (canvasHeight - (int)((star.y - _galaxyOffset.y) * _scaleFactor)) + 10;
        }
    }
}
