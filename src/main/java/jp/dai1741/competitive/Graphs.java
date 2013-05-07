package jp.dai1741.competitive;

import jp.dai1741.competitive.DataStructures.UnionFind;

import java.awt.geom.IllegalPathStateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Graphs {
    public static final int INF = Integer.MAX_VALUE / 16;

    /*
     * グラフを表す構造体。nはノード数
     */

    static class MatrixGraph {
        final int n;
        Edge[][] edges;

        public MatrixGraph(int n) {
            this.n = n;
            edges = new Edge[n][n];
        }

        void addArc(Edge e) {
            // arcとedgeで有向・無向を区別してみたがあまりよろしくない
            edges[e.from][e.to] = e;  // すでに追加済みの枝は上書きされるので注意
        }

        void addEdge(Edge e) {
            edges[e.from][e.to] = edges[e.to][e.from] = e;
        }
    }

    static class AdjGraph {
        final int n;
        ArrayList<Edge>[] edges;

        @SuppressWarnings("unchecked")
        public AdjGraph(int n) {
            this.n = n;
            edges = new ArrayList[n];
            for (int v = 0; v < n; v++) {
                edges[v] = new ArrayList<Edge>();
            }
        }

        void addArc(Edge e) {
            edges[e.from].add(e);
        }

        void addEdge(Edge e) {
            edges[e.from].add(e);
            edges[e.to].add(e.getInv());
        }
    }

    static class EdgeGraph {
        int n = 0;
        ArrayList<Edge> edges;

        public EdgeGraph(int n) {
            this.n = n;
            edges = new ArrayList<Edge>();
        }

        void addArc(Edge e) {
            n = Math.max(n, Math.max(e.from, e.to) + 1);
            edges.add(e);
        }

        void addEdge(Edge e) {
            n = Math.max(n, Math.max(e.from, e.to) + 1);
            edges.add(e);
            edges.add(e.getInv());
        }
    }

    static class Edge {
        final int from, to, cost, capacity;

        public Edge(int from, int to, int cost) {
            this(from, to, cost, 0);
        }

        public Edge(int from, int to, int cost, int capacity) {
            this.from = from;
            this.to = to;
            this.cost = cost;
            this.capacity = capacity;
        }

        Edge getInv() {
            return new Edge(to, from, cost, capacity);
        }
    }

    /*
     * Dijkstra
     * 
     * @see プログラミングコンテストチャレンジブック 第1版 p.96
     */

    static class EdgeState implements Comparable<EdgeState> {
        int cost, n;

        public EdgeState(int cost, int n) {
            this.cost = cost;
            this.n = n;
        }

        @Override
        public int compareTo(EdgeState o) {
            return cost - o.cost;
        }
    }

    static int[] dijkstra(MatrixGraph g, int s) {
        return dijkstraSub(g, s, -1, null);
    }

    static int[] dijkstraWithPath(MatrixGraph g, int s, int[] prev) {
        Arrays.fill(prev, -1);
        return dijkstraSub(g, s, -1, prev);
    }

    static int dijkstra(MatrixGraph g, int s, int t) {
        return dijkstraSub(g, s, t, null)[t];
    }

    static int[] dijkstraSub(MatrixGraph g, int s, int t, int[] prev) {
        int[] dists = new int[g.n];  // 距離
        Arrays.fill(dists, INF);
        dists[s] = 0;

        PriorityQueue<EdgeState> queue = new PriorityQueue<EdgeState>();
        queue.add(new EdgeState(0, s));
        while (!queue.isEmpty()) {
            EdgeState state = queue.poll();
            if (dists[state.n] < state.cost) continue;
            if (state.n == t) break;
            for (int v = 0; v < g.n; v++) {
                Edge e = g.edges[state.n][v];
                if (e != null && dists[v] > dists[state.n] + e.cost) {
                    dists[v] = dists[state.n] + e.cost;
                    if (prev != null) prev[v] = state.n;
                    queue.add(new EdgeState(dists[v], v));
                }
            }
        }
        return dists;
    }

    static int[] dijkstra(AdjGraph g, int s) {
        return dijkstraSub(g, s, -1, null);
    }

    static int[] dijkstraWithPath(AdjGraph g, int s, int[] prev) {
        Arrays.fill(prev, -1);
        return dijkstraSub(g, s, -1, prev);
    }

    static int dijkstra(AdjGraph g, int s, int t) {
        return dijkstraSub(g, s, t, null)[t];
    }

    static int[] dijkstraSub(AdjGraph g, int s, int t, int[] prev) {
        int[] dists = new int[g.n];
        Arrays.fill(dists, INF);
        dists[s] = 0;

        PriorityQueue<EdgeState> queue = new PriorityQueue<EdgeState>();
        queue.add(new EdgeState(0, s));
        while (!queue.isEmpty()) {
            EdgeState state = queue.poll();
            if (dists[state.n] < state.cost) continue;
            if (state.n == t) break;
            for (Edge e : g.edges[state.n]) {
                if (dists[e.to] > dists[state.n] + e.cost) {
                    dists[e.to] = dists[state.n] + e.cost;
                    if (prev != null) prev[e.to] = state.n;
                    queue.add(new EdgeState(dists[e.to], e.to));
                }
            }
        }
        return dists;
    }

    /*
     * ベルマンフォード法
     * 
     * @see プログラミングコンテストチャレンジブック 第1版 p.95
     */

    /**
     * @param g
     * @param s 始点
     * @return
     * @throws IllegalPathStateException 負の閉路を検出した場合
     */
    static int[] bellmanFord(MatrixGraph g, int s) {
        int[] dists = new int[g.n];
        Arrays.fill(dists, INF);
        dists[s] = 0;
        for (int i = 0; i < g.n; i++) {
            for (int u = 0; u < g.n; u++) {
                for (int v = 0; v < g.n; v++) {
                    Edge e = g.edges[u][v];
                    if (e != null && dists[u] != INF && dists[v] > dists[u] + e.cost) {
                        dists[v] = dists[u] + e.cost;
                        if (i == g.n - 1) throw new IllegalPathStateException(
                                "negative loop exists");  // この例外の使い方は多分Illegal
                    }
                }
            }
        }
        return dists;
    }

    static int[] bellmanFord(AdjGraph g, int s) {
        int[] dists = new int[g.n];
        Arrays.fill(dists, INF);
        dists[s] = 0;
        for (int i = 0; i < g.n; i++) {
            for (int v = 0; v < g.n; v++) {
                for (Edge e : g.edges[v]) {
                    if (dists[v] != INF && dists[e.to] > dists[v] + e.cost) {
                        dists[e.to] = dists[v] + e.cost;
                        if (i == g.n - 1) throw new IllegalPathStateException(
                                "negative loop exists");
                    }
                }
            }
        }
        return dists;
    }

    static int[] bellmanFord(EdgeGraph g, int s) {
        int[] dists = new int[g.n];
        Arrays.fill(dists, INF);
        dists[s] = 0;
        for (int i = 0; i < g.n; i++) {
            for (Edge e : g.edges) {
                int from = e.from;
                if (dists[from] != INF && dists[e.to] > dists[from] + e.cost) {
                    dists[e.to] = dists[from] + e.cost;
                    if (i == g.n - 1) throw new IllegalPathStateException(
                            "negative loop exists");
                }
            }
        }
        return dists;
    }


    /*
     * ワーシャルフロイド法
     * 
     * @see プログラミングコンテストチャレンジブック 第1版 p.98
     */

    static int[][] warshallFloyd(MatrixGraph g) {
        int[][] dists = makeDistsArray(g);
        warshallFloyd(dists);
        return dists;
    }

    static int[][] makeDistsArray(MatrixGraph g) {
        int[][] dists = new int[g.n][g.n];
        for (int u = 0; u < g.n; u++) {
            for (int v = 0; v < g.n; v++) {
                if (u != v) {
                    Edge e = g.edges[u][v];
                    dists[u][v] = e == null ? INF : e.cost;
                }
            }
        }
        return dists;
    }

    static int[][] warshallFloyd(AdjGraph g) {
        int[][] dists = makeDistsArray(g);
        warshallFloyd(dists);
        return dists;
    }

    static int[][] makeDistsArray(AdjGraph g) {
        int[][] dists = new int[g.n][g.n];
        for (int i = 0; i < g.n; i++) {
            Arrays.fill(dists[i], INF);
            dists[i][i] = 0;
            for (Edge e : g.edges[i]) {
                dists[i][e.to] = e.cost;
            }
        }
        warshallFloyd(dists);
        return dists;
    }

    static void warshallFloyd(int[][] dists) {
        int n = dists.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    if (dists[j][i] != INF && dists[i][k] != INF) dists[j][k] = Math.min(
                            dists[j][k], dists[j][i] + dists[i][k]);
                }
            }
        }
    }

    /**
     * @param dists
     * @param path 途中経路を保持しておくための配列。
     *            path[i][j]は、iからjへ行くためにiの次に通る点となる
     * @see http://www.is.doshisha.ac.jp/report/2004/4/8/20040716001/index.html
     */
    static void warshallFloydWithPath(int[][] dists, int[][] path) {
        int n = dists.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                path[i][j] = j;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    if (dists[j][i] != INF && dists[i][k] != INF
                            && dists[j][k] > dists[j][i] + dists[i][k]) {
                        dists[j][k] = dists[j][i] + dists[i][k];
                        path[j][k] = path[j][i];
                    }
                }
            }
        }
        // iからjへの道がないときにpath[i][j]がINFとなるようにするためには以下のようにする
        //        for (int i = 0; i < n; i++)
        //            for (int j = 0; j < n; j++)
        //                if (dists[i][j] == INF) path[i][j] = INF;
    }

    /**
     * 単一始点の最短経路を復元する。
     * 
     * @param prev {@code from}からその点へ行くための直前の経路
     * @param from
     * @param to
     * @return the path
     * @throws IllegalStateException if theres no path from {@code from} to {@code to}
     * @see プログラミングコンテストチャレンジブック 第1版 p.98
     */
    static ArrayList<Integer> getPath(int[] prev, int from, int to) {
        ArrayList<Integer> path = new ArrayList<Integer>();
        for (int v = to;; v = prev[v]) {
            if (v == -1) throw new IllegalStateException();
            path.add(v);
            if (v == from) break;
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * 全点間の最短経路を復元する。
     * 経路がないときは不正な値を返すので注意。
     * 
     * @param paths path array
     * @param from
     * @param to
     * @return path list
     */
    static ArrayList<Integer> getPath(int[][] paths, int from, int to) {
        ArrayList<Integer> path = new ArrayList<Integer>();
        for (int v = from;; v = paths[v][to]) {
            path.add(v);
            if (v == to) break;
            // if (v == INF) return null;  // 道がない場合
        }
        return path;
    }


    /*
     * 無向最小全域木（最小全域森）
     */

    /**
     * @return 無向最小全域森のコスト
     * @see プログラミングコンテストチャレンジブック 第1版 p.101
     */
    static int kruskal(EdgeGraph g) {
        List<Edge> edges = new ArrayList<Edge>(g.edges);
        Collections.sort(edges, new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                return o1.cost - o2.cost;
            }
        });
        UnionFind uf = new UnionFind(g.n);
        int ret = 0;
        for (Edge e : edges) {
            if (!uf.same(e.from, e.to)) {
                uf.unite(e.from, e.to);
                ret += e.cost;
            }
        }
        return ret;
    }

    /*
     * 最大流
     * 
     * 各関数は引数のグラフを書き換える（残余グラフが残る）ので注意。
     */

    /**
     * 逆辺をもった隣接リスト表現のネットワークグラフ。
     */
    static class FlowAdjGraph {
        final int numNodes;
        final ArrayList<ResidualEdge>[] edges;

        @SuppressWarnings("unchecked")
        public FlowAdjGraph(int n) {
            numNodes = n;
            edges = new ArrayList[n];
            for (int v = 0; v < n; v++) {
                edges[v] = new ArrayList<ResidualEdge>();
            }
        }

        void add(int from, int to, int cost, int capacity) {
            assert capacity >= 0;
            ResidualEdge e1 = new ResidualEdge(from, to, cost, capacity);
            ResidualEdge e2 = new ResidualEdge(to, from, -cost, 0);
            e1.reversedEdge = e2;
            e2.reversedEdge = e1;
            edges[from].add(e1);
            edges[to].add(e2);
        }

        void addArc(Edge e) {
            add(e.from, e.to, e.cost, e.capacity);
        }

        void addEdge(Edge e) {
            add(e.from, e.to, e.cost, e.capacity);
            add(e.to, e.from, e.cost, e.capacity);
            // 求めるものがコストフローでなければ、2辺をそれぞれの逆辺としてもよい
        }

        /** Resets flow */
        void resetFlow() {
            for (ArrayList<ResidualEdge> edge : edges) {
                for (ResidualEdge e : edge) {
                    e.residual = e.capacity;
                }
            }
        }
    }

    static class ResidualEdge {
        final int from, to, cost, capacity;
        int residual;
        ResidualEdge reversedEdge; // 逆辺

        public ResidualEdge(int from, int to, int cost, int capacity) {
            this.from = from;
            this.to = to;
            this.cost = cost;
            this.capacity = this.residual = capacity;
        }
    }

    static int edmondsKarp(FlowAdjGraph g, int s, int t) {
        int flow = 0;
        while (true) {
            ArrayList<ResidualEdge> path = findAugmentedPath(g.edges, s, t);
            if (path == null) break;
            int augedFlow = Integer.MAX_VALUE;
            for (ResidualEdge e : path) {
                augedFlow = Math.min(augedFlow, e.residual);
            }
            for (ResidualEdge e : path) {
                e.residual -= augedFlow;
                e.reversedEdge.residual += augedFlow;
            }
            flow += augedFlow;
        }
        return flow;
    }

    /**
     * @param edges
     * @param s
     * @param t
     * @return 増加道を構成する辺のリスト
     */
    private static ArrayList<ResidualEdge> findAugmentedPath(
            ArrayList<ResidualEdge>[] edges, int s, int t) {
        int n = edges.length;
        ResidualEdge[] prevE = new ResidualEdge[n];
        int[] prevV = new int[n];
        Arrays.fill(prevV, -1);
        Queue<Integer> queue = new LinkedList<Integer>();  // Java6ならArrayDequeにする
        queue.add(s);
        prevV[s] = -2;  // make sure source wont be rediscovered
        search: while (!queue.isEmpty()) {
            int v = queue.poll();
            for (ResidualEdge e : edges[v]) {
                if (e.residual > 0 && prevV[e.to] == -1) {
                    prevE[e.to] = e;
                    prevV[e.to] = v;
                    if (e.to == t) break search;
                    queue.add(e.to);
                }
            }
        }
        if (prevE[t] == null) return null;
        ArrayList<ResidualEdge> path = new ArrayList<ResidualEdge>();
        ResidualEdge e;
        for (int v = t;; v = prevV[v]) {
            e = prevE[v];
            if (e == null) break;
            path.add(e);  // 効率のためここで増加フローも計算したほうがよい
        }
        return path;
    }

    /**
     * @param g 無向二部グラフ。各枝の重みは1として扱われる
     * @see プログラミングコンテストチャレンジブック 第1版 p.196
     */
    static int bipartiteMatching(AdjGraph g) {
        boolean[] used = new boolean[g.n];
        int[] match = new int[g.n];
        int ret = 0;
        Arrays.fill(match, -1);
        for (int v = 0; v < g.n; v++) {
            if (match[v] < 0) {
                Arrays.fill(used, false);
                if (findABipartiteMatching(g, v, match, used)) ret++;
            }
        }
        return ret;
    }

    private static boolean findABipartiteMatching(AdjGraph g, int v, int[] match,
            boolean[] used) {
        used[v] = true;
        for (Edge e : g.edges[v]) {
            int u = e.to;
            int w = match[u];
            if (w == -1 || !used[w] && findABipartiteMatching(g, w, match, used)) {
                match[v] = u;
                match[u] = v;
                return true;
            }
        }
        return false;
    }


    /*
     * 最小費用流
     */
    /**
     * @param g グラフ
     * @param s 始点
     * @param t 終点
     * @param f 流量
     * @return 条件を満たす最小コスト。指定量の流量が流れなければ-1
     * @see プログラミングコンテストチャレンジブック 第1版 p.202
     */
    static int minCostFlowWithPrimalDual(FlowAdjGraph g, int s, int t, int f) {
        int[] lastShortestDists = new int[g.numNodes];
        int minCost = 0;
        while (f > 0) {
            ArrayList<ResidualEdge> path = minCostFlowWithPrimalDualSub(g.edges,
                    lastShortestDists, s, t);
            if (path == null) return -1;
            int augedFlow = f;
            for (ResidualEdge e : path) {
                augedFlow = Math.min(augedFlow, e.residual);
            }
            for (ResidualEdge e : path) {
                e.residual -= augedFlow;
                e.reversedEdge.residual += augedFlow;
                minCost += e.cost * augedFlow;
            }
            f -= augedFlow;
        }
        return minCost;
    }

    private static ArrayList<ResidualEdge> minCostFlowWithPrimalDualSub(
            ArrayList<ResidualEdge>[] edges, int[] lastShortestDists, int s, int t) {
        int n = edges.length;
        ResidualEdge[] prevE = new ResidualEdge[n];
        int[] prevV = new int[n];
        int[] dist = new int[n];
        Arrays.fill(prevV, -1);
        Arrays.fill(dist, INF);
        dist[s] = 0;
        prevV[s] = -2;  // make sure source is not rediscovered
        PriorityQueue<EdgeState> queue = new PriorityQueue<EdgeState>();
        queue.add(new EdgeState(0, s));

        while (!queue.isEmpty()) {
            EdgeState state = queue.poll();
            int v = state.n;
            if (dist[v] < state.cost) continue;
            for (ResidualEdge e : edges[v]) {
                if (e.residual > 0
                        && dist[e.to] > dist[v] + e.cost + lastShortestDists[v]
                                - lastShortestDists[e.to]) {
                    dist[e.to] = dist[v] + e.cost + lastShortestDists[v]
                            - lastShortestDists[e.to];
                    prevE[e.to] = e;
                    prevV[e.to] = v;
                    queue.add(new EdgeState(dist[e.to], e.to));
                }
            }
        }
        if (prevE[t] == null) return null;
        for (int v = 0; v < n; v++) {
            lastShortestDists[v] += dist[v];
        }
        ArrayList<ResidualEdge> path = new ArrayList<ResidualEdge>();
        ResidualEdge e;
        for (int v = t;; v = prevV[v]) {
            e = prevE[v];
            if (e == null) break;
            path.add(e);
        }
        return path;
    }

    /**
     * @see プログラミングコンテストチャレンジブック 第1版 p.199
     */
    static int minCostFlowWithBellmanFord(FlowAdjGraph g, int s, int t, int f) {
        int minCost = 0;
        while (f > 0) {
            ArrayList<ResidualEdge> path = minCostFlowWithBellmanFordSub(g.edges, s, t);
            if (path == null) return -1;  // failed!
            int augedFlow = f;
            for (ResidualEdge e : path) {
                augedFlow = Math.min(augedFlow, e.residual);
            }
            for (ResidualEdge e : path) {
                e.residual -= augedFlow;
                e.reversedEdge.residual += augedFlow;
                minCost += e.cost * augedFlow;
            }
            f -= augedFlow;
        }
        return minCost;
    }

    private static ArrayList<ResidualEdge> minCostFlowWithBellmanFordSub(
            ArrayList<ResidualEdge>[] edges, int s, int t) {
        int n = edges.length;
        ResidualEdge[] prevE = new ResidualEdge[n];
        int[] prevV = new int[n];
        int[] dist = new int[n];
        Arrays.fill(prevV, -1);
        Arrays.fill(dist, INF);
        dist[s] = 0;
        prevV[s] = -2;  // make sure source is not rediscovered
        for (int i = 0; i < n; i++) {
            for (int v = 0; v < n; v++) {
                for (ResidualEdge e : edges[v]) {
                    if (e.residual > 0 && dist[e.to] > dist[v] + e.cost) {
                        dist[e.to] = dist[v] + e.cost;
                        prevE[e.to] = e;
                        prevV[e.to] = v;
                    }
                }
            }
        }
        if (prevE[t] == null) return null;
        ArrayList<ResidualEdge> path = new ArrayList<ResidualEdge>();
        ResidualEdge e;
        for (int v = t;; v = prevV[v]) {
            e = prevE[v];
            if (e == null) break;
            path.add(e);
        }
        return path;
    }

    /*
     * 強連結成分分解
     */

    /**
     * O(V+E)
     * 
     * @param g
     * @param components i番目の要素に頂点iのトポロジカル順序が入る
     * @return 強連結成分数
     * @see プログラミングコンテストチャレンジブック 第1版 p.267
     */
    static int decomposeIntoStronglyConnectedComponents(AdjGraph g, int[] components) {
        int n = g.n;
        // 逆グラフを作る
        AdjGraph reversed = new AdjGraph(n);
        for (int v = 0; v < n; v++) {
            for (Edge e : g.edges[v]) {
                reversed.edges[e.to].add(e.getInv());
            }
        }

        boolean[] used = new boolean[n];
        ArrayList<Integer> forwardOrder = new ArrayList<Integer>(n);
        for (int v = 0; v < n; v++) {
            if (!used[v]) visitSccForward(g, v, used, forwardOrder);
        }
        Arrays.fill(used, false);
        int k = 0;
        Collections.reverse(forwardOrder);
        for (int v : forwardOrder) {
            if (!used[v]) visitSccBackward(reversed, v, used, k++, components);
        }
        return k;
    }

    private static void visitSccForward(AdjGraph g, int v, boolean[] used,
            ArrayList<Integer> forwardOrder) {
        used[v] = true;
        for (Edge e : g.edges[v]) {
            if (!used[e.to]) visitSccForward(g, e.to, used, forwardOrder);
        }
        forwardOrder.add(v);
    }

    /**
     * @param g 逆グラフ
     * @param v
     * @param used
     * @param cId
     * @param components
     */
    private static void visitSccBackward(AdjGraph g, int v, boolean[] used, int cId,
            int[] components) {
        used[v] = true;
        components[v] = cId;
        for (Edge e : g.edges[v]) {
            if (!used[e.to]) visitSccBackward(g, e.to, used, cId, components);
        }
    }

    /**
     * @param g
     * @see http://www.prefield.com/algorithm/graph/strongly_connected_components.html
     * @see http://hos.ac/slides/20110504_graph.pdf
     */
    static class StronglyConnectedComponents {
        int[] order;    // order[v]:   DFSでvを訪れた順序
        int[] lowlink;  // lowlink[v]: vから後退辺を1度だけ使って辿れる頂点のorderの最小値
        int curOrder;
        ArrayList<Integer> vStack;  // 連結成分の候補の頂点のスタック
        boolean[] inS;  // 頂点vがvStackに入っているならinS[v]==true
        ArrayList<ArrayList<Integer>> components;  // components[i]: i個目の強連結成分に含まれる頂点集合
        int[] topologicalOrder;  // topologicalOrder[v]: 頂点vのトポロジカル順序

        StronglyConnectedComponents(AdjGraph g) {
            order = new int[g.n];
            Arrays.fill(order, -1);  // 訪問済みでないならorder[v]==-1
            lowlink = new int[g.n];
            components = new ArrayList<ArrayList<Integer>>();
            vStack = new ArrayList<Integer>(g.n);
            inS = new boolean[g.n];

            for (int v = 0; v < g.n; v++) {
                if (order[v] == -1) dfs(g, v);
            }
            Collections.reverse(components);

            // てきとーにintにくるむ
            topologicalOrder = new int[g.n];
            for (int topoOrd = 0; topoOrd < components.size(); topoOrd++) {
                for (int v : components.get(topoOrd)) {
                    topologicalOrder[v] = topoOrd;
                }
            }
        }

        private void dfs(AdjGraph g, int v) {
            order[v] = lowlink[v] = curOrder++;  // 点vのorderを確定し、lowlinkの候補を代入
            vStack.add(v);
            inS[v] = true;
            for (Edge e : g.edges[v]) {
                if (order[e.to] == -1) {  // e.toが未訪問。このときeは前進辺
                    dfs(g, e.to);
                    lowlink[v] = Math.min(lowlink[v], lowlink[e.to]);
                    // eは前進辺なので、頂点e.toから後退辺1つで辿れる頂点はvからも同等の条件で辿れる
                }
                else if (inS[e.to]) {  // e.toが訪問済みで、vStack内にある。このときeは後退辺
                    lowlink[v] = Math.min(lowlink[v], order[e.to]);
                    // lowlinkの定義通りord[e.to]がlowlink[v]の候補となる
                }
                // それ以外の場合は、こちらから辿れるが向こうからはこちらに来れない→連結成分でない
            }
            if (order[v] == lowlink[v]) {  // vから後ろに戻れない→vから辿れる連結成分が確定
                ArrayList<Integer> cs = new ArrayList<Integer>();
                while (true) {
                    int u = vStack.remove(vStack.size() - 1);
                    cs.add(u);
                    inS[u] = false;
                    if (v == u) break;
                }
                components.add(cs);
            }
        }
    }

    /**
     * O(V+E)
     * 
     * ビタビアルゴリズムは最尤系列推定に使われるアルゴリズムだけど、
     * 某IME本ではその一般形の有向非循環グラフの最短経路問題にも当てはめていた。
     * ここでもそれにならって関数名をviterbiにしたけど、一般的ではなさそう。
     * 
     * @param topologicalOrder トポロジカル順序。配列は[0...n]の置換であること。
     * @return 最短距離
     */
    static int viterbi(AdjGraph g, int s, int t, int[] topologicalOrder) {
        int sOrder = topologicalOrder[s];
        int[] dists = new int[g.n];
        Arrays.fill(dists, INF);
        dists[s] = 0;

        int[] rank = new int[g.n];
        for (int i = 0; i < g.n; i++)
            rank[topologicalOrder[i]] = i;

        for (int ord = sOrder; ord < g.n; ord++) {
            int v = rank[ord];
            if (dists[v] < INF) for (Edge e : g.edges[v])
                dists[e.to] = Math.min(dists[e.to], dists[v] + e.cost);
        }
        return dists[t];
    }
}
