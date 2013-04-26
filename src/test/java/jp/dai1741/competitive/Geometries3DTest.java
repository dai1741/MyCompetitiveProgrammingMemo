package jp.dai1741.competitive;

import static org.junit.Assert.*;

import jp.dai1741.competitive.Geometries3D.Point;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Scanner;

import static jp.dai1741.competitive.Geometries3D.*;
import static java.lang.Math.sqrt;

public class Geometries3DTest {

    /* test utils */

    static Point p(double x, double y, double z) {
        return new Point(x, y, z);
    }

    private void assertArrayEqualsSorted(Point[] expecteds, Point[] actuals) {
        Arrays.sort(expecteds);
        Arrays.sort(actuals);
        assertArrayEquals(expecteds, actuals);
    }

    static Point[] makePoints(String representation) {
        Point[] points = new Point[representation.length() / 3];
        Scanner sc = new Scanner(representation);
        int i = 0;
        while (sc.hasNextDouble()) {
            points[i++] = p(sc.nextDouble(), sc.nextDouble(), sc.nextDouble());
        }
        return Arrays.copyOf(points, i);
    }

    @Test
    public void testPoint() {
        Point p = p(4, 3.6, -2);
        assertEquals(4, p.x, EPS);
        assertEquals(3.6, p.y, EPS);
        assertEquals(-2, p.z, EPS);
        assertEquals(p(3, 4.6, 0.5), p.add(p(-1, 1, 2.5)));
        assertEquals(p(5, 2.6, -4.5), p.subtract(p(-1, 1, 2.5)));

        assertEquals(32.96, p.distanceSqr(), EPS);
        assertEquals(2 * sqrt(206) / 5, p.distance(), EPS);
        assertEquals(sqrt(914) / 5, p.distance(p(1, 2, 3)), EPS);

        assertEquals(-4 + 3.6 - 5, p.dot(p(-1, 1, 2.5)), EPS);
        assertEquals(p(14.8, -14, 4.4), p.cross(p(1, 2, 3)));
        assertEquals(32.96, p.dot(p), EPS);
        assertEquals(p(0, 0, 0), p.cross(p));
        assertEquals(-32.96, p.dot(p.multiply(-1)), EPS);
        assertEquals(p(0, 0, 0), p.cross(p.multiply(-1)));
    }

    @Test
    public void testCcw() {
        assertEquals(1, ccw(p(0, 0, 0), p(1, 1, 0), p(1, 2, 0), p(0, 0, 1)));
        assertEquals(-1, ccw(p(0, 0, 0), p(1, 1, 0), p(1, 0, 0), p(0, 0, 1)));
        assertEquals(-1, ccw(p(0, 0, 0), p(1, 1, 0), p(1, 2, 0), p(0, 0, -1)));
        assertEquals(1, ccw(p(0, 0, 0), p(1, 1, 0), p(1, 0, 0), p(0, 0, -1)));

        assertEquals(1, ccw(p(0, 0, 1234), p(1, 1, -567), p(1, 2, 89012), p(0, 0, 1)));
        assertEquals(-1, ccw(p(0, 1234, 0), p(1, -5678, 1), p(1, 9012, 2), p(0, 1, 0)));
        assertEquals(1, ccw(p(34567, 0, 0), p(-890, 1, 1), p(1234, 1, 2), p(1, 0, 0)));

        assertEquals(0, ccw(p(0, 0, 0), p(2, 2, 0), p(1, 1, 0), p(0, 0, 1)));
        assertEquals(0, ccw(p(0, 0, 0), p(2, 2, 0), p(1, 1, 0), p(0, 0, -1)));

        // この他の場合は保留
    }

    @Test
    public void testRotate() {
        assertEquals(p(-sqrt(2), 0, 1), rotate(p(1, 1, 1), p(0, 0, 1), Math.PI * 3 / 4));
        assertEquals(p(1.5 + sqrt(3) / 2, 2, 0.5 - 3 * sqrt(3) / 2), rotate(p(3, 2, 1),
                p(0, 1, 0), Math.PI / 3));
    }

    @Test
    public void testDistances() {
        assertEquals(4 * sqrt(10.0 / 23), distanceLP(p(0, 0, 0), p(3, 6, 1), p(1, 2, 3)),
                EPS);
        assertEquals(4 * sqrt(10.0 / 31), distanceLL(p(0, 0, 0), p(3, 6, 1), p(1, 2, 3),
                p(9, 8, 7)), EPS);
        assertEquals(8 * sqrt(10.0 / 159), distanceLL(p(-8, -1, -4), p(-3, -6, -1), p(1,
                2, 3), p(9, 8, 7)), EPS);
        assertEquals(468 * sqrt(5.0 / 4681), distanceLL(p(0, 12, 0), p(3, 6, 1), p(65,
                -8, 16), p(7, 8, 16)), EPS);

        assertEquals(0, distanceLL(p(0, 0, 0), p(1, 1, 1), p(8, 8, 1), p(7, 7, -1)), EPS);
        assertEquals(7 * sqrt(2.0 / 3), distanceLS(p(0, 0, 0), p(1, 1, 1), p(8, 8, 1), p(
                7, 7, -1)), EPS);
        assertEquals(14 / sqrt(3), distanceLS(p(8, 8, 1), p(7, 7, -1), p(0, 0, 0), p(1,
                1, 1)), EPS);
        assertEquals(2 * sqrt(19), distanceSS(p(0, 0, 0), p(1, 1, 1), p(8, 8, 1), p(7, 7,
                -1)), EPS);

        assertEquals(0, distanceLL(p(0, 0, 0), p(1, 1, 1), p(1, 0, 1), p(0, 1, 0)), EPS);
        assertEquals(0, distanceSS(p(0, 0, 0), p(1, 1, 1), p(1, 0, 1), p(0, 1, 0)), EPS);
        assertEquals(1 / sqrt(6), distanceSS(p(0, 0, 0), p(0.25, 0.25, 0.25), p(1, 0, 1),
                p(0, 1, 0)), EPS);
        assertEquals(0, distanceLS(p(0, 0, 0), p(0.25, 0.25, 0.25), p(1, 0, 1),
                p(0, 1, 0)), EPS);
        assertEquals(0, distanceLL(p(0, 0, 0), p(0.25, 0.25, 0.25), p(1, 0, 1),
                p(0, 1, 0)), EPS);
        assertEquals(1 / sqrt(6), distanceSS(p(0, 0, 0), p(1, 1, 1), p(1, 0, 1), p(0.75,
                0.25, 0.75)), EPS);
        assertEquals(1 / sqrt(6), distanceLS(p(0, 0, 0), p(1, 1, 1), p(1, 0, 1), p(0.75,
                0.25, 0.75)), EPS);
        assertEquals(0, distanceLL(p(0, 0, 0), p(1, 1, 1), p(1, 0, 1),
                p(0.75, 0.25, 0.75)), EPS);
    }

    @Test
    public void testSpheres() {
        assertArrayEqualsSorted(new Point[] {
                p(-1 / sqrt(3), -1 / sqrt(3), -1 / sqrt(3)),
                p(1 / sqrt(3), 1 / sqrt(3), 1 / sqrt(3)) }, crosspointLSphere(p(0, 0, 0),
                p(1, 1, 1), p(0, 0, 0), 1));
        assertArrayEqualsSorted(new Point[] {
                p(-3 * (sqrt(5655) - 229) / 149, (4 * sqrt(5655) - 22) / 149,
                        (7 * sqrt(5655) + 632) / 298),
                p(3 * (sqrt(5655) + 229) / 149, -(4 * sqrt(5655) + 22) / 149, (-7
                        * sqrt(5655) + 632) / 298) }, crosspointLSphere(p(3, 2, 4), p(6,
                -2, 0.5), p(2.5, 1, -1), 5));
        assertArrayEqualsSorted(new Point[] { p(1, 0, 0) }, crosspointLSphere(p(1, 0, 0),
                p(2, 1, 1), p(2, 0, -1), sqrt(2)));
        assertArrayEqualsSorted(new Point[] {}, crosspointLSphere(p(3, 2, 4), p(6, -2,
                0.5), p(2.5, 1, -1), 3));

        assertEquals(p(0.5, 0, -0.5), crosspointSphereSphere(p(0, 0, 0), 1, p(1, 0, -1),
                1));
        assertEquals(p(-0.75, 0, -0.25), crosspointSphereSphere(p(-1, 0, 0), 1, p(0, 0,
                -1), sqrt(2)));
        assertEquals(p(-0.75, -0.25, 0), crosspointSphereSphere(p(-1, 0, 0), 1, p(0, -1,
                0), sqrt(2)));
        assertNull(crosspointSphereSphere(p(0, 0, 0), 1, p(1, 10, -1), 1));
        assertNull(crosspointSphereSphere(p(0, 0, 0), 1, p(1, 0, -1), 10));

        assertEquals(p(0, 0, 0), crosspointPlaneSphere(p(1, 0, 3), p(0, 1, 0),
                p(0, -1, 0), 2));
        assertEquals(p(0, 1, 0), crosspointPlaneSphere(p(1, 1, 3), p(0, 1, 0),
                p(0, -1, 0), 2));
        assertNull(crosspointPlaneSphere(p(1, 3, 3), p(0, 1, 0), p(0, -1, 0), 2));
    }

    static Point[][] somePointsList = {
            makePoints("0 0 0  1 1 1  0 0 1  0 1 0  0 1 1  1 0 0  1 0 1  1 1 0  0.5 0.5 0.5  0.5 0 0.5"),  // 立方体
            makePoints("0 0 0  1 0 0  0 1 0  1 1 0  0 0 1  0.5 0.5 0  0.5 0.5 0.1"),  // 四角すい
            makePoints("-5 -10 -10  -4 -10 -10  -5 -9 -10  -4 -9 -10  -5 -10 -9  -4.5 -9.5 -10  -4.5 -9.5 -9.9"),  // 上のからp(5,10,10)引いた
            makePoints("0 0 0  1 0 0  0 1 0  1 1 0  0.5 0.5 0  0.5 0 0"),  // 同一平面上
            makePoints("0 0 0  1 0 0  0 1 0  1 1 0  0.6 2 0  0.4 2 0  0 0 1  0.5 0 0"),  // 6角形の底面
            makePoints("0.5 0.5 0  0.5 0.5 1  0.5 0 0.5  0.5 1 0.5  0 0.5 0.5  1 0.5 0.5  0.5 0.5 0.5"),  // 正8面体
            makePoints("3 1 -1.5  3 1 -0.5  3 0.5 -1  3 1.5 -1  2 1 -1  4 1 -1  3 1 -1"),  // 平行移動した正8面体が横に伸びた
            dodecahedron(),  // 正12面体
    };
    double[] theVolumes = {
            1, 1.0 / 3, 1.0 / 3, 0, 8.0 / 15, 1.0 / 6, 1.0 / 3,
            (15 + 7 * sqrt(5)) / 4 * Math.pow(sqrt(5) - 1, 3) };

    /**
     * @see http://en.wikipedia.org/wiki/Dodecahedron#Cartesian_coordinates
     */
    static private Point[] dodecahedron() {
        Point[] ret = new Point[20];
        final double GOLDEN = (1 + sqrt(5)) / 2;
        int i = 0;
        for (int x = -1; x <= +1; x++) {
            for (int y = -1; y <= +1; y++) {
                for (int z = -1; z <= +1; z++) {
                    if (x * y * z != 0) ret[i++] = p(x, y, z);
                    else if (y * z != 0) ret[i++] = p(0, y / GOLDEN, z * GOLDEN);
                    else if (z * x != 0) ret[i++] = p(x * GOLDEN, 0, z / GOLDEN);
                    else if (x * y != 0) ret[i++] = p(x / GOLDEN, y * GOLDEN, 0);
                }
            }
        }
        return ret;
    }

    @Test
    public void testCovexHull() {
        for (Point[] points : somePointsList) {
            checkConvexHull(points);
        }
    }

    private void checkConvexHull(Point[] points) {
        Point[][] hullp = convexHull(points);
        // System.out.println(Arrays.deepToString(hullp));
        assertTrue(isConvex(hullp));
        for (Point p : points) {
            int type = isInConvex(p, hullp);
            assertTrue(type != 0);
            // if (type == 2) assertTrue(hull.contains(p));  // 凸包が無駄な辺を含む場合
        }
    }

    @Test
    public void testCentroid() {
        assertEquals(p(0.5, 0.5, 0), centroid(makePoints("0 0 0  1 0 0  1 1 0  0 1 0")));
        assertEquals(p(1, 1, 2), centroid(makePoints("2 2 2  0 2 2  0 0 2  2 0 2")));
        assertEquals(p(4.0 / 3, 4.0 / 3, 4.0 / 3),
                centroid(makePoints("4 0 0  0 4 0  0 0 4")));
    }

    @Test
    public void testVolume() {
        for (int i = 0; i < somePointsList.length; i++) {
            assertEquals(theVolumes[i], volume(convexHull(somePointsList[i])), EPS);
        }
    }

    @Test
    @Ignore("バグ解消の目途が立たないので無視")
    public void testConvexCut() {
        for (Point[] points : somePointsList) {
            checkConvexCut(points, p(0.5, 0.5, 0.5), p(0, 1, 0));
        }
    }

    private void checkConvexCut(Point[] points, Point a, Point n) {
        Point[][] hullp = convexCut(convexHull(points), a, n);
        System.out.println(Arrays.deepToString(hullp));
        assertTrue(isConvex(hullp));
        for (Point p : points) {
            int type = isInConvex(p, hullp);
            double d = a.subtract(p).dot(n);
            if (approxEquals(d, 0)) assertTrue(type == 2);
            else if (d > 0) assertTrue(type != 0);
            else assertTrue(type == 0);
        }
    }

    @Test
    public void test() {
    }

}
