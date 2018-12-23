package net.mosur.raycasting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Polygon {
    private List<Point> points;
    private List<Side> sides;

    private Comparator<Point> yAxisComparator = Comparator.comparingDouble(Point::getY);

    private final double EPSILON = 0.001;

    public Polygon(List<Point> points) {
        this.points = points;
        initSides();
    }

    public Polygon(Point... p) {
        this.points = new ArrayList<>();
        Collections.addAll(points, p);
        initSides();
    }

    private void initSides() {
        sides = new ArrayList<>();
        for (int i = 1; i < points.size(); i++) {
            sides.add(new Side(points.get(i - 1), points.get(i)));
        }
        sides.add(new Side(points.get(points.size() - 1), points.get(0)));
    }

    public boolean contains(Point p) {
        int count = 0;
        for (Side side : sides) {
            if (intersectSegment(p, side)) {
                count++;
            }
        }
        return count % 2 != 0;
    }

    private boolean intersectSegment(Point p, Side side) {
        Point lowest = getLowestPoint(side);
        Point highest = getHighestPoint(side);
        p = checkIfYEqual(p, lowest, highest);

        if (checkIsBelowOrAbove(p, lowest, highest)) {
            return false;
        }
        if (isAfter(p, lowest, highest)) {
            return false;
        }
        if (isBefore(p, lowest, highest)) {
            return true;
        } else {
            double redMark;
            if (lowest.getX() != highest.getX()) {
                redMark = (highest.getY() - lowest.getY()) / (highest.getX() - lowest.getX());
            } else {
                redMark = Double.NEGATIVE_INFINITY;
            }
            double blueMark;
            if (lowest.getX() != p.getX()) {
                blueMark = (p.getY() - lowest.getY()) / (p.getX() - lowest.getX());
            } else {
                blueMark = Double.POSITIVE_INFINITY;
            }
            return blueMark > redMark;
        }
    }

    private boolean isBefore(Point p, Point lowest, Point highest) {
        return p.getX() < lowest.getX() && p.getX() < highest.getX();

    }

    private boolean isAfter(Point p, Point lowest, Point highest) {
        return p.getX() > lowest.getX() && p.getX() > highest.getX();
    }

    private boolean checkIsBelowOrAbove(Point p, Point lowest, Point highest) {
        return p.getY() < lowest.getY() || p.getY() > highest.getY();
    }

    private Point checkIfYEqual(Point p, Point lowest, Point highest) {
        if (p.getY() == lowest.getY() || p.getY() == highest.getY()) {
            p.setY(p.getY() + EPSILON);
        }
        return p;
    }

    private Point getLowestPoint(Side side) {
        return yAxisComparator.compare(side.firstPoint, side.secondPoint) < 0 ? side.firstPoint : side.secondPoint;
    }

    private Point getHighestPoint(Side side) {
        return yAxisComparator.compare(side.firstPoint, side.secondPoint) >= 0 ? side.firstPoint : side.secondPoint;
    }
}
