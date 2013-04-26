package jp.dai1741.competitive;

import static org.junit.Assert.*;
import static jp.dai1741.competitive.ModTools.*;

import org.junit.Test;

import java.math.BigInteger;

public class ModToolsTest {

    final int BASIC_MOD = 1000000007;

    @Test(expected = RuntimeException.class)
    public void testFactorialFactory() {
        FactorialFactory cachedFactory = new CachedFactorialFactory(BASIC_MOD, 1000);
        FactorialFactory linearFactory = new LinearFactorialFactory(BASIC_MOD);
        for (int i = 0; i < 1000; i += 3) {
            long cached = cachedFactory.fact(i);
            long linear = linearFactory.fact(i);
            assertTrue("fact(" + i + ") differ: " + cached + "," + linear,
                    cached == linear);
        }
        assertEquals(1, cachedFactory.fact(0));
        assertEquals(24, cachedFactory.fact(4));
        assertEquals(318608048, cachedFactory.fact(50));
        assertEquals(749113557, cachedFactory.fact(756));
        assertEquals(756641425, cachedFactory.fact(999));

        cachedFactory.fact(1000);
    }

    @Test(expected = RuntimeException.class)
    public void testCombinationFactory() {
        CachedCombinationFactory factory = new CachedCombinationFactory(BASIC_MOD, 100);
        
        assertEquals(6, factory.combination(4, 2));
        assertEquals(330, factory.combination(11, 7));
        assertEquals(235492746, factory.combination(65, 21));
        assertEquals(253579538, factory.combination(99, 70));
        assertEquals(98, factory.combination(98, 97));
        assertEquals(1, factory.combination(32, 32));
        assertEquals(1, factory.combination(87, 0));
        assertEquals(1, factory.combination(1, 0));

        factory.combination(3, 4);
    }

    @Test
    public void testMul() {
        long[][] a = {
                { 10, 5, 3, 0, 9 }, { 0, -15, 307, 1, 1 }, { 2, 4, 9, 18, 12 },
                { 1, 1, -2, -1, 10 }, { 3, 4, 2, 1, 75 }, };
        long[][] b = {
                { 13, 6, -2, 10, -2, }, { 1, 2, -2, 8, 12, }, { 9, 9, 10, 16, 0, },
                { 12, 14, 1, 15, 16, }, { 12, 13, 1, 12, 6, }, };
        long[][] expected = {
                { 270, 214, 9, 296, 94 }, { 2772, 2760, 3102, 4819, -158 },
                { 471, 509, 108, 610, 404 }, { 104, 106, -15, 91, 54 },
                { 973, 1033, 82, 1009, 508 }, };
        assertArrayEquals(expected, mul(a, b, BASIC_MOD));
        assertArrayEquals(a, mul(a, makeIndentityMatrixLong(5), BASIC_MOD));

        a = new long[][] { { 10000000, 100000, 1 }, { 100000, 100, 1000 }, };
        b = new long[][] { { 5000, 500000 }, { 500000, 500000 }, { 500000000, 500 }, };
        expected = new long[][] { { 499999300, 999965157 }, { 549996500, 50499650 }, };
        assertArrayEquals(expected, mul(a, b, BASIC_MOD));
    }

    @Test
    public void testPow() {
        assertEquals(6, pow(5, 101, 19));
        assertEquals(6, pow(5, 100000001, 19));
        assertEquals(1, pow(5, 0, 19));
        assertEquals(512469062, pow(5, 100000001, BASIC_MOD));
        assertEquals(967035682, pow(512469062, 10000000001L, BASIC_MOD));

        long[][] fibonacci = { { 1, 1 }, { 1, 0 } };
        long[][] fibonnaciPow = pow(fibonacci, 100000000, 100000);
        assertEquals(46875, fibonnaciPow[1][0]);
        assertEquals(37501, fibonnaciPow[0][0]);
        // long[][] a = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        // long[][] expected = new long[][] {
        // {},
        // {},
        // {},
        // };
        // System.out.println(Arrays.deepToString(pow(a, 1000000, 100007)));
        // assertArrayEquals(expected, pow(a, 1000000, 100007));
        // data ga nai ;_;
        System.out.println(new BigInteger("1758412232636122750").mod(BigInteger.ONE.shiftLeft(64)).toString(4));
    }

    @Test
    public void testModInverse() {
        int[] as = { 1, 2, 25, 23237, 8738, 1287 };
        int[] ms = { BASIC_MOD, 29, 3313 };
        for (int i = 0; i < ms.length; i++) {
            for (int j = 0; j < as.length; j++) {
                int expected = BigInteger.valueOf(as[j]).modInverse(
                        BigInteger.valueOf(ms[i])).intValue();
                assertEquals(expected, modInverse(as[j], ms[i]));

            }
        }
        try {
            modInverse(0, BASIC_MOD);
            fail();
        }
        catch (ArithmeticException e) {
        }
        try {
            modInverse(4, 98);
            fail();
        }
        catch (ArithmeticException e) {
        }
    }

    @Test
    public void testSolveLinearCongruence() {
        int[] a = { 1, 1, 1 };
        int[] b = { 2, 3, 2 };
        int[] m = { 3, 5, 7 };
        assertArrayEquals(new int[] { 23, 105 }, solveLinearCongruence(a, b, m));
        int[] a2 = { 2, 3, 2 };
        int[] b2 = { 2, 2, 4 };
        int[] m2 = { 6, 7, 8 };
        assertArrayEquals(new int[] { 10, 84 }, solveLinearCongruence(a2, b2, m2));
    }

    @Test
    public void testModFact() {
        FactorialFactory factory = new CachedFactorialFactory(BASIC_MOD, 1000);
        int[] e = { 0 };
        for (int i : new int[] { 0, 10, 100, 510, 777 }) {
            assertEquals(factory.fact(i), modFact(i, BASIC_MOD, e, factory));
            assertEquals(0, e[0]);
        }
        factory = new CachedFactorialFactory(157, 1000);
        assertEquals(81, modFact(619, 157, e, factory));
        assertEquals(3, e[0]);
        assertEquals(1, modFact(0, 157, e, factory));
        assertEquals(0, e[0]);
        // assertEquals(27, modFact(10000000, 157, e, factory));
        // assertEquals(64101, e[0]);
        // data ga nai ;_;
    }

    @Test
    public void testModComb() {
        FactorialFactory factory = new CachedFactorialFactory(BASIC_MOD, 1000);
        assertEquals(210, modComb(10, 4, BASIC_MOD, factory));
        assertEquals(306934928, modComb(800, 321, BASIC_MOD, factory));

        factory = new CachedFactorialFactory(157, 1000);
        assertEquals(54, modComb(30, 9, 157, factory));
        assertEquals(31, modComb(200, 5, 157, factory));
        assertEquals(43, modComb(200, 42, 157, factory));
        assertEquals(0, modComb(200, 50, 157, factory));
        assertEquals(0, modComb(200, 77, 157, factory));
        assertEquals(0, modComb(200, 86, 157, factory));
        assertEquals(0, modComb(200, 100, 157, factory));

        assertEquals(107, modComb(349, 27, 157, factory));
        assertEquals(1, modComb(349, 35, 157, factory));
        assertEquals(0, modComb(349, 36, 157, factory));
        assertEquals(0, modComb(349, 52, 157, factory));
        assertEquals(0, modComb(349, 135, 157, factory));
        assertEquals(59, modComb(349, 160, 157, factory));
    }

}
