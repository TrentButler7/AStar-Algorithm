import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class UI extends Frame {
    private double _xScaleFactor = 1;
    private double _yScaleFactor = 1;
    private double _canvasHeight;
    private double _canvasWidth;
    private int _xOffset;
    private int _yOffset;

    public UI(List<Point> starList, Path path, int maxD) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                dispose();
            }
        });

        add(new MyCanvas(starList));

        setWindowSize(starList);
        setTitle("Pathfind Visualisation");
        setVisible(true);
    }

    private void setWindowSize(List<Point> starList) {
        if (starList.size() <= 0) {
            return;
        }

        Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        _canvasWidth = screenDimensions.getWidth() - 16;
        _canvasHeight = screenDimensions.getHeight() - 64;

        Point initial = starList.get(0);

        int left = initial.x;
        int top = initial.y;
        int width = initial.x;
        int height = initial.y;

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

        // Correct the coordinates to begin at (0, 0)
        width -= left;
        height -= top;
        _xOffset = left;
        _yOffset = top;

        System.out.println(width);

        if (width > _canvasWidth) {
            _xScaleFactor = _canvasWidth / width;
        }
        if (height > _canvasHeight) {
            _yScaleFactor = _canvasHeight / height;
        }

        width *= _xScaleFactor;
        height *= _yScaleFactor;
        setSize(width, height);
    }

    private class MyCanvas extends Canvas {
        private final List<Point> _starPoints;

        public MyCanvas(List<Point> starPoints) {
            _starPoints = starPoints;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;

            for (Point star : _starPoints) {
                int x = (int)((star.x - _xOffset) * _xScaleFactor) + 5;
                int y = (int)_canvasHeight - (int)((star.y - _yOffset) * _yScaleFactor) + 5;

                g2.setColor(Color.BLACK);
                g2.drawOval(x - 5, y - 5, 10, 10);

                g2.setColor(Color.RED);
                g2.fillOval(x - 5, y - 5, 10, 10);
            }
        }
    }
}
