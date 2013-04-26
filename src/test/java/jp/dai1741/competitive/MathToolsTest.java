package jp.dai1741.competitive;

import static org.junit.Assert.*;
import static jp.dai1741.competitive.MathTools.*;

import jp.dai1741.competitive.MathTools;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
import java.util.TreeMap;


public class MathToolsTest {

    MathTools tool = new MathTools();

    @Test
    public void testPrimes() {
        tool.makePrimesArray(10000);

        assertEquals(1229, tool.numPrimes);
        assertEquals(10001, tool.isPrime.length);
        for (int p : new int[] { 2, 3, 19, 3313, 7681 }) {
            assertTrue(isPrime(p));
            assertTrue(tool.isPrime[p]);
            assertTrue(tool.isPrimeWithPrimeArray(p));
        }
        for (int c : new int[] { 0, 1, 4, 2773, 9047, 10000 }) {
            assertFalse(isPrime(c));
            assertFalse(tool.isPrime[c]);
            assertFalse(tool.isPrimeWithPrimeArray(c));
        }
        assertTrue(isPrime(1000003));
        assertTrue(tool.isPrimeWithPrimeArray(1000003));
        assertFalse(isPrime(1000007));
        assertFalse(tool.isPrimeWithPrimeArray(1000007));
        assertTrue(tool.isPrimeWithPrimeArray(99999989));

        assertEquals(2, tool.primes[0]);
        assertEquals(31, tool.primes[10]);
        assertEquals(547, tool.primes[100]);
        assertEquals(7927, tool.primes[1000]);
        assertEquals(9973, tool.primes[1228]);
    }

    @Test
    public void testMakeSegmentPrimesArray() {
        long a = 10000000000L;
        tool.makeSegmentPrimesArray(a, a + 1000);

        assertTrue(tool.isLargePrime[19]);
        assertTrue(tool.isLargePrime[319]);
        assertTrue(tool.isLargePrime[583]);
        assertTrue(tool.isLargePrime[877]);
        assertTrue(tool.isLargePrime[999]);
        assertFalse(tool.isLargePrime[0]);
        assertFalse(tool.isLargePrime[1]);
        assertFalse(tool.isLargePrime[7]);
        assertFalse(tool.isLargePrime[763]);
        assertFalse(tool.isLargePrime[819]);

        tool.makeSegmentPrimesArray(2, 3);
        assertTrue(tool.isLargePrime[0]);
    }

    @Test
    public void testGetDivisors() {
        List<Integer> divs = getDivisors(132);
        Collections.sort(divs);
        assertEquals(Arrays.asList(1, 2, 3, 4, 6, 11, 12, 22, 33, 44, 66, 132), divs);

        divs = getDivisors(10080);
        Collections.sort(divs);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 15, 16, 18, 20,
                21, 24, 28, 30, 32, 35, 36, 40, 42, 45, 48, 56, 60, 63, 70, 72, 80, 84,
                90, 96, 105, 112, 120, 126, 140, 144, 160, 168, 180, 210, 224, 240, 252,
                280, 288, 315, 336, 360, 420, 480, 504, 560, 630, 672, 720, 840, 1008,
                1120, 1260, 1440, 1680, 2016, 2520, 3360, 5040, 10080), divs);

        divs = getDivisors(121);
        Collections.sort(divs);
        assertEquals(Arrays.asList(1, 11, 121), divs);

        divs = getDivisors(107);
        Collections.sort(divs);
        assertEquals(Arrays.asList(1, 107), divs);
        // assertTrue(divs instanceof RandomAccess);
    }

    @SuppressWarnings("serial")
    @Test
    public void testPrimeFactors() {
        tool.makePrimesArray(102);

        TreeMap<Integer, Integer> factors = getPrimeFactors(132);
        assertEquals(new TreeMap<Integer, Integer>() {
            {
                put(2, 2);
                put(3, 1);
                put(11, 1);
            }
        }, factors);
        assertEquals(tool.getPrimeFactorsWithPrimeArray(132), factors);
        assertEquals(Arrays.asList(2, 3, 11), getPrimeFactorsIgnoringCount(132));

        factors = getPrimeFactors(10080);
        assertEquals(new TreeMap<Integer, Integer>() {
            {
                put(2, 5);
                put(3, 2);
                put(5, 1);
                put(7, 1);
            }
        }, factors);
        assertEquals(tool.getPrimeFactorsWithPrimeArray(10080), factors);
        assertEquals(Arrays.asList(2, 3, 5, 7), getPrimeFactorsIgnoringCount(10080));

        factors = getPrimeFactors(121);
        assertEquals(new TreeMap<Integer, Integer>() {
            {
                put(11, 2);
            }
        }, factors);
        assertEquals(tool.getPrimeFactorsWithPrimeArray(121), factors);
        assertEquals(Collections.singletonList(11), getPrimeFactorsIgnoringCount(121));

        factors = getPrimeFactors(107);
        assertEquals(new TreeMap<Integer, Integer>() {
            {
                put(107, 1);
            }
        }, factors);
        assertEquals(tool.getPrimeFactorsWithPrimeArray(107), factors);
        assertEquals(Collections.singletonList(107), getPrimeFactorsIgnoringCount(107));
    }

    @Test
    public void testMoebiusMap() {
        TreeMap<Integer, Integer> map = getMoebiusMapOfFactorsOf(10080);
        for (int div : getDivisors(10080)) {
            boolean diviableSqr = div % 4 == 0 || div % 9 == 0 || div % 25 == 0
                    || div % 49 == 0;
            assertTrue(map.containsKey(div) != diviableSqr);
        }
        assertEquals(-1, map.get(2).intValue());
        assertEquals(-1, map.get(2 * 3 * 5).intValue());
        assertEquals(-1, map.get(3 * 5 * 7).intValue());
        assertEquals(1, map.get(1).intValue());
        assertEquals(1, map.get(2 * 5).intValue());
        assertEquals(1, map.get(2 * 3 * 5 * 7).intValue());

        assertFalse(map.containsKey(11));
        assertFalse(map.containsKey(13 * 19));
        assertFalse(map.containsValue(0));
    }

    @Test
    public void testGcds() {
        assertEquals(2, gcd(4, 6));
        assertEquals(3159, gcd(72657, 60021));
        assertEquals(3, gcd(63537, 8379));
        assertEquals(77647, gcd(698823, 76249354));
        assertEquals(1, gcd(1095, 563));
        assertEquals(1, gcd(19683, 65536));
        assertEquals(100, gcd(0, 100));
        assertEquals(31, gcd(31, 0));
        assertEquals(0, gcd(0, 0));

        assertEquals(60, lcm(12, 15));
        assertEquals(38822, lcm(2773, 826));
        assertEquals(0, lcm(100, 0));

        testAExtgcd(6, 8, 2);
        testAExtgcd(8937, 1929276, 9);
        testAExtgcd(654, 55, 1);
        testAExtgcd(627532, 18103, 1);
        testAExtgcd(0, 5, 5);
        testAExtgcd(3, 3, 3);

    }

    private void testAExtgcd(int a, int b, int gcd) {
        int[] xy = new int[2];
        assertEquals(gcd, extgcd(a, b, xy));
        int x = xy[0];
        int y = xy[1];
        assertTrue(a * x + b * y == gcd);
        assertTrue(a * b == 0 || (Math.abs(x) <= b && Math.abs(y) <= a));
    }

    @Test(expected = ArithmeticException.class)
    public void testSolveLinearEquationsSystemWithGaussJordan() {
        double[][] a = { { 1, 3, 1.2 }, { -30, 1.36, 0 }, { 6.7, 2.2, 1 }, };
        double[] b = { 1, 2.5, 3.82766 };
        double[] expecteds = { -4.72156, -102.314, 260.552 };
        final double EPS = 0.001;
        assertArrayEquals(expecteds, solveLinearEquationsSystemWithGaussJordan(a, b), EPS);

        a = new double[][] { { 1, 0, 0 }, { 0, 1, 1 }, { 0, 2, 2 }, };
        b = new double[] { 1, 2, 3 };
        solveLinearEquationsSystemWithGaussJordan(a, b);
    }

    @Test
    public void testMul() {
        double[][] a = {
                { 10, 5, 3, 0, 9 }, { 0, -15, 307, 1, 1 }, { 2, 4, 9, 18, 12 },
                { 1, 1, -2, -1, 10 }, { 3, 4, 2, 1, 75 }, };
        double[][] b = {
                { 13, 6, -2, 10, -2, }, { 1, 2, -2, 8, 12, }, { 9, 9, 10, 16, 0, },
                { 12, 14, 1, 15, 16, }, { 12, 13, 1, 12, 6, }, };
        double[][] expected = {
                { 270, 214, 9, 296, 94 }, { 2772, 2760, 3102, 4819, -158 },
                { 471, 509, 108, 610, 404 }, { 104, 106, -15, 91, 54 },
                { 973, 1033, 82, 1009, 508 }, };
        assertArrayEquals(expected, mul(a, b));
        assertArrayEquals(a, mul(a, makeIndentityMatrixDouble(5)));

        double[] v = { 0.25, 1.0 / 3, -1.7, 0, 20.3 };
        double[] expectedV = {
                5453.0 / 30, -2533.0 / 5, 3452.0 / 15, 12419.0 / 60, 91271.0 / 60 };
        assertArrayEquals(expectedV, mul(a, v), 0.0000001);
    }

    @Test
    public void testEulerφ() {
        int[] ns = { 0, 1, 100, 6351, 10007, 123456789 };
        int[] expecteds = { 0, 1, 40, 4032, 10006, 82260072 };
        tool.makeEulerφArray(10008);
        assertEquals(10008, tool.euler.length);

        for (int i = 0; i < ns.length; i++) {
            assertEquals(expecteds[i], getEulerφ(ns[i]));
            if (ns[i] < tool.euler.length) assertEquals(expecteds[i], tool.euler[ns[i]]);
        }
    }

    @Test
    public void testComb() {
        assertEquals(1, comb(100, 0));
        assertEquals(1, comb(100, 100));
        assertEquals(0, comb(100, 101));
        assertEquals(0, comb(100, -1));
        assertEquals(3, comb(3, 2));
        assertEquals(6, comb(6, 1));
        assertEquals(35, comb(7, 3));
        assertEquals(6435, comb(15, 8));
        assertEquals(3585446225075L, comb(107, 98));
        assertEquals(6724251264192L, comb(960, 5));
        assertEquals(247959266474052L, comb(51, 25));
        assertEquals(18412956934908690L, comb(62, 21));
    }

    @Test
    public void testPermutations() {
        Permutations p = new Permutations(10);
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, p.data);
        assertTrue(p.nextPermutaion());
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 9, 8 }, p.data);
        for (int i = 0; i < 10; i++) {
            p.nextPermutaion();
        }
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 7, 9, 8, 6 }, p.data);
        for (int i = 0; i < 57; i++) {
            p.nextPermutaion();
        }
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 7, 9, 6, 5, 8 }, p.data);
        p = new Permutations(4);
        assertArrayEquals(new int[] { 0, 1, 2, 3 }, p.data);
        for (int i = 0; i < 23; i++) {
            assertTrue(p.nextPermutaion());
        }
        assertArrayEquals(new int[] { 3, 2, 1, 0 }, p.data);
        assertFalse(p.nextPermutaion());

        p = new Permutations(new int[] { 0, 0, 0, 0, 1, 1, 1 });  // Combination 7C3
        assertTrue(p.nextPermutaion());
        assertArrayEquals(new int[] { 0, 0, 0, 1, 0, 1, 1 }, p.data);
        assertTrue(p.nextPermutaion());
        assertArrayEquals(new int[] { 0, 0, 0, 1, 1, 0, 1 }, p.data);
        assertTrue(p.nextPermutaion());
        assertArrayEquals(new int[] { 0, 0, 0, 1, 1, 1, 0 }, p.data);
        assertTrue(p.nextPermutaion());
        assertArrayEquals(new int[] { 0, 0, 1, 0, 0, 1, 1 }, p.data);
        for (int i = 0; i < 35 - 5; i++) {
            assertTrue(p.nextPermutaion());
        }
        assertArrayEquals(new int[] { 1, 1, 1, 0, 0, 0, 0 }, p.data);
        assertFalse(p.nextPermutaion());
    }

}
