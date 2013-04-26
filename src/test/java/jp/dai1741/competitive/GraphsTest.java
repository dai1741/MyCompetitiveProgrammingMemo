package jp.dai1741.competitive;

import static org.junit.Assert.*;

import jp.dai1741.competitive.Graphs.AllGraph;
import jp.dai1741.competitive.Graphs.Edge;
import jp.dai1741.competitive.Graphs.FlowListsGraph;
import jp.dai1741.competitive.Graphs.ListsGraph;

import static jp.dai1741.competitive.Graphs.*;

import org.junit.Ignore;
import org.junit.Test;

import java.awt.geom.IllegalPathStateException;
import java.util.Arrays;
import java.util.Random;

public class GraphsTest {

    AllGraph graphWithNegativeEdge = makeGraph("7 "
            + "0 1 3  0 5 2  1 2 1  2 0 3  2 3 2  3 6 3 "
            + "4 3 4  4 5 2  3 1 3  5 3 4  5 6 8  6 0 -5", true, false);
    AllGraph graphWithNegativeLoop = makeGraph("6 "
            + "0 1 3  0 4 1  1 2 1  2 3 1  3 1 -3  3 4 3  4 5 -1  5 4 1", true, false);
    AllGraph noedges = makeGraph("10", false, false);

    AllGraph standardWaightedGraph = makeGraph("8 "
            + "0 1 3  0 5 2  1 2 3  2 0 9  2 3 3  3 4 1  3 7 3 "
            + "4 1 1  4 3 3  4 5 2  5 2 3  5 3 10  5 7 8  6 1 2  7 0 1", true, false);

    @Test(timeout = 2000)
    public void testGraphStructure() {
        AllGraph g = makeGraph("10  0 1  0 5  1 2  3 4  3 7  8 0", false, false);

        assertEquals(10, g.matrixGraph.n);
        assertEquals(2, g.listsGraph.edges[0].size());
        assertEquals(6, g.listGraph.edges.size());

        assertNotNull(g.matrixGraph.edges[0][1]);
        assertNotNull(g.matrixGraph.edges[3][4]);
        assertNotNull(g.matrixGraph.edges[8][0]);
        assertNull(g.matrixGraph.edges[0][8]);
        assertNull(g.matrixGraph.edges[4][6]);
        assertEquals(1, g.matrixGraph.edges[3][7].cost);

        assertEquals(5, g.listsGraph.edges[0].get(1).to);
        assertEquals(4, g.listsGraph.edges[3].get(0).to);
        assertEquals(8, g.listsGraph.edges[8].get(0).from);
        assertEquals(1, g.listsGraph.edges[1].get(0).cost);

        assertEquals(1, g.listGraph.edges.get(2).from);
        assertEquals(2, g.listGraph.edges.get(2).to);
        assertEquals(1, g.listGraph.edges.get(4).cost);
    }

    @Test(timeout = 2000)
    public void testDijkstra() {
        AllGraph g = standardWaightedGraph;

        assertEquals(10, dijkstra(g.matrixGraph, 0, 7));
        assertEquals(10, dijkstra(g.listsGraph, 0, 7));
        assertEquals(INF, dijkstra(g.matrixGraph, 2, 6));
        assertEquals(INF, dijkstra(g.listsGraph, 2, 6));

        int[] distFrom0 = { 0, 3, 5, 8, 9, 2, INF, 10 };
        assertArrayEquals(distFrom0, dijkstra(g.matrixGraph, 0));
        assertArrayEquals(distFrom0, dijkstra(g.listsGraph, 0));
        int[] distFrom3 = { 4, 2, 5, 0, 1, 3, INF, 3 };
        assertArrayEquals(distFrom3, dijkstra(g.matrixGraph, 3));
        assertArrayEquals(distFrom3, dijkstra(g.listsGraph, 3));

        int[] expectedPrev = { 7, 4, 1, -1, 3, 4, -1, 3 };
        int[] prev = new int[g.n];
        dijkstraWithPath(g.matrixGraph, 3, prev);
        assertArrayEquals(expectedPrev, prev);
        dijkstraWithPath(g.listsGraph, 3, prev);
        assertArrayEquals(expectedPrev, prev);
        assertEquals(Arrays.asList(3, 4, 5), getPath(prev, 3, 5));
        assertEquals(Arrays.asList(3, 7, 0), getPath(prev, 3, 0));

        assertEquals(INF, dijkstra(noedges.matrixGraph, 2, 6));
    }

    @Test(timeout = 2000)
    public void testBellmanFord() {
        AllGraph g = graphWithNegativeEdge;

        int[] distFrom0 = { 0, 3, 4, 6, INF, 2, 9 };
        assertArrayEquals(distFrom0, bellmanFord(g.matrixGraph, 0));
        assertArrayEquals(distFrom0, bellmanFord(g.listsGraph, 0));
        int[] distFrom3 = { -2, 1, 2, 0, INF, 0, 3 };
        assertArrayEquals(distFrom3, bellmanFord(g.matrixGraph, 3));
        assertArrayEquals(distFrom3, bellmanFord(g.listsGraph, 3));
        // implement prev?

        AllGraph loopG = graphWithNegativeLoop;

        int[] distFrom4 = { INF, INF, INF, INF, 0, -1 };
        assertArrayEquals(distFrom4, bellmanFord(loopG.matrixGraph, 4));
        assertArrayEquals(distFrom4, bellmanFord(loopG.listsGraph, 4));

        try {
            bellmanFord(loopG.matrixGraph, 0);
            fail();
        }
        catch (IllegalPathStateException e) {
        }
        try {
            bellmanFord(loopG.listsGraph, 1);
            fail();
        }
        catch (IllegalPathStateException e) {
        }
    }

    @Test(timeout = 2000)
    public void testWarshallFloyd() {
        AllGraph g = graphWithNegativeEdge;

        int[][] dists = {
                { 0, 3, 4, 6, INF, 2, 9 }, { 1, 0, 1, 3, INF, 3, 6 },
                { 0, 3, 0, 2, INF, 2, 5 }, { -2, 1, 2, 0, INF, 0, 3 },
                { 2, 5, 6, 4, 0, 2, 7 }, { 2, 5, 6, 4, INF, 0, 7 },
                { -5, -2, -1, 1, INF, -3, 0 }, };
        assertArrayEquals(dists, warshallFloyd(g.matrixGraph));
        assertArrayEquals(dists, warshallFloyd(g.listsGraph));

        int[][] paths = new int[g.n][g.n];
        warshallFloydWithPath(makeDistsArray(g.matrixGraph), paths);

        assertEquals(Arrays.asList(2, 3, 6, 0, 5), getPath(paths, 2, 5));
        assertEquals(Arrays.asList(5, 3, 6, 0, 1, 2), getPath(paths, 5, 2));
        assertEquals(Arrays.asList(1, 2, 3), getPath(paths, 1, 3));
        assertEquals(Arrays.asList(4, 5), getPath(paths, 4, 5));

        AllGraph loopG = graphWithNegativeLoop;
        assertTrue(warshallFloyd(loopG.matrixGraph)[1][1] < 0);
        assertTrue(warshallFloyd(loopG.listsGraph)[2][2] < 0);
    }

    @Test(timeout = 2000)
    public void testKruskal() {
        AllGraph g = makeGraph("7 " + "0 1 3  0 2 3  0 5 2  1 2 1  1 3 3  2 3 2 "
                + "3 4 4  3 5 4  3 6 3  4 5 2  5 6 8  6 0 -5", true, false, true);
        assertEquals(5, kruskal(g.matrixGraph));
        assertEquals(5, kruskal(g.listGraph));

        AllGraph loopG = makeGraph("6 "
                + "0 1 3  0 4 1  1 2 1  2 3 1  3 1 -3  3 4 3  4 5 -1", true, false, true);
        assertEquals(1, kruskal(loopG.matrixGraph));
        assertEquals(1, kruskal(loopG.listGraph));

        assertEquals(0, kruskal(noedges.listGraph)); // ??
    }

    @Test(timeout = 2000)
    public void testEdmondsKarp() {
        AllGraph g = makeGraph("8 " + "0 1 3  0 5 2  1 2 3  2 0 9  2 3 3  3 4 1  3 7 3 "
                + "4 1 1  4 3 3  4 5 2  5 2 3  5 3 10  5 7 8  6 1 2  7 0 1", false, true);

        assertEquals(5, edmondsKarp(g.flowListsGraph, 0, 7));
        g.flowListsGraph.resetFlow();
        assertEquals(0, edmondsKarp(g.flowListsGraph, 0, 6));

        g = makeGraph("7 " + "0 1 45  0 2 30  1 2 10  1 3 19  2 4 60  2 5 14  3 5 8 "
                + "3 6 22  4 3 11  4 6 37  5 2 10  5 4 33  5 6 1  ", false, true);
        // System.out.println(generateGraphViz(g.matrixGraph));
        assertEquals(59, edmondsKarp(g.flowListsGraph, 0, 6));


        assertEquals(0, edmondsKarp(noedges.flowListsGraph, 0, 4));

        AllGraph multiples = makeGraph("2 " + "0 1 10  0 1 10 0 1 20  ", false, true);
        assertEquals(40, edmondsKarp(multiples.flowListsGraph, 0, 1));


        // 0:s, 11:t, 1~5, 6~10: 二部グラフ
        AllGraph bipartite = makeGraph("12 "
                + "1 7 1  2 6 1  2 7 1  2 9 1  3 8 1  4 7 1  5 8 1  5 9 1  5 10 1",
                false, true);
        for (int i = 0; i < 5; i++) {
            bipartite.addArc(new Edge(0, i + 1, 0, 1));
            bipartite.addArc(new Edge(i + 6, 11, 0, 1));
        }
        assertEquals(4, edmondsKarp(bipartite.flowListsGraph, 0, 11));
        AllGraph big = makeGraph(
                "100  1 22 72 1 28 26 1 45 64 1 65 39 1 67 20 1 78 65 1 87 77 1 91 94 1 94 8 1 98 67 2 17 31 2 42 5 2 43 11 2 45 66 2 47 39 2 51 88 2 52 5 2 54 17 2 56 77 2 64 15 2 69 13 2 75 45 2 92 68 3 14 50 3 19 30 3 33 40 3 40 11 3 43 56 3 47 92 3 65 9 3 66 88 3 68 6 3 74 89 3 75 42 3 85 69 3 97 74 3 98 60 4 29 53 4 55 54 4 59 10 4 64 14 4 70 66 4 77 97 4 80 34 4 82 2 5 10 69 5 19 68 5 28 3 5 36 98 5 40 95 5 47 81 5 58 18 5 59 48 5 63 4 5 85 7 5 87 69 6 22 22 6 24 40 6 32 25 6 49 60 6 79 37 6 80 35 6 92 99 6 95 47 7 3 80 7 31 67 7 59 51 7 62 10 7 66 4 7 87 76 7 88 46 7 92 82 8 3 26 8 6 41 8 9 10 8 16 7 8 18 97 8 38 37 8 47 14 8 49 92 8 53 77 8 60 46 8 87 98 8 97 74 8 98 91 9 28 91 9 35 69 9 43 19 9 66 39 10 18 98 10 41 38 10 67 91 10 70 57 10 88 18 11 4 22 11 8 5 11 18 17 11 20 91 11 29 68 11 46 66 11 51 54 11 52 92 11 66 52 11 75 71 11 83 55 11 85 35 11 96 62 12 1 64 12 11 80 12 25 37 12 42 93 12 53 49 12 54 84 12 57 8 12 60 41 12 69 66 12 73 32 13 18 85 13 50 76 13 63 65 13 64 18 13 71 8 13 74 95 13 76 11 13 81 17 13 92 29 14 1 60 14 2 82 14 8 21 14 10 70 14 33 60 14 40 63 14 44 22 14 47 62 14 50 25 14 55 90 14 59 50 14 60 94 14 78 79 14 80 94 15 11 46 15 12 10 15 13 66 15 23 5 15 47 67 15 48 36 15 58 95 15 67 37 15 86 54 15 93 66 16 4 62 16 15 3 16 24 70 16 30 33 16 45 5 16 54 98 16 55 17 16 57 35 16 58 99 16 69 14 16 81 98 16 82 12 16 96 54 17 2 71 17 4 38 17 35 78 17 37 75 17 48 70 17 50 18 17 52 100 17 53 69 17 55 5 17 97 43 18 8 63 18 14 24 18 32 81 18 35 91 18 40 43 18 43 1 18 45 16 18 55 7 18 58 34 18 70 79 18 98 80 18 99 81 19 6 2 19 36 8 19 45 67 19 65 34 19 70 88 19 74 89 19 80 31 19 87 56 19 99 69 20 1 14 20 24 21 20 30 21 20 33 62 20 41 96 20 43 83 20 47 86 20 48 41 20 58 80 20 63 28 20 88 40 20 91 5 20 97 28 20 98 6 21 2 81 21 20 81 21 35 64 21 55 82 21 75 85 21 92 22 21 96 98 22 1 74 22 6 71 22 14 54 22 27 81 22 33 58 22 64 29 22 74 37 22 83 79 22 97 93 23 9 69 23 13 33 23 26 20 23 33 57 23 38 39 23 46 80 23 51 93 23 57 58 23 61 18 23 89 42 23 96 67 23 98 95 23 99 47 24 17 62 24 26 10 24 46 20 24 55 11 24 69 52 24 70 79 24 75 23 24 76 82 24 78 39 24 86 49 24 90 85 24 91 56 25 2 48 25 14 76 25 22 63 25 29 6 25 30 93 25 70 18 25 75 77 25 78 99 25 79 39 25 86 60 25 91 3 25 92 64 25 98 99 26 23 65 26 24 43 26 51 5 26 53 22 26 59 14 26 77 98 27 5 7 27 25 13 27 43 13 27 46 23 27 48 28 27 51 91 27 58 72 27 82 47 27 84 2 27 87 8 27 92 83 27 95 53 28 5 93 28 8 91 28 93 18 29 9 45 29 11 23 29 23 47 29 37 78 29 40 100 29 41 62 29 73 64 29 78 4 29 80 82 29 88 62 29 99 55 30 8 56 30 24 74 30 27 72 30 29 71 30 51 8 30 64 78 30 66 73 30 67 53 30 90 45 31 27 20 31 33 80 31 43 29 31 51 63 31 54 42 31 55 33 31 58 81 31 67 55 31 71 38 31 84 77 31 95 1 32 3 83 32 25 85 32 35 89 32 37 7 32 38 44 32 39 11 32 46 36 32 66 54 32 70 52 32 72 82 32 75 99 32 86 86 32 91 9 32 92 8 32 94 7 33 11 74 33 42 50 33 51 18 33 56 11 33 64 16 33 66 56 33 86 37 33 90 36 33 91 10 34 13 48 34 17 69 34 18 25 34 21 64 34 35 9 34 40 99 34 51 86 34 55 69 34 87 33 35 4 100 35 15 91 35 22 3 35 37 54 35 43 56 35 50 64 35 82 89 35 92 75 35 98 31 36 1 92 36 3 96 36 7 67 36 18 61 36 41 70 36 48 84 36 51 31 36 53 58 36 68 42 36 73 39 36 83 77 37 7 60 37 31 27 37 33 25 37 62 13 37 68 23 37 73 20 37 77 72 37 94 51 38 1 86 38 10 23 38 18 51 38 25 43 38 32 86 38 44 26 38 56 37 38 59 82 38 66 22 38 67 52 38 72 2 38 75 36 38 80 48 38 89 33 38 96 28 39 27 68 39 31 40 39 32 50 39 55 62 39 56 12 39 78 75 39 94 12 40 9 19 40 36 33 40 39 21 40 43 46 40 44 83 40 45 69 40 46 18 40 82 44 40 83 4 40 85 43 40 86 82 41 1 7 41 7 19 41 24 97 41 43 68 41 45 31 41 49 73 41 69 71 41 79 29 41 85 73 41 86 23 41 87 62 41 92 39 42 15 92 42 17 5 42 21 59 42 36 57 42 56 91 42 81 3 42 91 4 42 95 21 43 24 35 43 36 51 43 68 40 43 91 42 43 98 46 44 2 25 44 7 29 44 10 27 44 17 26 44 21 27 44 33 4 44 36 78 44 48 85 44 53 85 44 61 82 44 90 82 44 93 65 45 19 46 45 23 80 45 38 1 45 40 36 45 56 92 45 63 89 45 65 93 46 11 13 46 31 29 46 41 43 46 50 64 46 61 48 46 82 26 46 87 6 46 88 95 46 92 86 46 94 69 47 1 53 47 2 15 47 5 1 47 10 6 47 18 7 47 21 83 47 36 56 47 43 33 47 50 30 47 57 99 47 67 23 47 70 30 47 78 49 47 80 50 47 86 6 48 28 9 48 50 13 48 67 27 48 84 34 49 23 91 49 31 76 49 54 48 49 67 15 49 89 48 50 4 86 50 7 32 50 9 2 50 19 79 50 52 92 50 57 31 50 63 73 50 64 69 50 73 35 50 75 38 50 87 75 50 92 60 51 10 23 51 17 96 51 18 90 51 35 72 51 46 90 51 50 2 51 56 10 51 74 28 51 75 95 51 91 8 51 93 73 51 99 71 52 7 58 52 8 21 52 30 95 52 41 79 52 53 97 52 61 61 52 62 97 52 68 84 52 82 40 52 84 88 52 94 84 52 96 84 52 98 86 53 5 19 53 17 51 53 66 41 53 69 36 53 76 1 53 77 53 53 85 48 53 90 76 54 22 92 54 68 91 54 69 71 54 73 96 54 78 54 54 87 70 55 3 48 55 7 61 55 22 85 55 28 46 55 29 26 55 32 48 55 36 6 55 69 15 55 72 94 55 86 76 55 88 40 55 98 6 56 25 1 56 28 61 56 30 84 56 42 81 56 53 32 56 54 24 56 66 65 56 70 5 56 80 46 56 87 16 57 19 69 57 22 25 57 25 39 57 27 5 57 31 12 57 49 83 57 58 86 57 62 98 57 78 43 57 79 47 57 88 79 57 89 65 57 96 80 57 98 22 58 2 63 58 3 93 58 10 95 58 14 86 58 16 44 58 31 89 58 34 35 58 49 50 58 50 11 58 57 56 58 59 11 58 68 12 58 79 90 58 84 76 58 88 52 59 7 47 59 40 70 59 42 72 59 46 19 59 49 3 59 61 2 59 70 80 59 77 82 59 78 51 59 88 65 59 92 63 60 1 72 60 6 45 60 9 87 60 24 36 60 37 79 60 46 18 60 49 11 60 85 27 61 1 53 61 5 45 61 11 86 61 20 8 61 21 89 61 34 64 61 54 35 61 78 7 62 6 9 62 15 24 62 50 77 62 54 15 62 65 32 62 68 22 62 74 51 62 77 26 62 79 93 62 83 76 62 89 38 62 94 55 63 5 75 63 8 5 63 22 87 63 26 97 63 30 24 63 33 49 63 40 7 63 43 88 63 48 34 63 71 42 63 83 48 63 89 99 63 90 59 63 94 51 64 18 92 64 21 89 64 23 80 64 29 22 64 32 24 64 55 44 64 68 29 64 71 99 64 72 66 64 79 83 64 83 79 64 90 84 64 96 64 64 99 88 65 12 4 65 15 7 65 18 57 65 30 99 65 31 40 65 43 44 65 50 64 65 56 80 65 61 42 65 69 45 65 72 56 65 73 55 65 74 48 65 89 60 66 8 82 66 13 33 66 14 92 66 20 86 66 30 32 66 60 11 66 71 42 66 73 43 66 75 11 66 83 78 66 88 20 66 92 47 66 93 78 67 1 75 67 7 42 67 8 73 67 23 30 67 30 45 67 33 17 67 34 88 67 40 36 67 55 53 67 65 51 67 85 70 67 87 21 67 98 61 68 1 95 68 3 68 68 10 78 68 11 51 68 12 77 68 16 35 68 17 60 68 25 44 68 26 66 68 32 10 68 49 58 68 53 62 68 98 13 69 1 92 69 12 90 69 13 92 69 21 70 69 25 74 69 26 27 69 30 4 69 33 10 69 36 41 69 40 55 69 42 34 69 58 15 69 71 59 69 72 98 69 76 90 69 85 24 69 97 8 70 9 37 70 15 81 70 19 11 70 25 39 70 26 80 70 35 86 70 38 65 70 49 46 70 97 40 71 1 9 71 11 100 71 24 33 71 58 1 71 66 31 71 68 49 71 70 56 71 76 21 71 85 72 71 86 18 71 92 51 72 14 55 72 37 94 72 38 83 72 42 49 72 58 75 72 59 82 72 80 62 72 87 25 72 92 96 73 10 89 73 14 69 73 26 19 73 56 1 73 78 44 74 5 88 74 16 29 74 31 76 74 32 7 74 41 67 74 52 16 74 60 45 74 62 29 74 72 21 74 75 15 75 9 16 75 13 88 75 17 85 75 19 99 75 56 92 75 67 14 75 74 31 75 93 43 76 7 48 76 20 63 76 24 77 76 26 94 76 29 5 76 30 75 76 43 96 76 47 90 76 50 100 76 61 54 77 2 57 77 21 88 77 26 91 77 27 92 77 41 58 77 42 44 77 43 39 77 90 32 77 91 96 78 18 2 78 25 16 78 35 38 78 45 11 78 46 44 78 53 24 78 54 20 78 65 76 78 76 14 78 98 30 78 99 46 79 3 49 79 9 38 79 11 34 79 12 41 79 17 29 79 18 4 79 38 77 79 46 83 79 49 36 79 54 47 79 68 23 79 70 88 79 71 20 79 72 56 79 77 28 79 81 12 79 83 15 79 96 26 80 14 15 80 16 21 80 18 3 80 34 93 80 42 8 80 55 98 80 58 53 80 68 87 80 74 54 80 78 27 81 1 66 81 12 8 81 13 38 81 19 14 81 20 59 81 34 61 81 42 92 81 75 79 81 82 8 81 99 100 82 1 86 82 24 51 82 25 8 82 43 17 82 46 64 82 50 4 82 74 35 82 76 11 82 79 45 82 83 34 83 2 90 83 12 29 83 25 57 83 27 42 83 33 39 83 51 98 83 59 2 83 68 2 83 72 54 83 79 26 83 96 37 84 8 37 84 31 67 84 40 50 84 50 58 84 54 23 84 86 52 84 95 70 85 3 41 85 8 20 85 9 99 85 19 21 85 33 66 85 37 38 85 40 48 85 44 63 85 59 19 85 71 19 85 92 13 85 95 43 85 99 77 86 8 73 86 10 91 86 13 88 86 22 70 86 25 64 86 32 98 86 38 54 86 40 89 86 45 15 86 61 68 86 67 51 86 75 70 87 2 58 87 16 78 87 17 40 87 22 56 87 25 12 87 32 71 87 33 7 87 37 31 87 47 42 87 85 13 87 94 89 87 98 71 88 13 1 88 19 41 88 33 35 88 46 6 88 48 93 88 58 46 88 84 71 88 86 7 88 99 42 89 4 40 89 9 68 89 17 15 89 40 67 89 42 25 89 45 74 89 63 64 90 28 68 90 36 62 90 39 5 90 46 41 90 51 1 90 73 20 90 74 99 90 88 89 90 96 84 91 28 48 91 43 55 91 44 40 91 47 84 91 71 41 91 74 79 91 80 28 91 82 82 92 4 74 92 5 80 92 7 5 92 48 9 92 58 8 92 62 17 92 87 74 92 89 87 92 93 30 92 98 67 93 19 66 93 32 80 93 69 88 93 77 17 93 88 78 93 96 57 94 43 26 94 75 98 94 79 99 94 88 43 95 6 31 95 17 82 95 36 2 95 47 79 95 53 39 95 58 20 95 59 61 95 61 74 95 67 36 95 72 72 95 83 36 96 10 66 96 15 53 96 27 18 96 40 82 96 56 15 96 64 86 96 71 57 96 79 27 97 45 49 97 52 21 97 53 65 97 66 69 97 83 14 98 3 82 98 8 87 98 10 34 98 33 38 98 50 19 98 54 5 98 71 35 98 81 63 98 89 54 98 92 46 98 97 94 99 11 47 99 25 7",
                false, true);
        assertEquals(532, edmondsKarp(big.flowListsGraph, 1, 99));
    }

    @Test(timeout = 2000)
    public void testBipartiteMatching() {
        AllGraph g = makeGraph("12 "
                + "1 7 1  2 6 1  2 7 1  2 9 1  3 8 1  4 7 1  5 8 1  5 9 1  5 10 1",
                false, true, true);
        assertEquals(4, bipartiteMatching(g.listsGraph));
        g = new AllGraph(1000);
        for (int i = 0; i < 500; i++) {
            g.addEdge(new Edge(i, i * i % 500 + 500, 0, 1));
        }
        assertEquals(106, bipartiteMatching(g.listsGraph));
    }

    AllGraph standardCostFlowGraph = makeGraph("8 "
            + "0 1 3 13  0 5 2 7  1 2 3 8  2 0 9 12  2 3 3 4  3 4 1 2  3 7 3 3 "
            + "4 1 1 4  4 3 3 9  4 5 2 7  5 2 3 9  5 3 10 5  5 7 8 9  6 1 2 9  7 0 1 6",
            true, true);
    AllGraph reversingCostFlowGraph = makeGraph("4 "
            + "0 1 1 5  0 2 3 5  1 2 1 5  1 3 3 5  2 3 1 5", true, true);

    @Test(timeout = 2000)
    public void testMinCostFlowWithBellmanFord() {
        AllGraph g = standardCostFlowGraph;

        assertEquals(11, edmondsKarp(g.flowListsGraph, 0, 7));
        g.flowListsGraph.resetFlow();
        assertEquals(94, minCostFlowWithBellmanFord(g.flowListsGraph, 0, 7, 9));
        g.flowListsGraph.resetFlow();
        assertEquals(-1, minCostFlowWithBellmanFord(g.flowListsGraph, 0, 6, 6));
        g.flowListsGraph.resetFlow();

        g = reversingCostFlowGraph;

        assertEquals(12, minCostFlowWithBellmanFord(g.flowListsGraph, 0, 3, 4));
        g.flowListsGraph.resetFlow();
        assertEquals(15, minCostFlowWithBellmanFord(g.flowListsGraph, 0, 3, 5));
        g.flowListsGraph.resetFlow();
        assertEquals(20, minCostFlowWithBellmanFord(g.flowListsGraph, 0, 3, 6));
        g.flowListsGraph.resetFlow();
        assertEquals(40, minCostFlowWithBellmanFord(g.flowListsGraph, 0, 3, 10));
        g.flowListsGraph.resetFlow();
        assertEquals(-1, minCostFlowWithBellmanFord(g.flowListsGraph, 0, 3, 11));
        g.flowListsGraph.resetFlow();

        //        g = makeGraph("7 " + "0 1 45  0 2 30  1 2 10  1 3 19  2 4 60  2 5 14  3 5 8 "
        //                + "3 6 22  4 3 11  4 6 37  5 2 10  5 4 33  5 6 1  ", false, true);
        //        assertEquals(59, edmondsKarp(g.listsGraph, 0, 6));

        assertEquals(-1, minCostFlowWithBellmanFord(noedges.flowListsGraph, 0, 4, 1));
        assertEquals(0, minCostFlowWithBellmanFord(noedges.flowListsGraph, 0, 4, 0));

        AllGraph multiples = makeGraph("2 " + "0 1 1 10  0 1 2 10 0 1 3 20  ", true, true);
        assertEquals(60, minCostFlowWithBellmanFord(multiples.flowListsGraph, 0, 1, 30));


        // 0:s, 11:t, 1~5, 6~10: 二部グラフ
        //        AllGraph bipartite = makeGraph("12 "
        //                + "1 7 1  2 6 1  2 7 1  2 9 1  3 8 1  4 7 1  5 8 1  5 9 1  5 10 1",
        //                false, true);
        //        for (int i = 0; i < 5; i++) {
        //            bipartite.addArc(new Edge(0, i + 1, 0, 1));
        //            bipartite.addArc(new Edge(i + 6, 11, 0, 1));
        //        }
        //        assertEquals(4, edmondsKarp(bipartite.listsGraph, 0, 11));
    }

    @Test(timeout = 2000)
    public void testMinCostFlowWithPrimalDual() {
        AllGraph g = standardCostFlowGraph;

        assertEquals(94, minCostFlowWithPrimalDual(g.flowListsGraph, 0, 7, 9));
        g.flowListsGraph.resetFlow();
        assertEquals(-1, minCostFlowWithPrimalDual(g.flowListsGraph, 0, 6, 6));
        g.flowListsGraph.resetFlow();

        g = reversingCostFlowGraph;

        assertEquals(12, minCostFlowWithPrimalDual(g.flowListsGraph, 0, 3, 4));
        g.flowListsGraph.resetFlow();
        assertEquals(15, minCostFlowWithPrimalDual(g.flowListsGraph, 0, 3, 5));
        g.flowListsGraph.resetFlow();
        assertEquals(20, minCostFlowWithPrimalDual(g.flowListsGraph, 0, 3, 6));
        g.flowListsGraph.resetFlow();
        assertEquals(40, minCostFlowWithPrimalDual(g.flowListsGraph, 0, 3, 10));
        g.flowListsGraph.resetFlow();
        assertEquals(-1, minCostFlowWithPrimalDual(g.flowListsGraph, 0, 3, 11));
        g.flowListsGraph.resetFlow();

        //        g = makeGraph("7 " + "0 1 45  0 2 30  1 2 10  1 3 19  2 4 60  2 5 14  3 5 8 "
        //                + "3 6 22  4 3 11  4 6 37  5 2 10  5 4 33  5 6 1  ", false, true);
        //        assertEquals(59, edmondsKarp(g.listsGraph, 0, 6));

        assertEquals(-1, minCostFlowWithPrimalDual(noedges.flowListsGraph, 0, 4, 1));
        assertEquals(0, minCostFlowWithPrimalDual(noedges.flowListsGraph, 0, 4, 0));

        AllGraph multiples = makeGraph("2 " + "0 1 1 10  0 1 2 10 0 1 3 20  ", true, true);
        assertEquals(60, minCostFlowWithPrimalDual(multiples.flowListsGraph, 0, 1, 30));
    }

    @Test(timeout = 2000)
    public void testDecomposeIntoStronglyConnectedComponents() {
        int[] components = new int[standardWaightedGraph.n];
        assertEquals(2, decomposeIntoStronglyConnectedComponents(
                standardWaightedGraph.listsGraph, components));
        assertArrayEquals(new int[] { 1, 1, 1, 1, 1, 1, 0, 1 }, components);

        StronglyConnectedComponents scc = new StronglyConnectedComponents(
                standardWaightedGraph.listsGraph);
        assertEquals(2, scc.components.size());
        assertArrayEquals(new int[] { 1, 1, 1, 1, 1, 1, 0, 1 }, scc.topologicalOrder);


        AllGraph straightTree = makeGraph("6  0 1  1 2  2 0  3 0  2 4  4 5", false, false);
        components = new int[6];
        assertEquals(4, decomposeIntoStronglyConnectedComponents(straightTree.listsGraph,
                components));
        assertArrayEquals(new int[] { 1, 1, 1, 0, 2, 3 }, components);

        scc = new StronglyConnectedComponents(straightTree.listsGraph);
        assertEquals(4, scc.components.size());
        assertArrayEquals(new int[] { 1, 1, 1, 0, 2, 3 }, scc.topologicalOrder);

        AllGraph smallForest = makeGraph("3  0 1", false, false);
        components = new int[3];
        assertEquals(3, decomposeIntoStronglyConnectedComponents(smallForest.listsGraph,
                components));
        assertTrue(components[0] < components[1]);
        assertTrue(components[0] != components[2]);
        assertTrue(components[1] != components[2]);

        scc = new StronglyConnectedComponents(smallForest.listsGraph);
        assertEquals(3, scc.components.size());
        assertTrue(scc.topologicalOrder[0] < scc.topologicalOrder[1]);
        assertTrue(scc.topologicalOrder[0] != scc.topologicalOrder[2]);
        assertTrue(scc.topologicalOrder[1] != scc.topologicalOrder[2]);
    }

    @Test(timeout = 2000)
    public void testViterbi() {
        AllGraph aDag = makeGraph("7  5 3 7  3 1 3  3 2 6  1 2 1  1 6 3  2 4 4  2 0 8  "
                + "6 4 1  6 0 2  4 0 9", true, false);
        int[] components = new int[] { 6, 2, 3, 1, 5, 0, 4 };
        assertEquals(4, viterbi(aDag.listsGraph, 3, 2, components));
        assertEquals(8, viterbi(aDag.listsGraph, 3, 0, components));
        assertEquals(INF, viterbi(aDag.listsGraph, 3, 5, components));
        assertEquals(15, viterbi(aDag.listsGraph, 5, 0, components));
        assertEquals(8, viterbi(aDag.listsGraph, 2, 0, components));
        assertEquals(14, viterbi(aDag.listsGraph, 5, 4, components));

        AllGraph aDisConnectedDag = makeGraph("8  3 4 12  4 5 28  4 7 46  5 7 21 "
                + "0 1 30  0 2 91  1 2 39", true, false);
        components = new int[] { 4, 5, 6, 1, 2, 3, 0, 7 };
        assertEquals(INF, viterbi(aDisConnectedDag.listsGraph, 6, 7, components));
        assertEquals(INF, viterbi(aDisConnectedDag.listsGraph, 4, 1, components));
        assertEquals(INF, viterbi(aDisConnectedDag.listsGraph, 1, 5, components));
        assertEquals(58, viterbi(aDisConnectedDag.listsGraph, 3, 7, components));
        assertEquals(69, viterbi(aDisConnectedDag.listsGraph, 0, 2, components));
        assertEquals(INF, viterbi(aDisConnectedDag.listsGraph, 2, 0, components));
    }

    @Test(timeout = 8000)
    @Ignore("重いので全体テストでは無視")
    public void testBigRandomGraph() {
        AllGraph g = new AllGraph(1000);
        Random random = new Random(42);
        for (int i = 0; i < 10000; i++) {
            int from = random.nextInt(1000);
            int to = random.nextInt(999);
            if (from == to) to = 999;
            if (g.matrixGraph.edges[from][to] != null) continue;
            Edge e = new Edge(from, to, random.nextInt(1000), random.nextInt(1000));
            g.addArc(e);
        }
        // System.out.println(generateGraphViz(g.matrixGraph));

        assertEquals(730, dijkstra(g.listsGraph, 0, 500));
        assertEquals(730, dijkstra(g.matrixGraph, 0, 500));
        assertEquals(888, dijkstra(g.matrixGraph, 777, 444));
        assertEquals(888, bellmanFord(g.listsGraph, 777)[444]);
        // assertEquals(888, bellmanFord(g.matrixGraph, 777)[444]); // TLE
        assertEquals(997, dijkstra(g.listsGraph, 256, 8));
        assertEquals(56947, kruskal(g.listGraph));
        // assertEquals(56947, kruskal(g.matrixGraph)); // undirected制約によりムリ
        assertEquals(4166, edmondsKarp(g.flowListsGraph, 0, 1));

        ListsGraph lg = new ListsGraph(100000);
        FlowListsGraph flg = new FlowListsGraph(100000);
        for (int i = 0; i < 300000; i++) {
            int from = random.nextInt(100000);
            int to = random.nextInt(100000 - 1);
            if (from == to) to = 100000 - 1;
            Edge e = new Edge(from, to, random.nextInt(1000), random.nextInt(1000));
            lg.addArc(e);
            flg.addArc(e);
        }
        assertEquals(4022, dijkstra(lg, 100, 10000));
        assertEquals(INF, dijkstra(lg, 74747, 4747));
        assertEquals(157, edmondsKarp(flg, 0, 99999));
        assertEquals(1065, edmondsKarp(flg, 65536, 128));
        assertEquals(0, edmondsKarp(flg, 1234, 5678));
    }
}
