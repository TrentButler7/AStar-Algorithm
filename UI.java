import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Contains logic to display a UI that visualises
 * the outcome of an A-Star operation.
 * 
 * @author Carl Stephens (1505521)
 * @author Trent Butler (1522993)
 */
public class UI extends Frame {
    /**
     * Gets the size of the galaxy.
     */
    private DPoint _galaxyDimension;

    /**
     * Gets the amount by which each star should be shifted
     * when painting, in order to fit on a (0, 0) rooted canvas
     */
    private DPoint _galaxyOffset;

    public UI(List<DPoint> starList, Path path, int maxD) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                dispose();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });

        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenDimension);
        setBackground(new Color(0x1b, 0x1b, 0x1e));

        add(new MyCanvas(starList, path, maxD));
        calculateGalaxyDimensions(starList);
        setTitle("Pathfind Visualisation");
        setVisible(true);
    }

    private void calculateGalaxyDimensions(List<DPoint> starList) {
        if (starList.size() <= 0) {
            return;
        }

        DPoint initial = starList.get(0);
        double left = initial.x;
        double top = initial.y;
        double width = initial.x;
        double height = initial.y;

        // Find the outermost bounds of the star positions
        for (DPoint p : starList) {
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

        _galaxyDimension = new DPoint(width, height);
        _galaxyOffset = new DPoint(left, top);
    }

    private class MyCanvas extends Canvas {
        private static final int GALAXY_BORDER = 30;

        private final List<DPoint> _starPoints;
        private final Path _path;
        private final int _maxTravelDistance;

        private double _scaleFactor;

        public MyCanvas(List<DPoint> starPoints, Path path, int maxTravelDistance) {
            _starPoints = starPoints;
            _path = path;
            _maxTravelDistance = maxTravelDistance;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            Dimension d = this.getSize();

            double scaleX = (d.getWidth() - GALAXY_BORDER * 2) / _galaxyDimension.getX();
            double scaleY = (d.getHeight() - GALAXY_BORDER * 2) / _galaxyDimension.getY();
            _scaleFactor = scaleY;
            if (scaleX < scaleY) {
                _scaleFactor = scaleX;
            }

            List<DPoint> pathPoints = _path.getPoints();
            if (pathPoints.size() == 0) {
                return;
            }

            drawPath(g2);

            // Draw each star
            for (DPoint star : _starPoints) {
                drawStar(g2, star, Color.RED, 10);
            }
            
            drawMaxTravelDistance(g2);
            // Draw the start and goal stars
            drawEndpointStars(g2);

            // Draw markers to explicitly show the stars that the path is moving through
            g2.setColor(Color.WHITE);
            for (int i = 1; i < pathPoints.size() - 1; i++) {
                drawStar(g2, pathPoints.get(i), Color.WHITE, 4);
            }

            drawScale(g2);
        }

        private void drawPath(Graphics2D g2) {
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.WHITE);
            DPoint lastPathPoint = null;
            for (DPoint p : _path.getPoints()) {
                if (lastPathPoint == null) {
                    lastPathPoint = p;
                    continue;
                }

                g2.drawLine(
                    getStarX(lastPathPoint),
                    getStarY(lastPathPoint),
                    getStarX(p),
                    getStarY(p)
                );
                lastPathPoint = p;
            }
        }

        private void drawEndpointStars(Graphics2D g2) {
            DPoint start = _path.getPoints().get(0);
            drawStar(g2, start, Color.CYAN, 20);
            drawStar(g2, _path.getGoal(), Color.CYAN, 20);

            Font font = new Font(g2.getFont().getName(), Font.BOLD, 20);
            g2.setFont(font);
            g2.setColor(Color.BLACK);

            g2.drawString(
                "S",
                getStarX(start) - 6,
                getStarY(start) + 8
            );

            g2.drawString(
                "G",
                getStarX(_path.getGoal()) - 8,
                getStarY(_path.getGoal()) + 8
            );
        }

        private void drawMaxTravelDistance(Graphics2D g2) {
            DPoint start = _path.getPoints().get(0);
            int mdx = (int)(_maxTravelDistance * _scaleFactor);
            int mdy = (int)(_maxTravelDistance * _scaleFactor);

            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.GREEN);
            g2.drawOval(getStarX(start) - mdx, getStarY(start) - mdy, mdx * 2, mdy * 2);
        }

        private void drawScale(Graphics2D g2) {
            int xCount = (int)Math.ceil(_galaxyDimension.x / 10) + 1;
            int yCount = (int)Math.ceil(_galaxyDimension.y / 10) + 1;

            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.WHITE);

            for (int x = 0; x < xCount; x++) {
                int xPos = (int)(x * 10 * _scaleFactor) + GALAXY_BORDER;
                g2.drawLine(xPos, getSize().height, xPos, getSize().height - 15);

                String label = ((Integer)(x * 10)).toString();
                g2.drawString(label, xPos + 4, getSize().height);
            }

            for (int y = 0; y < yCount; y++) {
                int yPos = getSize().height - (int)(y * 10 * _scaleFactor) - GALAXY_BORDER;
                g2.drawLine(0, yPos, 15, yPos);

                String label = ((Integer)(y * 10)).toString();
                g2.drawString(label, 1, yPos - 4);
            }
        }

        private void drawStar(Graphics2D g2, DPoint star, Color color, int diameter) {
            int x = getStarX(star) - diameter / 2;
            int y = getStarY(star) - diameter / 2;

            g2.setStroke(new BasicStroke(diameter / 10));
            g2.setColor(Color.WHITE);
            g2.drawOval(x, y, diameter, diameter);

            g2.setColor(color);
            g2.fillOval(x, y, diameter, diameter);
        }

        private int getStarX(DPoint star) {
            return ((int)((star.x - _galaxyOffset.x) * _scaleFactor)) + GALAXY_BORDER;
        }

        private int getStarY(DPoint star) {
            int canvasHeight = getSize().height;
            return (canvasHeight - (int)((star.y - _galaxyOffset.y) * _scaleFactor)) - GALAXY_BORDER;
        }
    }
}
