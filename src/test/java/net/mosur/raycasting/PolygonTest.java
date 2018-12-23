package net.mosur.raycasting;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PolygonTest {

    Polygon square;

    @Before
    public void setUp() {
        Point point1 = new Point(0, 0);
        Point point2 = new Point(1, 0);
        Point point3 = new Point(1, 1);
        Point point4 = new Point(0, 1);

        square = new Polygon(point1, point2, point3, point4);
    }

    @Test
    public void should_be_in_square_positive_coords() {
        Point inside = new Point(0.5, 0.5);

        assertTrue(square.contains(inside));
    }


    @Test
    public void should_be_in_quadrangle() {
        Point point1 = new Point(0, 0);
        Point point2 = new Point(5, 0);
        Point point3 = new Point(10, 10);
        Point point4 = new Point(-1, 3);
        Polygon square = new Polygon(point1, point2, point3, point4);
        Point inside = new Point(1.5, 1.5);
        assertTrue(square.contains(inside));
    }

    @Test
    public void should_not_be_in_quadrangle_point_below() {
        Point point1 = new Point(0, 0);
        Point point2 = new Point(5, 0);
        Point point3 = new Point(10, 10);
        Point point4 = new Point(-1, 3);
        Polygon square = new Polygon(point1, point2, point3, point4);
        Point inside = new Point(1, -0.001);
        assertFalse(square.contains(inside));
    }

    @Test
    public void should_be_in_quadrangle_point_on_side_fail() {
        Point point1 = new Point(0, 0);
        Point point2 = new Point(5, 0);
        Point point3 = new Point(10, 10);
        Point point4 = new Point(-1, 21);
        Polygon square = new Polygon(point1, point2, point3, point4);
        Point inside = new Point(9, 11);
        Point inside2 = new Point(6, 2);
        assertFalse(square.contains(inside));
        assertFalse(square.contains(inside2));
    }

    @Test
    public void should_be_in_square_differ_coords() {
        Point point1 = new Point(-1, 1);
        Point point2 = new Point(1, 1);
        Point point3 = new Point(1, -1);
        Point point4 = new Point(-1, -1);
        Polygon square = new Polygon(point1, point2, point3, point4);
        Point inside = new Point(0, 0);
        assertTrue(square.contains(inside));
    }

    @Test
    public void should_be_in_square_fail() {
        Point inside = new Point(1.5, 0.5);
        assertFalse(square.contains(inside));
    }


}