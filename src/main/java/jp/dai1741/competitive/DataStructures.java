package jp.dai1741.competitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;


public class DataStructures {


    /**
     * @see プログラミングコンテストチャレンジブック 第1版 p.159
     */
    public static class BinaryIndexedTree {
        final int[] data;
        final int n;

        public BinaryIndexedTree(int n) {
            this.n = n;
            data = new int[n + 1];
        }

        int sum(int i) {
            checkRange(i);
            int s = 0;
            while (i > 0) {
                s += data[i];
                i -= i & -i;
            }
            return s;
        }

        private void checkRange(int i) {
            if (i < 1 || n < i) throw new IllegalArgumentException();
        }

        void add(int i, int x) {
            checkRange(i);
            while (i <= n) {
                data[i] += x;
                i += i & -i;
            }
        }
    }

    /**
     * @see プログラミングコンテストチャレンジブック 第1版 p.165
     */
    public static class RangeAddableBinaryIndexedTree {
        final BinaryIndexedTree bit0, bit1;
        final int n;

        public RangeAddableBinaryIndexedTree(int n) {
            this.n = n;
            bit0 = new BinaryIndexedTree(n);
            bit1 = new BinaryIndexedTree(n);
        }

        int sum(int i) {
            return bit1.sum(i) * i + bit0.sum(i);
        }

        void add(int i, int x) {
            bit0.add(i, x);
        }

        void addRange(int l, int r, int x) {
            bit1.add(l, x);  // l以降のとき一様に増加
            bit0.add(l, -x * (l - 1));  // 上文で[1,l)のときも加算しているので相殺
            if (r != n) {
                bit1.add(r + 1, -x);  // r超のとき一様増加を止める
                bit0.add(r + 1, x * r);  // 上文で止めた増加分をまとめて加算
            }
        }
    }

    public static class BinaryIndexedTree2D {
        final BinaryIndexedTree[] data;
        final int w;
        final int h;

        public BinaryIndexedTree2D(int w, int h) {
            this.w = w;
            this.h = h;
            data = new BinaryIndexedTree[h + 1];
            for (int i = 1; i <= h; i++) {
                data[i] = new BinaryIndexedTree(w);
            }
        }

        int sum(int x, int y) {
            checkRange(y);
            int s = 0;
            while (y > 0) {
                s += data[y].sum(x);
                y -= y & -y;
            }
            return s;
        }

        private void checkRange(int i) {
            if (i < 1 || h < i) throw new IllegalArgumentException();
        }

        void add(int x, int y, int v) {
            checkRange(y);
            while (y <= h) {
                data[y].add(x, v);
                y += y & -y;
            }
        }
    }

    /**
     * ランクを処理していないのでならしO(log n)
     * 
     * @see プログラミングコンテストチャレンジブック 第1版 p.81
     */
    public static class UnionFind {
        final int[] data;

        public UnionFind(int size) {
            data = new int[size];
            for (int i = 0; i < size; i++) {
                data[i] = i;
            }
        }

        int find(int x) {
            if (data[x] == x) return x;
            else return data[x] = find(data[x]);
        }

        void unite(int x, int y) {
            data[find(x)] = data[find(y)];
        }

        boolean same(int x, int y) {
            return find(x) == find(y);
        }
    }

    /**
     * @see http://www.slideshare.net/iwiwi/2-12188757
     */
    static class LazyRMQSegmentTreeNode {

        int l;
        int r;
        int size;
        // [l, r)
        int minVal = Integer.MAX_VALUE;
        LazyRMQSegmentTreeNode left;
        LazyRMQSegmentTreeNode right;


        LazyRMQSegmentTreeNode(int l, int r) {
            this.l = l;
            this.r = r;
            size = r - l;
        }

        void set(int i, int x) {
            if (l == i && size == 1) {
                minVal = x;
                return;
            }
            else if (i < l + size / 2) {
                if (left == null) left = new LazyRMQSegmentTreeNode(l, l + size / 2);
                left.set(i, x);
            }
            else {
                if (right == null) right = new LazyRMQSegmentTreeNode(l + size / 2, r);
                right.set(i, x);
            }

            minVal = Math.min(left == null ? Integer.MAX_VALUE : left.minVal,
                    right == null ? Integer.MAX_VALUE : right.minVal);
        }

        int minRange(int l, int r) {
            if (l <= this.l && this.r <= r) {
                return minVal;
            }
            int ret = Integer.MAX_VALUE;
            if (l < this.l + size / 2 && left != null) {
                ret = left.minRange(l, r);
            }
            if (this.l + size / 2 < r && right != null) {
                ret = Math.min(ret, right.minRange(l, r));
            }
            return ret;
        }
    }

    /**
     * @see プログラミングコンテストチャレンジブック 第1版 p.155
     */
    static class ArrayRMQSegmentTree {
        final int n;
        final int[] tree;


        ArrayRMQSegmentTree(int size) {
            n = 1 << 32 - Integer.numberOfLeadingZeros(size - 1);
            tree = new int[n * 2];
            Arrays.fill(tree, Integer.MAX_VALUE);
        }

        void set(int i, int x) {
            i += n - 1;
            tree[i] = x;
            do {
                i = (i - 1) / 2;
                tree[i] = Math.min(tree[i * 2 + 1], tree[i * 2 + 2]);
            } while (i > 0);
        }

        /**
         * @return minimum value in range [l,r)
         */
        int minRange(int l, int r) {
            return minRange(l, r, 0, 0, n);
        }

        /**
         * @param a 元の範囲の左端
         * @param b 元の範囲の右端
         * @param k 現在の節点の番号
         * @return minimum value in range [ max(l,a), min(r,b) )
         */
        int minRange(int a, int b, int k, int l, int r) {
            if (r <= a || b <= l) return Integer.MAX_VALUE;
            if (a <= l && r <= b) return tree[k];
            return Math.min(minRange(a, b, k * 2 + 1, l, (l + r) / 2), minRange(a, b,
                    k * 2 + 2, (l + r) / 2, r));
        }
    }

    /**
     * @see http://community.topcoder.com/tc?module=Static&d1=tutorials&d2=lowestCommonAncestor#Sparse_Table_(ST)_algorithm
     */
    static class RMQSparseTable {
        final int[][] st;

        RMQSparseTable(int[] data) {
            int n = data.length;
            int lgN = 31 - Integer.numberOfLeadingZeros(n);
            st = new int[lgN + 1][];
            st[0] = Arrays.copyOf(data, n);
            for (int i = 1; i <= lgN; i++) {
                int blockSize = 1 << i;
                st[i] = new int[n - blockSize + 1];
                for (int j = 0; j < n - blockSize + 1; j++) {
                    st[i][j] = Math.min(st[i - 1][j], st[i - 1][j + blockSize / 2]);
                }
            }
        }

        int minRange(int l, int r) {
            int i = 31 - Integer.numberOfLeadingZeros(r - l);
            return Math.min(st[i][l], st[i][r - (1 << i)]);
        }
    }

    static class BitTrie {  // used here: http://codeforces.com/contest/282/submission/3356004
        BitTrie[] children = new BitTrie[2];
        final int childBit;

        public BitTrie(int childBit) {
            this.childBit = childBit;
        }

        void insert(long v) {
            if (childBit < 0) return;
            int ch = (v & (1L << childBit)) == 0 ? 0 : 1;
            if (children[ch] == null) children[ch] = new BitTrie(childBit - 1);
            children[ch].insert(v);
        }

        long findNearest(long v) {
            int ch = (v & (1L << childBit)) == 0 ? 0 : 1;
            if (children[ch] == null) ch ^= 1;
            return (ch == 0 ? 0 : (1L << childBit))
                    + (childBit > 0 ? children[ch].findNearest(v) : 0);
        }

        boolean find(long v) {
            if (childBit < 0) return true;
            int ch = (v & (1L << childBit)) == 0 ? 0 : 1;
            if (children[ch] == null) return false;
            return children[ch].find(v);
        }
    }

    /**
     * accepted in: http://poj.org/problem?id=3580
     * 
     * @see http://www.slideshare.net/iwiwi/2-12188757
     */
    static class LazyRMQTreap {

        static class NodePair {
            LazyRMQTreap l, r;
        }

        LazyRMQTreap l, r;
        int size = 1;
        int val;
        boolean rev;
        double p;
        int min;
        int sum;
        int addingVal;

        public LazyRMQTreap(int val) {
            this.val = val;
            p = Math.random();
            update();
        }

        /** 頂点情報を更新 */
        private LazyRMQTreap update() {
            assert addingVal == 0;
            size = nodeSize(l) + nodeSize(r) + 1;
            min = Math.min(l == null ? Integer.MAX_VALUE : l.min + l.addingVal, r == null
                    ? Integer.MAX_VALUE
                    : r.min + r.addingVal);
            min = Math.min(min, val);
            sum = val + (l == null ? 0 : l.sum + l.addingVal * l.size)
                    + (r == null ? 0 : r.sum + r.addingVal * r.size);
            return this;
        }

        private static int nodeSize(LazyRMQTreap node) {
            return node != null ? node.size : 0;
        }

        /** 範囲処理を遅延評価する */
        private void processLazy() {
            if (rev) {
                LazyRMQTreap temp = l;
                l = r;
                r = temp;
                if (l != null) l.rev ^= true;
                if (r != null) r.rev ^= true;
                rev = false;
            }
            if (addingVal > 0) {
                if (l != null) l.addingVal += addingVal;
                if (r != null) r.addingVal += addingVal;
                val += addingVal;
                addingVal = 0;
                update();
            }
        }

        static LazyRMQTreap merge(LazyRMQTreap l, LazyRMQTreap r) {
            if (l == null || r == null) return l != null ? l : r;
            if (l.p > r.p) {
                l.processLazy();
                l.r = merge(l.r, r);
                return l.update();
            }
            else {
                r.processLazy();
                r.l = merge(l, r.l);
                return r.update();
            }
        }

        static NodePair split(LazyRMQTreap node, int k) {
            NodePair ret = new NodePair();
            if (node == null) return ret;
            node.processLazy();

            if (k <= nodeSize(node.l)) {
                ret = split(node.l, k);
                node.l = ret.r;
                ret.r = node.update();
            }
            else {
                ret = split(node.r, k - nodeSize(node.l) - 1);
                node.r = ret.l;
                ret.l = node.update();
            }
            return ret;
        }

        int get(int i) {
            processLazy();
            if (i < nodeSize(l)) return l.get(i);
            if (i == nodeSize(l)) return val;
            return r.get(i - nodeSize(l) - 1);
        }

        LazyRMQTreap insert(int i, int val) {
            NodePair p = split(this, i);
            return merge(p.l, merge(new LazyRMQTreap(val), p.r));
        }

        LazyRMQTreap delete(int i) {
            NodePair p = split(this, i);
            return merge(p.l, split(p.r, 1).r);
        }

        LazyRMQTreap reverseRange(int l, int r) {
            NodePair ab = split(this, l);
            NodePair bc = split(ab.r, r - l);
            bc.l.rev ^= true;
            return merge(ab.l, merge(bc.l, bc.r));
        }

        LazyRMQTreap rotateLeft(int i) {
            NodePair p = split(this, i);
            return merge(p.r, p.l);
        }

        LazyRMQTreap rotateLeftRange(int l, int r, int i) {
            NodePair ab = split(this, l);
            NodePair bc = split(ab.r, r - l);
            bc.l = bc.l.rotateLeft(i);
            return merge(ab.l, merge(bc.l, bc.r));
        }

        int minRange(int l, int r) {
            // 怠惰にsplitしているが定数倍で遅い
            NodePair ab = split(this, l);
            NodePair bc = split(ab.r, r - l);
            int ret = bc.l.min;
            merge(ab.l, merge(bc.l, bc.r)); // 元の木を復元するのでrootは処理前と変わらない
            return ret;
        }

        int sumRange(int l, int r) {
            NodePair ab = split(this, l);
            NodePair bc = split(ab.r, r - l);
            int ret = bc.l.sum;
            merge(ab.l, merge(bc.l, bc.r));
            return ret;
        }

        void addRange(int l, int r, int val) {
            NodePair ab = split(this, l);
            NodePair bc = split(ab.r, r - l);
            bc.l.addingVal += val;
            merge(ab.l, merge(bc.l, bc.r));
        }
    }

    /**
     * 特定の要素が木の何番目にあるのかがO(logn)でわかるTreap
     */
    static class ReversibleBidirectionalTreap {

        Node[] nodes;  // 初期順序を保持しておくための配列
        Node root;

        static class NodePair {
            Node l;
            Node r;
        }

        static class Node {
            Node l;
            Node r;
            Node par;
            int size = 1;
            int val;
            boolean rev;
            double p;

            private Node update() {
                size = nodeSize(l) + nodeSize(r) + 1;
                return this;
            }

            private static int nodeSize(Node node) {
                return node != null ? node.size : 0;
            }

            private void processRev() {
                if (rev) {
                    Node temp = l;
                    l = r;
                    r = temp;
                    if (l != null) l.rev ^= true;
                    if (r != null) r.rev ^= true;
                    rev = false;
                }
            }

            static Node merge(Node l, Node r) {
                if (l == null || r == null) return l != null ? l : r;
                if (l.p > r.p) {
                    l.processRev();
                    l.r = merge(l.r, r);
                    l.r.par = l;
                    return l.update();
                }
                else {
                    r.processRev();
                    r.l = merge(l, r.l);
                    r.l.par = r;
                    return r.update();
                }
            }

            static NodePair split(Node node, int k) {
                NodePair ret = new NodePair();
                if (node == null) return ret;
                node.processRev();

                if (k <= nodeSize(node.l)) {
                    ret = split(node.l, k);
                    node.l = ret.r;
                    if (ret.r != null) ret.r.par = node;
                    ret.r = node.update();
                    if (ret.l != null) ret.l.par = null;
                }
                else {
                    ret = split(node.r, k - nodeSize(node.l) - 1);
                    node.r = ret.l;
                    if (ret.l != null) ret.l.par = node;
                    ret.l = node.update();
                    if (ret.r != null) ret.r.par = null;
                }
                return ret;
            }

            int get(int i) {
                processRev();
                if (i < nodeSize(l)) return l.get(i);
                if (i == nodeSize(l)) return val;
                return r.get(i - nodeSize(l) - 1);
            }

            int getOrder() {
                int ret = par != null ? par.getOrder() : 0;
                processRev();
                return ret
                        + (par == null ? nodeSize(l) : par.l == this
                                ? -nodeSize(r) - 1
                                : nodeSize(l) + 1);
            }

            Node reverseRange(int l, int r) {
                NodePair ab = split(this, l);
                NodePair bc = split(ab.r, r - l);
                bc.l.rev ^= true;
                return merge(ab.l, merge(bc.l, bc.r));
            }
        }

        public ReversibleBidirectionalTreap(int[] data) {
            int n = data.length;
            nodes = new Node[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new Node();
                nodes[i].val = i;  // data is perm
                nodes[i].p = Math.random();
            }
            root = nodes[data[0]];
            for (int i = 1; i < n; i++) {
                root = Node.merge(root, nodes[data[i]]);
            }
        }

        int getOrder(int i) {
            return nodes[i].getOrder();
        }
    }


    /**
     * @see http://www.prefield.com/algorithm/string/knuth_morris_pratt.html
     */
    static int kmpSearch(String pat, String text) {
        int n = text.length();
        int m = pat.length();
        int[] trans = new int[m + 1];  // オートマトンの遷移テーブルを作る
        trans[0] = -1;
        for (int i = 2; i <= m; i++) {
            char c = pat.charAt(i - 1);
            int fall = trans[i - 1];
            while (fall != -1 && c != pat.charAt(fall))
                fall = trans[fall];  // 次の位置が見つかるまで戻る
            trans[i] = fall + 1;
        }

        int st = 0;  // 現在のステート
        int count = 0;
        for (int i = 0; i < n; i++) {
            // オートマトンの文字と一致するまで戻り続ける
            while (st >= 0 && text.charAt(i) != pat.charAt(st))
                st = trans[st];
            if (++st >= m) {  // 受理状態
                // ここで何か処理する
                count++;
                st = trans[st];
            }
        }
        return count;
    }

    /*
     * Aho-Corasick法
     * @see http://www.prefield.com/algorithm/string/aho_corasick.html
     * @see http://blog.ivank.net/aho-corasick-algorithm-in-as3.html
     */

    static class CharTrie {
        CharTrie[] children = new CharTrie[128];  // hashmapのがいいか?
        CharTrie fail;
        ArrayList<Integer> matches = new ArrayList<Integer>(1);
    }

    static int ahoCorasickBuildAndSearch(String[] pats, String text) {
        return ahoCorasickSearch(ahoCorasickBuild(pats), text);
    }

    /**
     * メモリ使用量が非常に多い。パターン文字数の総和が100000を超えるとOutOfMemoryになる。
     * 手元で-Xmx256Mで400000文字まで大丈夫なのは確認したが…
     */
    static CharTrie ahoCorasickBuild(String[] pats) {
        CharTrie root = new CharTrie();
        for (int i = 0; i < pats.length; i++) {
            CharTrie cur = root;
            for (int j = 0; j < pats[i].length(); j++) {
                char c = pats[i].charAt(j);
                if (cur.children[c] == null) cur.children[c] = new CharTrie();
                cur = cur.children[c];
            }
            cur.matches.add(i);
        }

        Queue<CharTrie> queue = new LinkedList<CharTrie>();  // Java5
        for (int i = 0; i < root.children.length; i++) {
            if (root.children[i] == null) root.children[i] = root;
            else {
                root.children[i].fail = root;
                queue.add(root.children[i]);
            }
        }
        while (!queue.isEmpty()) {
            CharTrie par = queue.poll();
            for (int i = 0; i < par.children.length; i++) {
                CharTrie child = par.children[i];
                if (child == null) continue;
                queue.add(child);

                CharTrie fail = par.fail;
                while (fail.children[i] == null)
                    // 子の失敗遷移は、親の失敗遷移先からその文字で進めるところ
                    fail = fail.fail;
                child.fail = fail.children[i];
                child.matches.addAll(child.fail.matches);
            }
        }
        return root;
    }

    static int ahoCorasickSearch(CharTrie state, String text) {
        int n = text.length();
        int count = 0;
        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            while (state.children[c] == null)
                state = state.fail;
            state = state.children[c];
            for (int j : state.matches) {
                // String s = pat[j];
                // System.out.println("matched: " + s);
                // if (!s.equals(text.substring(i + 1 - s.length(), i + 1))) throw new RuntimeException();
                // ここで何か処理する
                count++;
            }
        }
        return count;
    }

    /*
     * Suffix Array
     * @see http://www.stanford.edu/class/cs97si/suffix-array.pdf
     * @see http://www.prefield.com/algorithm/string/suffix_array.html
     */

    static class SuffixArrayEntry implements Comparable<SuffixArrayEntry> {
        int idx, pos, prevPos;

        @Override
        public int compareTo(SuffixArrayEntry o) {
            return prevPos != o.prevPos ? prevPos - o.prevPos : pos - o.pos;
        }
    }

    /**
     * O(n log^2 n)。SAの先頭に入るべき空文字を無視していることに注意。
     */
    static int[] buildSuffixArray(String s) {
        int n = s.length();  // 空文字を含むならここで+1
        SuffixArrayEntry[] sa = new SuffixArrayEntry[n];
        int[][] pos = new int[2][n];  // pos[t][i]: s.substr(i)の位置（ランク）
        for (int i = 0; i < n; i++) {  // まず1文字だけでのランクを作成
            sa[i] = new SuffixArrayEntry();
            pos[0][i] = s.charAt(i);  // 空文字を含むならpos[0][n-1]=-1; とする
        }
        for (int t = 1, step = 1;; t ^= 1, step *= 2) {
            for (int i = 0; i < n; i++) {
                sa[i].idx = i;
                sa[i].prevPos = pos[t ^ 1][i];  // 前回の位置
                sa[i].pos = i + step < n ? pos[t ^ 1][i + step] : -1;  // 前回からstep文字だけ進んだ時の位置
            }
            Arrays.sort(sa);
            if (step * 2 >= n) break;
            // 今回のランクを更新
            for (int i = 0; i < n; i++) {
                pos[t][sa[i].idx] = (i > 0 && sa[i].compareTo(sa[i - 1]) == 0)
                        ? pos[t][sa[i - 1].idx]
                        : i;
            }
        }
        // 結果をint配列にくるんで返す
        int[] ret = new int[n];
        for (int i = 0; i < n; i++) {
            ret[i] = sa[i].idx;
        }
        return ret;
    }

    /**
     * O(mlogn)
     * 
     * @return マッチする箇所のうち接尾辞が辞書順最小のもの。マッチしなかった場合は
     *         (-(挿入ポイント) - 1)を返す（Arrays#binarySearch()と同様）
     */
    static int findWithSuffixArrayNaive(String pat, String text, int[] sa) {
        int n = text.length();
        int m = pat.length();
        int l = 0;
        int r = n;
        boolean matched = false;
        while (l < r) {
            int mid = (l + r) / 2;
            int comp = text.substring(sa[mid], Math.min(sa[mid] + m, n)).compareTo(pat);
            matched |= comp == 0;  // マッチの有無を判別するだけでいいならここでreturn
            if (comp < 0) l = mid + 1;
            else r = mid;
        }
        return matched ? sa[l] : -sa[l] - 1;
    }

    /**
     * 接尾辞配列とそのLCPをO(1)で求めるRMQを同時に作る。O(n log^2 n)
     */
    static class SuffixArray {
        SuffixArrayEntry[] sa;
        int[][] st;

        public SuffixArray(String s) {
            int n = s.length();
            int logN = 32 - Integer.numberOfLeadingZeros(n - 1);
            sa = new SuffixArrayEntry[n];
            int[][] pos = new int[logN + 1][n];  // pos[t][i]: s.substr(i,i+2**t)の位置（ランク）
            for (int i = 0; i < n; i++) {  // まず1文字だけでのランクを作成
                sa[i] = new SuffixArrayEntry();
                pos[0][i] = s.charAt(i);
            }
            for (int t = 1, step = 1; step < n; t++, step *= 2) {
                for (int i = 0; i < n; i++) {
                    sa[i].idx = i;
                    sa[i].prevPos = pos[t - 1][i];  // 前回の位置
                    sa[i].pos = i + step < n ? pos[t - 1][i + step] : -1;  // 前回からstep文字だけ進んだ時の位置
                }
                Arrays.sort(sa);
                // 今回のランクを更新
                for (int i = 0; i < n; i++) {
                    pos[t][sa[i].idx] = (i > 0 && sa[i].compareTo(sa[i - 1]) == 0)
                            ? pos[t][sa[i - 1].idx]
                            : i;
                }
            }
            // 高さ配列を作る。
            // 任意の二点間のLCPの計算にO(logn)かけていいならこれ以下の作業は不要
            int[] lcp = new int[n];
            for (int i = 0; i < n - 1; i++) {
                int x = sa[i].idx, y = sa[i + 1].idx;
                for (int k = logN - 1; k >= 0 && x < n && y < n; k--) {
                    if (pos[k][x] == pos[k][y]) {
                        x += 1 << k;
                        y += 1 << k;
                        lcp[i] += 1 << k;
                    }
                }
            }
            // 高さ配列を元にSparse Tableを作る
            st = pos;  // メモリ効率のため再利用
            st[0] = lcp;
            for (int i = 1; i < logN; i++) {
                for (int j = 0; j < n - (1 << i) + 1; j++) {
                    st[i][j] = Math.min(st[i - 1][j], st[i - 1][j + (1 << i - 1)]);
                }
            }
        }

        int lcp(int x, int y) {
            int i = 31 - Integer.numberOfLeadingZeros(y - x);
            return Math.min(st[i][x], st[i][y - (1 << i)]);
        }
    }
}
