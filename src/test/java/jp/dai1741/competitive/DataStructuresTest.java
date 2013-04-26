package jp.dai1741.competitive;

import static org.junit.Assert.*;

import jp.dai1741.competitive.DataStructures.LazyRMQTreap;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static jp.dai1741.competitive.DataStructures.*;

public class DataStructuresTest {

    @Test
    public void testBIT() {
        BinaryIndexedTree bit = new BinaryIndexedTree(15);
        bit.add(3, 5);
        bit.add(1, 2);
        bit.add(11, 13);
        bit.add(5, -1);
        bit.add(11, 2);
        assertEquals(6, bit.sum(7));
        assertEquals(21, bit.sum(15));
        assertEquals(2, bit.sum(1));
        try {
            bit.sum(0);
            fail();
        }
        catch (RuntimeException e) {
        }
    }

    @Test
    public void testDualBIT() {
        DualBinaryIndexedTree bit = new DualBinaryIndexedTree(15);
        bit.add(3, 5);
        bit.add(1, 2);
        bit.addRange(10, 15, 3);
        bit.addRange(3, 12, -1);
        bit.add(5, -1);
        bit.addRange(1, 2, 10);
        assertEquals(21, bit.sum(7));
        assertEquals(34, bit.sum(15));
        assertEquals(12, bit.sum(1));
        try {
            bit.addRange(0, 16, 1);
            fail();
        }
        catch (RuntimeException e) {
        }
    }

    @Test
    public void testBIT2D() {
        BinaryIndexedTree2D bit2d = new BinaryIndexedTree2D(10, 10);
        bit2d.add(3, 1, 5);
        bit2d.add(1, 2, 2);
        bit2d.add(7, 5, 13);
        bit2d.add(5, 10, -1);
        bit2d.add(9, 8, 2);
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                System.out.print(bit2d.sum(j, i) + " ");
            }
            System.out.println();
        }
        assertEquals(6, bit2d.sum(5, 10));
        assertEquals(21, bit2d.sum(10, 10));
        assertEquals(2, bit2d.sum(2, 4));
        assertEquals(5, bit2d.sum(7, 1));
        try {
            bit2d.sum(0, 1);
            fail();
        }
        catch (RuntimeException e) {
        }
    }

    @Test
    public void testUnionFind() {
        UnionFind uf = new UnionFind(10);
        uf.unite(2, 5);
        uf.unite(3, 4);
        uf.unite(0, 5);
        uf.unite(7, 9);
        uf.unite(1, 8);
        uf.unite(4, 7);

        assertTrue(uf.find(2) == uf.find(5));
        assertTrue(uf.find(2) != uf.find(6));
        assertTrue(uf.find(2) != uf.find(7));
        assertTrue(uf.find(0) != uf.find(9));
        assertTrue(uf.find(3) == uf.find(9));
        assertTrue(uf.find(0) == uf.find(2));
    }

    @Test
    public void testLazyRMQSegmentTree() {
        LazyRMQSegmentTreeNode tree = new LazyRMQSegmentTreeNode(0, 1000000000);
        tree.set(50000, 1000);
        tree.set(5000, 10000);

        assertEquals(1000, tree.minRange(400, 1000000));
        assertEquals(10000, tree.minRange(700, 50000));
        assertEquals(1000, tree.minRange(50000, 50001));
        assertEquals(Integer.MAX_VALUE, tree.minRange(70000, 100000));
        assertEquals(Integer.MAX_VALUE, tree.minRange(3, 9));

        tree.set(5000, 999);
        assertEquals(999, tree.minRange(700, 50000));

        for (int i = 0; i < 1000000000; i += 10000000) {
            tree.set(i, i);
        }
        tree.set(150000001, -3);
        assertEquals(-3, tree.minRange(10000000, 1000000000));
        assertEquals(10000000, tree.minRange(10000000, 100000000));
        assertEquals(60000000, tree.minRange(54697200, 99999999));
    }

    @Test
    public void testArrayRMQSegmentTree() {
        ArrayRMQSegmentTree tree = new ArrayRMQSegmentTree(100000);
        tree.set(5000, 1000);
        tree.set(500, 10000);
        assertEquals(1000, tree.minRange(400, 100000));
        assertEquals(10000, tree.minRange(70, 5000));
        assertEquals(1000, tree.minRange(5000, 5001));
        assertEquals(Integer.MAX_VALUE, tree.minRange(4999, 5000));
        assertEquals(Integer.MAX_VALUE, tree.minRange(5001, 5002));
        assertEquals(Integer.MAX_VALUE, tree.minRange(700, 1000));
        assertEquals(Integer.MAX_VALUE, tree.minRange(3, 9));

        tree.set(500, 999);
        assertEquals(999, tree.minRange(70, 5000));

        for (int i = 0; i < 100000; i += 1000) {
            tree.set(i, i % 10000);
        }
        assertEquals(0, tree.minRange(6347, 33622));
        assertEquals(4000, tree.minRange(13859, 17461));

        tree.set(777, -77777);
        assertEquals(-77777, tree.minRange(0, 100000));
        assertEquals(9000, tree.minRange(99000, 100000));
    }

    @Test
    public void testRMQSparseTable() {
        int[] data = new int[100000];
        Arrays.fill(data, Integer.MAX_VALUE);
        data[5555] = 1000;
        data[555] = 10000;
        data[777] = -77777;
        for (int i = 0; i < 100000; i += 1000) {
            data[i] = i % 10000;
        }
        RMQSparseTable st = new RMQSparseTable(data);
        assertEquals(-77777, st.minRange(400, 100000));
        assertEquals(-77777, st.minRange(70, 5000));
        assertEquals(5000, st.minRange(5000, 5001));
        assertEquals(Integer.MAX_VALUE, st.minRange(4999, 5000));
        assertEquals(Integer.MAX_VALUE, st.minRange(5001, 5002));
        assertEquals(Integer.MAX_VALUE, st.minRange(3, 9));
        assertEquals(-77777, st.minRange(700, 1000));
        assertEquals(1000, st.minRange(778, 9563));

        assertEquals(0, st.minRange(6347, 33622));
        assertEquals(4000, st.minRange(13859, 17461));

        assertEquals(9000, st.minRange(99000, 100000));
    }

    @Test
    public void testBitTrie() {
        BitTrie trie = new BitTrie(63);

        assertFalse(trie.find(0));
        trie.insert(0);
        assertTrue(trie.find(0));

        trie.insert(5730512458017474L);
        assertTrue(trie.find(5730512458017474L));
        assertFalse(trie.find(5730512458017474L + 1000));

        assertEquals(5730512458017474L, trie.findNearest(8193018831944941L));
        assertEquals(0, trie.findNearest(3193018831944941L));
        assertEquals(0, trie.findNearest(29));
        trie.insert(16);
        assertEquals(16L, trie.findNearest(29));

        assertEquals(5730512458017474L, trie.findNearest(-5));
        assertFalse(trie.find(-6));
        trie.insert(-6);
        assertTrue(trie.find(-6));
        assertEquals(-6, trie.findNearest(-5));
        assertEquals(-6, trie.findNearest(-479390920817L));

        assertEquals(0, trie.findNearest(1));
        trie.insert(1);
        assertEquals(1, trie.findNearest(3));
    }

    @Test
    public void testReversibleBidirectionalTreap() {
        ReversibleBidirectionalTreap treap = new ReversibleBidirectionalTreap(new int[] {
                3, 5, 2, 0, 4, 6, 1 });
        assertEquals(1, treap.getOrder(5));
        assertEquals(3, treap.getOrder(0));
        assertEquals(4, treap.root.get(4));

        treap.root.reverseRange(2, 6);
        assertEquals(0, treap.root.get(4));
        assertEquals(2, treap.getOrder(6));
        assertEquals(3, treap.getOrder(4));

        treap.root.reverseRange(1, 4);
        treap.root.reverseRange(0, 5);
        treap.root.reverseRange(2, 7);
        assertEquals(6, treap.root.get(6));
        assertEquals(5, treap.getOrder(4));
        assertEquals(1, treap.getOrder(5));

        treap.root.reverseRange(2, 4);
        treap.root.reverseRange(0, 7);
        assertEquals(0, treap.root.get(6));
        assertEquals(1, treap.root.get(3));
        assertEquals(2, treap.getOrder(3));
        assertEquals(4, treap.getOrder(2));
    }

    @Test
    public void testLazyRMQTreap() {
        LazyRMQTreap root = new LazyRMQTreap(3);
        for (int i = 30; i < 40; i++) {
            root = LazyRMQTreap.merge(root, new LazyRMQTreap(i));
        }
        assertEquals(3, root.get(0));
        assertEquals(35, root.get(6));
        assertEquals(36, root.get(7));
        assertEquals(39, root.get(10));

        root = root.reverseRange(0, 11);
        assertEquals(39, root.get(0));
        assertEquals(33, root.get(6));
        assertEquals(32, root.get(7));
        assertEquals(3, root.get(10));
        root = root.delete(6);
        assertEquals(32, root.get(6));
        root = root.insert(3, 10000);
        assertEquals(3, root.get(10));
        assertEquals(10000, root.get(3));
        assertEquals(34, root.get(6));

        assertEquals(30, root.minRange(0, 10));
        assertEquals(10312, root.sumRange(0, 10));
        assertEquals(61, root.sumRange(8, 10));
        assertEquals(64, root.sumRange(8, 11));

        root.addRange(8, 10, 100);
        assertEquals(32, root.minRange(0, 10));
        assertEquals(10512, root.sumRange(0, 10));
        assertEquals(261, root.sumRange(8, 10));
    }

    final String aDNA = "CTCATGGTGTCGCTCGTAACTAGTGCAGGTATCGGTTTTTTTCACTTGCCTAAAGGACAATCGACTTTGCTCCGACACCTAGGACGAGCACCGGTCTGGCGTGAATCGCTGTTGCCGACAGCGCTACAGCGACATAATGTCCGCGCTCAGCATATGTATACCGGATGAAAACTTCGCGGCAATAGCATCGTACACGATTTCTGTTAGCTACCAGGATAGAATAGAGTAATGTTTTAAGCCCCAGCCTGTAAGAAGCCCCGTTGACCCGCGGTTCGCTAAGTAAAACGGCCGAAATGGTTT";
    final String aPoem = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    @Test
    public void testKMPSearch() {
        assertEquals(1, kmpSearch("abcdefg", "abcdefghjk"));
        assertEquals(1, kmpSearch("abcdefg", "hogehoge abcdefg"));
        assertEquals(2, kmpSearch("abcdefg", "abcdefgabcdefg"));
        assertEquals(1, kmpSearch("abcdefg", "abcabcdefgefg"));
        assertEquals(0, kmpSearch("abcdefg", "hogehogehogehoge"));
        assertEquals(2, kmpSearch("abab", "abacabbabababcab"));
        assertEquals(12, kmpSearch("aaa", "aaaaaaabaaaaaaaaa"));
        assertEquals(2, kmpSearch("1234123", "123412341234"));
        assertEquals(1, kmpSearch("1234124", "12341234124567"));
        assertEquals(2, kmpSearch("aaaaaab", "aaaaaaaaaaaaabaaaaaaaaaaaaaaaaababa"));
        assertEquals(0, kmpSearch("aaaaaab", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        assertEquals(3, kmpSearch("l", "hello world!"));

        assertEquals(1, kmpSearch("ACTA", aDNA));
        assertEquals(0, kmpSearch("ACGT", aDNA));
        assertEquals(2, kmpSearch("GAG", aDNA));
        assertEquals(16, kmpSearch("CA", aDNA));
        assertEquals(3, kmpSearch("TAT", aDNA));
        assertEquals(13, kmpSearch("GG", aDNA));
        assertEquals(74, kmpSearch("G", aDNA));
    }

    @Test
    public void testAhoCorasickSearch() {
        assertEquals(2, ahoCorasickBuildAndSearch(new String[] { "hoge", "fuga" },
                "hoge fuga"));
        assertEquals(7, ahoCorasickBuildAndSearch(new String[] {
                "aaa", "aa", "ab", "baa", "ca" }, "aabaaab"));
        assertEquals(7, ahoCorasickBuildAndSearch(new String[] {
                "ada", "bra", "dab", "cab", "aca", "ra" }, "abracadabra"));
        assertEquals(7, ahoCorasickBuildAndSearch(new String[] {
                "a", "ab", "abc", "abcd", "bcd", "cd", "d" }, "abcd"));
        assertEquals(0, ahoCorasickBuildAndSearch(new String[] {
                "abc", "def", "lnm", "zzz", "xyz" }, "sadly, no match exists."));
        assertEquals(1, ahoCorasickBuildAndSearch(new String[] {
                "aaaaaaaaaaabbbbbbbbbbcccc", "ddddddddddeeeeeffffffff", "hhhhhhhiii" },
                "aaaaaaabbbbbbbbbccccccccccddddddeeeeeeeefffffffhhhhhhhhhhhhhiiiiijj"));
        assertEquals(
                3 + 5 + 7 + 2 + 11 + 5 + 1,
                ahoCorasickBuildAndSearch(
                        new String[] { "(a)", "::", ":(", ":)", "a", "((", "generator" },
                        "a(a)::(((::)))())((a)(:((:a())):((::(:()(a)))i am trapped in a test case generator :(:(a(:::))"));
        assertEquals(9, ahoCorasickBuildAndSearch(new String[] {
                "sa", "ma", "aita", "ama", "e", "efe", "ect", "gunma", "aichi" },
                "saitama prefecture"));
        assertEquals(109, ahoCorasickBuildAndSearch(new String[] {
                "ACTA", "ACGT", "GAG", "CA", "TAT", "GG", "G", }, aDNA));
        assertEquals(1, ahoCorasickBuildAndSearch(new String[] {
                aDNA, aPoem, "GAG", "CA", "TAT", "GG", "G", }, aPoem));

        // a large one
        // 700パターンx700文字を食わせるとメモリ256MBあっても死ぬ。
        String[] pats = new String[90];
        pats[0] = "XY";
        StringBuilder sbAll = new StringBuilder(101000);
        StringBuilder sb = new StringBuilder(800);
        for (int i = 1; i < 90; i++) {
            sb.setLength(0);
            for (int j = 0; j < 800; j++)
                sb.append((char) (i % 90 + 32));
            pats[i] = sb.toString();
            if (sbAll.length() < 100000) sbAll.append(pats[i]);
            if (i % 100 == 0) System.out.println(i + "," + sbAll.length());
        }
        assertEquals(90, ahoCorasickBuildAndSearch(pats, sbAll.toString()));
    }

    @Test
    public void testSuffixArray() {
        System.out.println(Arrays.toString(buildSuffixArray("abcde")));
        System.out.println(Arrays.toString(buildSuffixArray("abcde")));
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, buildSuffixArray("abcde"));
        assertArrayEquals(new int[] { 10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2 },
                buildSuffixArray("abracadabra"));
        assertArrayEquals(new int[] { 4, 0, 3, 1, 2 }, buildSuffixArray("abcba"));

        int[] saDNA = buildSuffixArray(aDNA);
        HashSet<Integer> set = new HashSet<Integer>(aDNA.length() * 2);
        
        for (int i = 1; i < saDNA.length; i++) {
            set.add(saDNA[i-1]);
            set.add(saDNA[i]);
            assertTrue(i + "-th data differ", aDNA.substring(saDNA[i - 1]).compareTo(
                    aDNA.substring(saDNA[i])) < 0);
        }
        assertEquals(aDNA.length(), set.size());
        
        assertEquals(250, findWithSuffixArrayNaive("AG", aDNA, saDNA));
        assertEquals(18, findWithSuffixArrayNaive("ACTA", aDNA, saDNA));
        assertEquals(207, findWithSuffixArrayNaive("CTACCAGGATAGAA", aDNA, saDNA));
        assertTrue(findWithSuffixArrayNaive("AAAAAAGAGAGTA", aDNA, saDNA) < 0);
        assertEquals(370, findWithSuffixArrayNaive("non proident", aPoem, buildSuffixArray(aPoem)));
    }
}
