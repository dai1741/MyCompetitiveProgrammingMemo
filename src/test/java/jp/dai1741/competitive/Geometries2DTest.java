package jp.dai1741.competitive;

import static org.junit.Assert.*;

import jp.dai1741.competitive.Geometries2D;
import jp.dai1741.competitive.Geometries2D.Edge;
import jp.dai1741.competitive.Geometries2D.AdjGraph;
import jp.dai1741.competitive.Geometries2D.Point;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import static jp.dai1741.competitive.Geometries2D.*;
import static java.lang.Math.sqrt;


/**
 * Geometries2Dのテスト。物によってテストの強度がまちまちなので注意。
 * それ以前に読めたコードでないので注意。
 */
public class Geometries2DTest {

    Point zeroP = new Point();
    Point oneP = new Point(1, 1);
    Point[] aConvex = makePoints("-1 -1  7 -4  12 -1  10 4  6 5  4 5  -1 1");

    /* test utils */

    static Point p(double x, double y) {
        return new Point(x, y);
    }

    static Line l(Point a, Point b) {
        return new Line(a, b);
    }

    private void assertArrayEqualsSorted(Point[] expecteds, Point[] actuals) {
        Arrays.sort(actuals);
        assertArrayEquals(expecteds, actuals);
    }

    private void assertArrayEqualsSorted(int[] expecteds, int[] actuals) {
        Arrays.sort(actuals);
        assertArrayEquals(expecteds, actuals);
    }

    // デバッグ用
    static Point[] makePoints(String representation) {
        Point[] points = new Point[representation.length()];
        Scanner sc = new Scanner(representation);
        int i = 0;
        while (sc.hasNextDouble()) {
            points[i++] = p(sc.nextDouble(), sc.nextDouble());
        }
        return Arrays.copyOf(points, i);
    }

    @Test
    public void testPoint() {
        Point p = p(4, 6.6);
        assertEquals(4, p.x, EPS);
        assertEquals(6.6, p.y, EPS);
        assertEquals(p.add(p(-1, 3.7)), p(3, 10.3));

        Point p1 = p.subtract(p(-0.5, 1.2)).multiply(2.5);
        assertEquals(11.25, p1.x, EPS);
        assertEquals(13.5, p1.y, EPS);
        assertEquals(sqrt(59.56), p.distance(), EPS);
        assertEquals(59.56, p.distanceSqr(), EPS);

        assertEquals(-14.1978, p.dot(p(-3, -0.333)), EPS);
        assertEquals(0, p.dot(p(3.3, -2)), EPS);
        assertEquals(59.56, p.dot(p), EPS);

        assertEquals(18.468, p.cross(p(-3, -0.333)), EPS);
        assertEquals(0, p.cross(p(-5, -8.25)), EPS);
        assertEquals(0.4, p.cross(p(4, 6.7)), EPS);

        Point rnd1 = p(19.64037541, 36.59106884);
        Point rnd2 = p(-32.99432000, 34.21049412);
        assertEquals(1724.6506650993516137, rnd1.distanceSqr(), EPS);
        assertEquals(52.6885025114680797425, rnd1.subtract(rnd2).distance(), EPS);
        assertEquals(603.7777141976640208, rnd1.dot(rnd2), EPS);
        assertEquals(1879.2043819273863892, rnd1.cross(rnd2), EPS);
        assertEquals(-1879.2043819273863892, rnd2.cross(rnd1), EPS);
    }

    @Test
    public void testLineDirections() {
        Point p = p(46.529, 8.193);
        Point dir = p(58.2, -4.5);
        Point p2 = p.add(dir);

        assertEquals(1, ccw(p, p2, p(45, 9)));
        assertEquals(-1, ccw(p, p2, p(45, -9)));
        assertEquals(-2, ccw(p, p2, p.add(dir.multiply(1.8))));
        assertEquals(2, ccw(p, p2, p.add(dir.multiply(-1.7))));
        assertEquals(0, ccw(p, p2, p.add(dir.multiply(0.5))));

        assertTrue(intersectsLP(p, p2, p.add(dir.multiply(1.8))));
        assertTrue(intersectsLP(p, p2, p.add(dir.multiply(-1.7))));
        assertTrue(intersectsLP(p, p2, p.add(dir.multiply(0.5))));
        assertTrue(intersectsLP(p, p2, p2));
        assertFalse(intersectsLP(p, p2, p(45, 9)));
        assertFalse(intersectsLP(p, p2, p(-4364, 120)));

        assertEquals(1, ccw(p, p2, p(-45, 72)));
        assertEquals(1, ccw(p, p2, p(80, 7)));
        assertEquals(-1, ccw(p, p2, p(70, 5)));
        assertEquals(1, ccw(p, p2, p(1800, -50)));
        assertEquals(-1, ccw(p, p2, p(500, -50)));
        assertEquals(-1, ccw(p, p2, p(-4364, 120)));

        assertEquals(0, ccw(zeroP, zeroP, zeroP));
        assertEquals(0, ccw(oneP, zeroP, zeroP));
        assertEquals(0, ccw(zeroP, oneP, zeroP));
        assertEquals(-2, ccw(zeroP, zeroP, oneP));  // この場合は0を返した方が便利かもしれない
    }

    @Test(timeout = 2000, expected = IllegalArgumentException.class)
    public void testIntersections() {
        Point a1 = p(0, 0);
        Point a2 = p(1, 2);
        Point b1 = p(1, 0);
        Point b2 = p(-4, 1);
        Point bp1 = p(11, -2);
        Point bp2 = p(16, -3);
        // 線分aと線分bは交差する。線分aと線分bpは交差しない。
        // 線分bと線分bpは同一直線上に存在するが重ならない。

        assertTrue(intersectsLL(a1, a2, b1, b2));
        assertTrue(intersectsLL(a1, a2, b1.add(oneP), b2.add(oneP)));
        assertTrue(intersectsLS(a1, a2, b1, b2));
        assertFalse(intersectsLP(a1, a2, b1));
        assertFalse(intersectsLP(a1, a2, b2));
        assertTrue(intersectsSS(a1, a2, b1, b2));
        assertFalse(intersectsSP(a1, a2, b1));

        assertTrue(intersectsLL(a1, a2, bp1, bp2));
        assertFalse(intersectsLS(a1, a2, bp1, bp2));
        assertFalse(intersectsSS(a1, a2, bp1, bp2));
        assertTrue(intersectsLS(bp1, bp2, a1, a2));
        assertFalse(intersectsSS(bp1, bp2, a1, a2));
        assertTrue(intersectsLL(b1, b2, bp1, bp2));
        assertTrue(intersectsLS(b1, b2, bp1, bp2));
        assertTrue(intersectsLS(bp1, bp2, b1, b2));
        assertTrue(intersectsLP(b1, b2, bp1));
        assertFalse(intersectsSS(b1, b2, bp1, bp2));
        assertFalse(intersectsLL(b1, b2, bp1.add(oneP), bp2.add(oneP)));

        assertTrue(intersectsLL(a1, a2, a1, a2));
        assertTrue(intersectsLP(a1, a2, a1));
        assertTrue(intersectsSS(a1, a2, a1, a2));
        assertTrue(intersectsSP(a1, a2, a2));

        Point c1 = p(3287, 282 + 2844.0 / 4379);
        Point c2 = p(-2154281.0 / 187, -982);  // 線分cは点a2を通る
        assertTrue(intersectsSS(a1, a2, c1, c2));
        assertFalse(intersectsSS(a1, a2, c1.add(p(0, 1)), c2));
        assertFalse(intersectsSS(a1, a2, c1, c2.add(p(-0.1, 0))));

        assertEquals(p(1.0 / 11, 2.0 / 11), crosspointLL(a1, a2, b1, b2));
        assertEquals(a2, crosspointLL(a1, a2, c1, c2));

        assertEquals(b1, crosspointLL(b1, b2, bp1, bp2));  // ???
        crosspointLL(b1, b2, bp1.add(oneP), bp2.add(oneP));
    }

    @Test
    public void testDistances() {
        Point a1 = p(0, 0);
        Point a2 = p(1, 2);
        Point b1 = p(1, 0);
        Point b2 = p(-4, 1);
        Point bp1 = p(11, -2);
        Point bp2 = p(16, -3);
        // 線分aと線分bは交差する。線分aと線分bpは交差しない。
        // 線分bと線分bpは同一直線上に存在するが重ならない。

        assertEquals(0, distanceLL(a1, a2, b1, b2), EPS);
        assertEquals(0, distanceLS(a1, a2, b1, b2), EPS);
        assertEquals(0, distanceSS(a1, a2, b1, b2), EPS);
        assertEquals(2 / sqrt(5), distanceLP(a1, a2, b1), EPS);
        assertEquals(2 / sqrt(5), distanceSP(a1, a2, b1), EPS);
        assertEquals(9 / sqrt(5), distanceLP(a1, a2, b2), EPS);
        assertEquals(sqrt(17), distanceSP(a1, a2, b2), EPS);
        assertEquals(1 / sqrt(5), distanceLP(a1, a2, oneP), EPS);

        assertEquals(0, distanceLL(a1, a2, bp1, bp2), EPS);
        assertEquals(24 / sqrt(5), distanceLS(a1, a2, bp1, bp2), EPS);
        assertEquals(0, distanceLS(bp1, bp2, a1, a2), EPS);
        assertEquals(sqrt(116), distanceSS(a1, a2, bp1, bp2), EPS);
        assertEquals(0, distanceLL(b1, b2, bp1, bp2), EPS);
        assertEquals(0, distanceLS(b1, b2, bp1, bp2), EPS);
        assertEquals(sqrt(104), distanceSS(b1, b2, bp1, bp2), EPS);
        assertEquals(sqrt(234), distanceSP(b1, b2, bp2), EPS);
        assertEquals(3 * sqrt(2.0 / 13),
                distanceLL(b1, b2, bp1.add(oneP), bp2.add(oneP)), EPS);

        assertEquals(0, distanceLL(a1, a2, a1, a2), EPS);
        assertEquals(0, distanceLS(a1, a2, a1, a2), EPS);
        assertEquals(0, distanceLP(a1, a2, a1), EPS);
        assertEquals(0, distanceSP(a1, a2, a2), EPS);

        assertEquals(1, distanceSS(zeroP, p(2, 0), oneP, p(1, 2)), EPS);

        assertEquals(1 / (13 * sqrt(114293)), distanceLS(p(0, 8385.0 / 4379), p(
                -8385.0 / 374, 0), a1, a2), EPS);
    }

    @Test
    public void testCircles() {
        Point a1 = p(0, 0);
        Point a2 = p(1, 2);
        Point c = p(1, -1);

        assertEquals(3 / sqrt(5) - 1, distanceLC(a1, a2, c, 1), EPS);
        assertEquals(0, distanceLC(a1, a2, c, 5), EPS);
        assertEquals(sqrt(2) - 1, distanceSC(a1, a2, c, 1), EPS);
        assertEquals(0, distanceSC(a1, a2, c, 2), EPS);
        assertEquals(2, distanceSC(a1, a2, c, 5), EPS);
        assertEquals(sqrt(2) - 1.4, distanceSC(a1, a2, c, 1.4), EPS);
        assertEquals(sqrt(13) - 1, distanceSC(a1, p(0, 1), p(3, 3), 1), EPS);

        assertArrayEqualsSorted(new Point[] {
                p(-(1 + sqrt(11)) / 5, -(1 + sqrt(11)) * 2 / 5),
                p((sqrt(11) - 1) / 5, (sqrt(11) - 1) * 2 / 5) }, crosspointLC(a1, a2, c,
                2));
        assertArrayEqualsSorted(new Point[] {
                p(-(1 + 2 * sqrt(29)) / 5, -(1 + 2 * sqrt(29)) * 2 / 5),
                p((2 * sqrt(29) - 1) / 5, (2 * sqrt(29) - 1) * 2 / 5) }, crosspointLC(a1,
                a2, c, 5));
        assertArrayEqualsSorted(new Point[] { p(-1.0 / 5, -2.0 / 5) }, crosspointLC(a1,
                a2, c, 3 / sqrt(5)));
        assertArrayEqualsSorted(new Point[] {}, crosspointLC(a1, a2, c, 1));

        assertEquals(1 / sqrt(5) - 0.4, distanceSC(a1, a2, oneP, 0.4), EPS);
        assertEquals(0, distanceSC(a1, a2, oneP, 1 / sqrt(5)), EPS);
        assertEquals(0, distanceSC(a1, a2, oneP, 0.7), EPS);
        assertEquals(0, distanceSC(a1, a2, oneP, 1.2), EPS);
        assertEquals(1.5 - sqrt(2), distanceSC(a1, a2, oneP, 1.5), EPS);
        assertEquals(1 / sqrt(5) - 0.4, distanceLC(a1, a2, oneP, 0.4), EPS);
        assertEquals(0, distanceLC(a1, a2, oneP, 1.5), EPS);


        Point b = p(0, 1);
        assertEquals(-2, intersectionCC(b, 1, c, 1));
        assertEquals(2, intersectionCC(b, 1, c, sqrt(5) - 1));
        assertEquals(1, intersectionCC(b, 1, c, 2));
        assertEquals(3, intersectionCC(b, 1, c, sqrt(5) + 1));
        assertEquals(-1, intersectionCC(b, 1, c, 4));

        assertEquals(sqrt(5) - 2, distanceCC(b, 1, c, 1), EPS);
        assertEquals(0, distanceCC(b, 1, c, sqrt(5) - 1), EPS);
        assertEquals(0, distanceCC(b, 1, c, 2), EPS);
        assertEquals(0, distanceCC(b, 1, c, sqrt(5) + 1), EPS);
        assertEquals(3 - sqrt(5), distanceCC(b, 1, c, 4), EPS);

        assertArrayEqualsSorted(new Point[] { p(1 / sqrt(5), 1 - 2 / sqrt(5)) }, crosspointCC(
                b, 1, c, sqrt(5) - 1));
        assertArrayEqualsSorted(new Point[] { p(-1 / sqrt(5), 1 + 2 / sqrt(5)) }, crosspointCC(
                b, 1, c, sqrt(5) + 1));
        assertArrayEqualsSorted(new Point[] { p(1 + 1 / sqrt(5), -1 - 2 / sqrt(5)) },
                crosspointCC(b, sqrt(5) + 1, c, 1));

        assertArrayEqualsSorted(new Point[] { p(-0.6, 0.2), p(1, 1) }, crosspointCC(b, 1,
                c, 2));
        assertArrayEqualsSorted(new Point[] { }, crosspointCC(b, 1,
                c, 10));
        assertArrayEqualsSorted(new Point[] { }, crosspointCC(b, 10,
                c, 1));


        assertArrayEquals(new Point[] {
                p(-0.5, 1 + sqrt(3) / 2), p(-0.5, 1 - sqrt(3) / 2) }, tangent(b, 1, p(-2,
                1)));
        assertArrayEquals(new Point[] {
                p((3 - 4 * sqrt(3)) / 13, 1 - (1 + 3 * sqrt(3)) * 2 / 13),
                p((3 + 4 * sqrt(3)) / 13, 1 + (3 * sqrt(3) - 1) * 2 / 13) }, tangent(b,
                1, p(3, -1)));
        assertArrayEquals(new Point[] { p(40.0 / 29, -13.0 / 29), p(0, 3) }, tangent(b,
                2, p(5, 3)));

        assertEquals(p(3.0 / 2, 1.0 / 2), circumcenter(a1, a2, b));
        assertEquals(p(1.0 / 2, 1.0 / 2), circumcenter(zeroP, oneP, b));
        assertEquals(p(37.0 / 28, 19.0 / 28), circumcenter(a2, b, p(2, -0.5)));

        assertEquals(p(sqrt(2) / (2 + sqrt(2) + sqrt(10)), sqrt(2) * (2 + sqrt(5))
                / (2 + sqrt(2) + sqrt(10))), incenter(a1, a2, b));
        assertEquals(p(1 / (2 + sqrt(2)), 1 / sqrt(2)), incenter(zeroP, oneP, b));
        assertEquals(p((5.0 / 2 + sqrt(8)) / (5.0 / 2 + sqrt(2) + sqrt(29) / 2),
                (5 - 1 / sqrt(2) + sqrt(29) / 2) / (5.0 / 2 + sqrt(2) + sqrt(29) / 2)),
                incenter(a2, b, p(2, -0.5)));
    }

    @Test
    public void testTangentLines() {
        Line[][] tangents = tangentLines(zeroP, 1, oneP, 1);
        assertArrayEquals(new Line[][] {
                {
                        l(p(-sqrt(2) / 2, sqrt(2) / 2), p(1 - sqrt(2) / 2,
                                1 + sqrt(2) / 2)),
                        l(p(sqrt(2) / 2, -sqrt(2) / 2), p(1 + sqrt(2) / 2,
                                1 - sqrt(2) / 2)) }, null }, tangents);

        tangents = tangentLines(zeroP, 1, p(5, 0), 2);
        assertArrayEquals(
                new Line[][] {
                        {
                                l(p(-1.0 / 5, 2 * sqrt(6) / 5), p(23.0 / 5,
                                        4 * sqrt(6) / 5)),
                                l(p(-1.0 / 5, -2 * sqrt(6) / 5), p(23.0 / 5, -4 * sqrt(6)
                                        / 5)) },
                        {
                                l(p(3.0 / 5, 4.0 / 5), p(19.0 / 5, -8.0 / 5)),
                                l(p(3.0 / 5, -4.0 / 5), p(19.0 / 5, 8.0 / 5)) }, },
                tangents);
        tangents = tangentLines(zeroP, 2, p(5, 0), 1);
        assertArrayEquals(new Line[][] {
                {
                        l(p(2.0 / 5, 4 * sqrt(6) / 5), p(26.0 / 5, 2 * sqrt(6) / 5)),
                        l(p(2.0 / 5, -4 * sqrt(6) / 5), p(26.0 / 5, -2 * sqrt(6) / 5)) },
                {
                        l(p(6.0 / 5, 8.0 / 5), p(22.0 / 5, -4.0 / 5)),
                        l(p(6.0 / 5, -8.0 / 5), p(22.0 / 5, 4.0 / 5)) }, }, tangents);

        tangents = tangentLines(zeroP, 2, p(3, 0), 1);
        assertArrayEquals(new Line[][] {
                {
                        l(p(2.0 / 3, 4 * sqrt(2) / 3), p(10.0 / 3, 2 * sqrt(2) / 3)),
                        l(p(2.0 / 3, -4 * sqrt(2) / 3), p(10.0 / 3, -2 * sqrt(2) / 3)) },
                { l(p(2, 0), p(2, 0)), null }, }, tangents);
        tangents = tangentLines(zeroP, 1, p(3, 0), 2);
        assertArrayEquals(new Line[][] {
                {
                        l(p(-1.0 / 3, 2 * sqrt(2) / 3), p(7.0 / 3, 4 * sqrt(2) / 3)),
                        l(p(-1.0 / 3, -2 * sqrt(2) / 3), p(7.0 / 3, -4 * sqrt(2) / 3)) },
                { l(p(1, 0), p(1, 0)), null }, }, tangents);
        tangents = tangentLines(p(3, 0), 1, zeroP, 2);
        assertArrayEquals(new Line[][] {
                {
                        l(p(10.0 / 3, -2 * sqrt(2) / 3), p(2.0 / 3, -4 * sqrt(2) / 3)),
                        l(p(10.0 / 3, 2 * sqrt(2) / 3), p(2.0 / 3, 4 * sqrt(2) / 3)) },
                { l(p(2, 0), p(2, 0)), null }, }, tangents);

        tangents = tangentLines(zeroP, 1, p(3, 0), 1);
        assertArrayEquals(new Line[][] {
                { l(p(0, 1), p(3, 1)), l(p(0, -1), p(3, -1)) },
                {
                        l(p(2.0 / 3, sqrt(5) / 3), p(7.0 / 3, -sqrt(5) / 3)),
                        l(p(2.0 / 3, -sqrt(5) / 3), p(7.0 / 3, sqrt(5) / 3)) }, },
                tangents);
        tangents = tangentLines(zeroP, 1, p(2, 0), 1);
        assertArrayEquals(new Line[][] {
                { l(p(0, 1), p(2, 1)), l(p(0, -1), p(2, -1)) },
                { l(p(1, 0), p(1, 0)), null }, }, tangents);

        tangents = tangentLines(zeroP, 1, p(2, 0), 1);
        assertArrayEquals(new Line[][] {
                { l(p(0, 1), p(2, 1)), l(p(0, -1), p(2, -1)) },
                { l(p(1, 0), p(1, 0)), null }, }, tangents);
        tangents = tangentLines(zeroP, 3, p(2, 0), 1);
        assertArrayEquals(new Line[][] { { l(p(3, 0), p(3, 0)), null }, null }, tangents);

        tangents = tangentLines(zeroP, 4, p(2, 0), 1);
        assertArrayEquals(new Line[][] { null, null }, tangents);
        tangents = tangentLines(zeroP, 4, zeroP, 1);
        assertArrayEquals(new Line[][] { null, null }, tangents);
        tangents = tangentLines(zeroP, 1, zeroP, 1);
        assertArrayEquals(new Line[][] { null, null }, tangents);
    }

    @Test
    public void testCirclesThru2PointsWithRadius() {
        Point[] centers = circlesThru2PointsWithRadius(zeroP, p(0, 1), 5);
        assertEquals(2, centers.length);
        for (Point c : centers) {
            assertEquals(5, c.distance(zeroP), EPS);
            assertEquals(5, c.distance(p(0, 1)), EPS);
        }

        centers = circlesThru2PointsWithRadius(zeroP, p(0, 1), 0.5);
        assertEquals(1, centers.length);
        for (Point c : centers) {
            assertEquals(0.5, c.distance(zeroP), EPS);
            assertEquals(0.5, c.distance(p(0, 1)), EPS);
        }

        centers = circlesThru2PointsWithRadius(zeroP, p(0, 1), 0.4);
        assertEquals(0, centers.length);
        centers = circlesThru2PointsWithRadius(zeroP, zeroP, 1);
        assertEquals(0, centers.length);
    }

    @Test
    public void testCirclesThru2PointsWithTangent() {
        Point[] centers = circlesThru2PointsWithTangent(zeroP, p(0, 1), p(0, 2), p(1, 3));
        circlesThru2PointsWithTangent(p(1, -1), p(1, 1), p(-1, 20), p(1, 30));
        Point[][] testData = {
                { zeroP, p(0, 1), p(0, 2), p(1, 3) },
                { p(1, -1), p(1, 1), p(-1, 20), p(1, 30) },
                { p(2.43, 3.1), p(1.2, 6.43), p(287, 382), p(-2.3, 15.3) },
                { p(0, 1), p(1, 1), p(0, 0), p(1, 2) },
                { p(0, 1), p(1, 1), p(-1, 0), p(1, 2) },
                { p(0, 0), p(0, 1), p(-1, 2), p(1, 2) },
                { p(0, 0), p(0, 1), p(-1, 1), p(1, 1) },
                { p(0, 0), p(0, 1), p(-1, -1), p(-1, 1) },
                { p(0, 0), p(0, 1), p(0, 1), p(0, 0) } };
        int[] lens = { 2, 2, 2, 0, 1, 2, 1, 1, 0 };
        int i = 0;
        for (Point[] data : testData) {
            centers = circlesThru2PointsWithTangent(data[0], data[1], data[2], data[3]);
            //            System.out.println(Arrays.toString(centers));
            assertEquals(lens[i++], centers.length);
            for (Point c : centers) {
                assertTrue(approxEquals(c.distance(data[0]), c.distance(data[1])));
                assertTrue(approxEquals(c.distance(data[0]), distanceLP(data[2], data[3],
                        c)));
            }
        }
    }

    @Test
    public void testMinEnclosingCircle() {
        assertEquals(p(0.5, 1),
                minEnclosingCircle(new Point[] { zeroP, p(1, 2), p(0, 1) }));
        assertEquals(p(1, 3.0 / 4), minEnclosingCircle(new Point[] {
                zeroP, p(1, 2), p(2, 0), p(1, 1) }));

        Point[] manyPoints = new Point[1800];
        for (int i = 0; i < 1600; i++) {
            manyPoints[i] = new Point(i % 40 * 2, i / 40);
        }
        for (int i = 1600; i < 1800; i++) {
            manyPoints[i] = new Point(i % 20, i / 20 % 30);
        }
        assertEquals(p(39, 19.5), minEnclosingCircle(manyPoints));
    }

    @Test
    public void testIsConvex() {
        Point[] points = makePoints("0 0  4 5  6 5  9 3  10 4  12 -1  7 -4  4 0  3 -2  -1 1  8 2  -1 -1");
        assertFalse(isConvex(points));
        assertFalse(isCcwConvex(points));
        points = aConvex;
        assertTrue(isConvex(points));
        assertTrue(isCcwConvex(points));
        points = makePoints("0.50507641 39.107643  50.002551 25.218046  79.296972 -14.68298  93.944191 -57.867001  84.347738 -94.990105  34.850263 -82.363197  -0.50507641 -36.14872  -12.121831 5.7726092");
        assertTrue(isConvex(points));
        assertFalse(isCcwConvex(points));
        points = makePoints("0 0  1 0  2 0  3 0  2 0  1 0");
        assertTrue(isConvex(points));
        assertTrue(isCcwConvex(points));
        points = makePoints("-35.860415 -16.379211  -4.0406102 -35.067033  40.406102 13.925365  57.578695 56.856849  -37.88072 38.674103  -72.225907 6.060915  -11.111678 6.3492214");
        assertFalse(isConvex(points));
        assertFalse(isCcwConvex(points));
    }

    @Test
    public void testIsInPolygons() {
        Point[] points = aConvex;
        Point[][] checkeds = new Point[][] {
                makePoints("-1 5  0 2  13 -1  3 5  3.89 4.92  4 -4  6.1 5  10 12"),
                makePoints("0 0  8.2 2.2  10 -2.1  11.5 0  3.1 4.2  3.91 4.92  5 -1"),
                makePoints("-1 -1  9 -2.8  12 -1  5 5  3.9 4.92  -1 0") };
        for (int i = 0; i < checkeds.length; i++) {
            for (Point p : checkeds[i]) {
                assertEquals(i, isInConvex(p, points));
                assertEquals(i, isInCcwConvex(p, points));
                assertEquals(i, isInPolygon(p, points));
            }
        }
        points = makePoints("0 0  2 0  2 2  3 2  3 1  -2 1  -2 -2  0 -2");
        checkeds = new Point[][] {
                makePoints("4 2  4 1  -2.1 1  -2 3  2 -2  3 0  -4 0  0 3  2.5 0.5"),
                makePoints("2.5 1.5  1 0.5  -1 0  1.9 0.9"),
                makePoints("2 1  0 0  -1 -2 0.1 1") };
        for (int i = 0; i < checkeds.length; i++) {
            for (Point p : checkeds[i]) {
                assertEquals(i, isInPolygon(p, points));
            }
        }
        points = makePoints("-35.860415 -16.379211  -4.0406102 -35.067033  40.406102 13.925365  57.578695 56.856849  -37.88072 38.674103  -72.225907 6.060915  -11.111678 6.3492214");
        checkeds = new Point[][] {
                makePoints("-40 0  -37.88072 5.2  -27.47263 -8.6761"),
                makePoints("0 0  -20 -20  -27.47263 -8.6762"),
                makePoints("-4.0406102 -35.067033  -27.47263 -8.67614298654") };
        for (int i = 0; i < checkeds.length; i++) {
            for (Point p : checkeds[i]) {
                assertEquals(i, isInPolygon(p, points));
            }
        }
    }

    @Test
    public void testConvexHull() {
        checkConvexHull(makePoints("0 0  4 5  6 5  9 3  10 4  12 -1  7 -4  4 0  3 -2  -1 1  8 2  -1 -1"));
        checkConvexHull(makePoints("43.459 40.119  28.740 5.437  -1.038 -3.614  -28.130 -34.897  -13.785 -31.319  -48.709 1.616  9.819 4.463  -45.839 -21.446  -14.079 30.034  -32.017 -45.677"));
        checkConvexHull(makePoints("0 0  1 0  2 0  1 1"));
        checkConvexHull(makePoints("0 0  1 0  2 0  3 0"));
    }

    private void checkConvexHull(Point[] points) {
        Point[] hullp = convexHull(points);
        assertTrue(isCcwConvex(hullp));
        for (Point p : points) {
            int type = isInConvex(p, hullp);
            assertTrue(type != 0);
            // if (type == 2) assertTrue(hull.contains(p));  // 凸包が無駄な辺を含む場合
        }
    }

    @Test
    public void testConvexIntersection() {
        Point[] points = aConvex;

        Point[] points2 = makePoints("0.50507641 -39.107643  50.002551 -25.218046  79.296972 14.68298  93.944191 57.867001  84.347738 94.990105  34.850263 82.363197  -0.50507641 36.14872  -12.121831 -5.7726092");
        checkConvexIntersection(points, points2, makePoints("0 0  10 20"));

        points2 = makePoints("0 -4  6 -6  11 -1  8 2  3 1");
        checkConvexIntersection(points, points2, makePoints("0 0"));

        points2 = makePoints("1 6  0 0  1 -6  3 -7  9 -4  8 5");
        checkConvexIntersection(points, points2, makePoints("11 -1"));

        points2 = makePoints("7 -4   9 -6  12 -3  10 0");
        checkConvexIntersection(points, points2, makePoints("11 -1"));

        points2 = makePoints("0 9  0 -9  1 -9  1 9");
        checkConvexIntersection(points, points2, makePoints("11 -1"));

        points = makePoints("0.0 0.0  0.5 0.0  0.5 0.5  0.0 0.5");
        assertTrue(isCcwConvex(points));
        points2 = makePoints("0.0 0.0  0.5 0.0  0.5 0.5");
        checkConvexIntersection(points, points2, makePoints("11 -1"));

        points2 = makePoints("0.0 0.0  0.5 0.0  0.5 0.5  0 1");
        assertTrue(isCcwConvex(points2));
        checkConvexIntersection(points, points2, makePoints("11 -1"));
        points2 = makePoints("0.5 0.5  0 1  0.0 0.0  0.5 0.0");
        assertTrue(isCcwConvex(points2));
        checkConvexIntersection(points, points2, makePoints("11 -1"));

        points2 = makePoints("0.1 0.0  0.5 0.0  0.5 0.4");
        checkConvexIntersection(points, points2, makePoints("11 -1"));

        points2 = makePoints("0.5 0.4  0.1 0.0  0.5 0.0");
        checkConvexIntersection(points, points2, makePoints("11 -1"));

        points2 = makePoints("0.0 0.0  0.5 0.0  0.5 0.5  0.0 0.5");
        checkConvexIntersection(points, points2, makePoints("11 -1"));

        points2 = makePoints("0.0 -1.0  0.5 -1.0  0.5 0.0  0.0 0.0");
        assertTrue(isCcwConvex(points2));
        // checkConvexIntersection2(points, points2, makePoints("11 -1")); // 縮退無理OrZ

        points2 = makePoints("0.1584936490538903 -0.09150635094610966  0.5915063509461096 0.1584936490538903  0.3415063509461097 0.5915063509461096  -0.09150635094610968 0.3415063509461097");
        assertTrue(isCcwConvex(points2));
        checkConvexIntersection(points, points2, makePoints("11 -1"));

        points2 = makePoints("1 1  2 0  2 2");
        assertEquals(Collections.emptyList(), convexIntersection(points, points2));

        points2 = makePoints("0 0  0.2 -0.2  0.5 0.5  0.2 0.7");
        checkConvexIntersection(points, points2, makePoints("11 -1"));
    }

    private void checkConvexIntersection(Point[] convex1, Point[] convex2,
            Point[] testingPoints) {
        ArrayList<Point> convexAnd = convexIntersection(convex1, convex2);
        Point[] convexAndP = convexAnd.toArray(new Point[convexAnd.size()]);
        assertTrue(isCcwConvex(convexAndP));

        ArrayList<Point> testingPointList = new ArrayList<Point>(Arrays.asList(convex1));
        testingPointList.addAll(Arrays.asList(convex2));
        testingPointList.addAll(Arrays.asList(testingPoints));
        for (Point p : testingPointList) {
            assertTrue((isInCcwConvex(p, convexAndP) != 0) == (isInCcwConvex(p, convex1) != 0 && isInCcwConvex(
                    p, convex2) != 0));
        }
    }

    @Test
    public void testConvexCut() {
        Point[] points = aConvex;
        assertEquals(
                Arrays.asList(p(-1.0, -1.0), p(5.0, 5.0), p(4.0, 5.0), p(-1.0, 1.0)),
                convexCut(points, zeroP, oneP));
        assertEquals(Arrays.asList(p(-1.0, -1.0), p(7.0, -4.0), p(12.0, -1.0), p(10.0,
                4.0), p(6.0, 5.0), p(5.0, 5.0)), convexCut(points, oneP, zeroP));
        assertEquals(Arrays.asList(p(11.6, 0.0), p(10.0, 4.0), p(6.0, 5.0), p(4.0, 5.0),
                p(-1.0, 1.0), p(-1.0, 0.0)), convexCut(points, zeroP, p(1, 0)));

        assertEquals(Arrays.asList(p(-1.0, -1.0), p(7.0, -4.0), p(5.0, 5.0), p(4.0, 5.0),
                p(-1.0, 1.0)), convexCut(points, p(7, -4), p(5, 5)));
        assertEquals(Arrays.asList(p(-1.0, -1.0), p(7.0, -4.0), p(7.5, -3.7),
                p(6.0, 5.0), p(4.0, 5.0), p(-1.0, 1.0)), convexCut(points, p(7.5, -3.7),
                p(6, 5)));
        assertEquals(Arrays.asList(points), convexCut(points, p(10, 4), p(6, 5)));
        assertEquals(Arrays.asList(p(10.0, 4.0), p(6.0, 5.0)), convexCut(points, p(6, 5),
                p(10, 4)));
        assertEquals(Arrays.asList(points), convexCut(points, p(0, -100), p(100, -100)));
        assertEquals(Arrays.asList(), convexCut(points, p(100, -100), p(0, -100)));
        assertEquals(Arrays.asList(p(-1, -1)),
                convexCut(points, p(99, -101), p(-101, 99)));

        // assertEquals(Arrays.asList(p(0, 0),p(1,0),p(1.5,0),p(1.5,0)),  // 縮退しても動くが無駄な辺ができる
        // convexCut(makePoints("0 0  1 0  2 0"), p(1.5, -1), p(1.5, 1)));
    }

    @Test
    public void testConvexDiameter() {
        Point[] points = aConvex;
        assertArrayEqualsSorted(new int[] { 2, 6 }, convexDiameter(aConvex));

        points = makePoints("0 0  1 0  0.6 100");
        assertArrayEqualsSorted(new int[] { 0, 2 }, convexDiameter(points));

        points = makePoints("0 1  0 0  5 0  4.9 1  4.7 2  3 10  2.6 10.1");
        assertArrayEqualsSorted(new int[] { 1, 5 }, convexDiameter(points));

        points = makePoints("0 0  0 -1  1 -1  1 0  1 1  0 1");
        assertArrayEqualsSorted(new int[] { 1, 4 }, convexDiameter(points));

        points = makePoints("0 0  0 -1  1 -1  1 -1  1 0  1 1  0 1");
        assertArrayEqualsSorted(new int[] { 1, 5 }, convexDiameter(points));

        points = makePoints("0 1  -1 1  -1 0  0 0  1 0  1 1");
        assertArrayEqualsSorted(new int[] { 1, 4 }, convexDiameter(points));
    }

    @Test
    public void testArea() {
        Point[] points = aConvex;
        assertEquals(78.5, area(points), EPS);

        points = makePoints("0 0  2 0  0 1");
        assertEquals(1, area(points), EPS);

        points = makePoints("0 0  0 1  2 0");
        assertEquals(-1, area(points), EPS);

        points = makePoints("0 0  2 0  2 0  2 2");
        assertEquals(2, area(points), EPS);

        points = makePoints("100 100  102 100  102 100  102 102");
        assertEquals(2, area(points), EPS);

        points = makePoints("0 0  1 0  2 0");
        assertEquals(0, area(points), EPS);
    }

    @Test
    public void testCentroid() {
        Point[] points = aConvex;
        assertEquals(p(1330.0 / 3 / 78.5, 151.0 / 3 / 78.5), centroid(points));

        points = makePoints("0 0  1 0  2 0.5  1 1  0 1");
        assertEquals(p(7.0 / 9, 0.5), centroid(points));

        points = makePoints("0 0  3 0  1 1  0 3");
        assertEquals(p(5.0 / 6, 5.0 / 6), centroid(points));

        points = makePoints("0 0  0 23  27 2  12 1  9 4  2 23  1 19");
        assertEquals(p(13597.0 / 6 / 182.5, 1553.0 / 182.5), centroid(points));
    }

    @Test
    public void testVoronoiCell() {
        assertArrayEquals(new Point[] {
                p(-10, -10), p(0.1875, -10), p(55.0 / 14, -1.0 / 42), p(3.9, 0),
                p(-10, 0) }, voronoiCell(aConvex[0], aConvex,
                makePoints("-10 -10  10 -10  10 10  -10 10")));
        // TODO: test more
    }

    private void assertHasEdge(AdjGraph g, Edge actual) {
        for (Edge edge : g.edges[actual.from]) {
            if (actual.to == edge.to) {
                assertEquals(actual.cost, actual.cost, EPS);
                return;
            }
        }
        fail("Expected edge not found: " + actual);
    }

    private void assertHasNoEdge(AdjGraph g, int from, int to) {
        for (Edge edge : g.edges[from]) {
            assertFalse(to + ", " + from, to == edge.to);
        }
    }

    @Test
    public void testSegmentArrangement() {
        Line[] segs = {
                l(zeroP, oneP), l(p(0, 1), p(1, 0)), l(p(1, 0), p(3, 0)),
                l(p(2, 2), p(5, 1)), l(p(3, 0), p(4, 5)), };
        ArrayList<Point> psMap = new ArrayList<Geometries2D.Point>();
        AdjGraph g = segmentArrangement(segs, psMap);

        assertTrue(psMap.contains(p(0.5, 0.5)));
        assertTrue(psMap.contains(p(53.0 / 16, 25.0 / 16)));
        for (Line seg : segs) {
            assertTrue(psMap.contains(seg.a));
            assertTrue(psMap.contains(seg.b));
        }

        Edge[] edges = {
                new Edge(psMap.indexOf(p(0, 0)), psMap.indexOf(p(0.5, 0.5)), sqrt(2) / 2),
                new Edge(psMap.indexOf(p(0.5, 0.5)), psMap.indexOf(p(0, 1)), sqrt(2) / 2),
                new Edge(psMap.indexOf(p(2, 2)), psMap.indexOf(p(53.0 / 16, 25.0 / 16)),
                        7 * sqrt(5.0 / 2) / 8),
                new Edge(psMap.indexOf(p(1, 0)), psMap.indexOf(p(3, 0)), 2) };
        int[][] noEdgeTable = {
                { psMap.indexOf(p(0, 0)), psMap.indexOf(p(1, 1)) },
                { psMap.indexOf(p(0, 0)), psMap.indexOf(p(0, 1)) },
                { psMap.indexOf(p(3, 0)), psMap.indexOf(p(4, 5)) }, };
        for (Edge edge : edges) {
            assertHasEdge(g, edge);
        }
        for (int[] is : noEdgeTable) {
            assertHasNoEdge(g, is[0], is[1]);
        }
    }

    @Test
    public void testVisibilityGraph() {
        Point[] ps = Arrays.copyOf(aConvex, aConvex.length + 3);
        ps[ps.length - 3] = p(-4, -2);
        ps[ps.length - 2] = p(5, 7);
        ps[ps.length - 1] = p(11, 6);
//        System.out.println(Arrays.toString(ps));
        AdjGraph g = visibilityGraph(ps, new Point[][] { aConvex });
        //        for (ArrayList<Edge> edges : g.edges) {
        //            System.out.println(edges);
        //        }
        Edge[] existingEdges = {
                new Edge(2, 1, sqrt(34)), new Edge(5, 6, sqrt(41)), new Edge(6, 0, 2),
                new Edge(5, 6, sqrt(41)), new Edge(6, 7, sqrt(18)),
                new Edge(7, 1, sqrt(125)), new Edge(8, 3, 6), new Edge(8, 9, sqrt(37)), };
        int[][] noEdgeTable = {
                { 0, 2 }, { 1, 4 }, { 7, 5 }, { 7, 2 }, { 7, 8 }, { 8, 2 }, };
        for (Edge edge : existingEdges) {
            assertHasEdge(g, edge);
        }
        for (int[] is : noEdgeTable) {
            assertHasNoEdge(g, is[0], is[1]);
        }

        ps = new Point[18 + 5];
        int i = 0;
        ps[i++] = p(-9, -9);
        ps[i++] = p(4, -3);
        ps[i++] = p(9, 9);
        ps[i++] = p(-4, 3);
        Point[][] objs = { { p(-10, -10), p(10, -10), p(10, 10), p(-10, 10) },  // 4..7, 外殻
                { p(-5, -5), p(0, -5), p(0, 0), p(-5, 0) },  // 8..11, objs[2]と一点で接する
                { p(0, 0), p(5, 0), p(5, 5), p(0, 5) },  // 12..15, objs[3]と辺の一部が接する
                { p(1, 0), p(2, -1), p(3, 0) },  // 16..18
                { p(-3, -1), p(-1, -1), p(-2, 1) },  // 19..21, objs[1]と交差する
        };
        for (Point[] obj : objs) {
            for (Point p : obj) {
                ps[i++] = p;
            }
        }
        ps[i++] = p(0, -12);  // 22, objs[0]の外側
        g = visibilityGraph(ps, objs);
        int degs[] = {
                0x4, 7, 4, 6, 0x3, 3, 2, 2, 0x3, 9, 8, 5, 0x8, 6, 3, 7, 0x5, 7, 5, 0x1,
                1, 5, 0x2 };
        for (int j = 0; j < i; j++) {
            assertEquals(degs[j], g.edges[j].size());
            // degs[16] と degs[18] は共に4とすべきかもしれないが、POJ2678的には5でよい
        }
        existingEdges = new Edge[] {
                new Edge(10, 12, 0), new Edge(16, 18, 2), new Edge(1, 13, sqrt(10)),
                new Edge(1, 2, 13) };
        noEdgeTable = new int[][] { { 1, 16 }, { 0, 1 }, { 0, 4 } };
        for (Edge edge : existingEdges) {
            assertHasEdge(g, edge);
        }
        for (int[] is : noEdgeTable) {
            assertHasNoEdge(g, is[0], is[1]);
        }
    }

    @Test
    public void testMergeSegments() {
        Line[] segs = {
                l(p(0, 0), p(3, 3)), l(p(4, 4), p(2, 2)), l(p(5, 5), p(8, 8)),
                l(p(7, 7), p(6, 6)), l(p(8, 8), p(9, 9)), l(p(1, 0), p(2, 3)) };
        Line[] mergedSegs = mergeSegments(segs);

        assertEquals(mergedSegs.length, 3);
        Arrays.asList(mergedSegs).containsAll(
                Arrays.asList(l(p(0, 0), p(4, 4)), l(p(5, 5), p(9, 9)), l(p(1, 0),
                        p(2, 3))));
    }
}
