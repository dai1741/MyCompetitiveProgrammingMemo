package jp.dai1741.competitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;


/**
 * 数学関係のライブラリ。かなり分類が適当。
 */
public class MathTools {

    static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    static long lcm(int a, int b) {
        if (a * b == 0) return 0;
        return (long) a * b / gcd(a, b);
    }

    /**
     * ax+by=gcd(a,b)を解く。
     * 
     * @param a
     * @param b
     * @param xy 出力用変数。元の内容は上書きされる。x=xy[0], y=xy[1]
     * @return gcd(a,b)
     * @see プログラミングコンテストチャレンジブック 第1版 p.120
     */
    static int extgcd(int a, int b, int[] xy) {
        assert a >= 0 && b >= 0 && xy.length == 2;
        int d = a;
        if (b != 0) {
            d = extgcd(b, a % b, xy);
            swap(xy, 0, 1);
            xy[1] -= a / b * xy[0];
        }
        else {
            xy[0] = 1;
            xy[1] = 0;
        }
        return d;
    }

    boolean[] isPrime;
    int[] primes;
    int numPrimes;

    public void makePrimesArray(int n) {
        assert 1 <= n;
        isPrime = new boolean[n + 1];
        primes = new int[n > 1000 ? n / 4 : n];
        numPrimes = 0;
        Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;
        int sqrtN = (int) Math.ceil(Math.sqrt(n) + 0.5);
        for (int i = 0; i <= n; i++) {
            if (isPrime[i]) {
                primes[numPrimes++] = i;
                if (i <= sqrtN) for (int j = i * 2; j <= n; j += i) {
                    isPrime[j] = false;
                }
            }
        }
    }

    /** isLargePrime[i] == true iff (i+a) is a prime */
    boolean[] isLargePrime;

    /**
     * [a,b)の範囲の素数表を作る
     * 
     * @param a
     * @param b
     * @see プログラミングコンテストチャレンジブック 第1版 p.124
     */
    public void makeSegmentPrimesArray(long a, long b) {
        assert 2 <= a && a < b;
        int sqrtB = (int) Math.floor(Math.sqrt(b) + 0.05);
        makePrimesArray(sqrtB);
        int n = (int) (b - a);
        isLargePrime = new boolean[n];
        Arrays.fill(isLargePrime, true);
        for (int i = 0; i < numPrimes; i++) {
            int p = primes[i];
            for (long j = Math.max((a + p - 1) / p, 2L) * p; j < b; j += p) {
                isLargePrime[(int) (j - a)] = false;
            }
        }
    }

    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * 試しわりで素数判定を行う。O(√n).
     */
    static boolean isPrime(int n) {
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return n > 1;
    }

    /**
     * 前もって作成した素数配列を使って素数判定を行う。O(log(n))。
     */
    boolean isPrimeWithPrimeArray(int n) {
        assert numPrimes > 0 && Math.pow(isPrime.length - 1, 2) >= n;
        for (int i = 0; i < numPrimes; i++) {
            int p = primes[i];
            if (p * p > n) break;
            if (n % p == 0) return false;
        }
        return n > 1;
    }

    /**
     * @return ソートされていないnの約数のリスト
     */
    static ArrayList<Integer> getDivisors(int n) {
        ArrayList<Integer> divisors = new ArrayList<Integer>();
        int sqrtN = (int) Math.floor(Math.sqrt(n) + 0.0005);
        for (int i = 1; i <= sqrtN; i++) {
            if (n % i == 0) {
                divisors.add(i);
                if (i != n / i) divisors.add(n / i);
            }
        }
        return divisors;
    }

    static TreeMap<Integer, Integer> getPrimeFactors(int n) {
        TreeMap<Integer, Integer> factors = new TreeMap<Integer, Integer>();
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                int count = 0;
                do {
                    n /= i;
                    count++;
                } while (n % i == 0);
                factors.put(i, count);
            }
        }
        if (n != 1) factors.put(n, 1);
        return factors;
    }

    TreeMap<Integer, Integer> getPrimeFactorsWithPrimeArray(int n) {
        TreeMap<Integer, Integer> factors = new TreeMap<Integer, Integer>();
        for (int i = 0; i < numPrimes; i++) {
            int p = primes[i];
            if (p * p > n) break;
            if (n % p == 0) {
                int count = 0;
                do {
                    n /= p;
                    count++;
                } while (n % p == 0);
                factors.put(p, count);
            }
        }
        if (n != 1) factors.put(n, 1);
        return factors;
    }

    static ArrayList<Integer> getPrimeFactorsIgnoringCount(int n) {
        ArrayList<Integer> factors = new ArrayList<Integer>();
        if (n < 2) return factors;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                factors.add(i);
                do {
                    n /= i;
                } while (n % i == 0);
            }
        }
        if (n != 1) factors.add(n);
        return factors;
    }

    static TreeMap<Integer, Integer> getMoebiusMapForFactorsOf(int n) {
        List<Integer> factors = getPrimeFactorsIgnoringCount(n);
        TreeMap<Integer, Integer> moebius = new TreeMap<Integer, Integer>();
        int m = factors.size();
        for (int i = 0; i < 1 << m; i++) {
            int mu = 1;
            int d = 1;
            for (int j = 0; j < m; j++) {
                if ((i >> j & 1) != 0) {
                    mu *= -1;
                    d *= factors.get(j);
                }
            }
            moebius.put(d, mu);
        }
        return moebius;
    }

    /**
     * Ax=b を解く。
     * 
     * @param a 行列A。内容は破壊されない
     * @param b ベクトルb。内容は破壊されない
     * @return Ax=bを満たすベクトルx
     * @throws IllegalArgumentException 行列とベクトルのサイズが一致しないとき
     * @throws ArithmeticException 解が存在しないか一意でないとき
     * @see プログラミングコンテストチャレンジブック 第1版 p.238
     */
    static double[] solveLinearEquationsSystemWithGaussJordan(double[][] a, double[] b) {
        int n = a.length;
        if (n != a[0].length || n != b.length) throw new IllegalArgumentException();

        double[][] B = new double[n][n + 1];
        final double EPS = 0.0000000001;

        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, B[i], 0, n);
            B[i][n] = b[i];
        }

        for (int i = 0; i < n; i++) {
            int pivot = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(B[j][i]) > Math.abs(B[pivot][i])) pivot = j;
            }
            double[] temp = B[i];
            B[i] = B[pivot];
            B[pivot] = temp;

            if (Math.abs(B[i][i]) < EPS) throw new ArithmeticException(
                    "no answer or more than one answer exists");

            for (int j = i + 1; j <= n; j++) {
                B[i][j] /= B[i][i];
            }
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                for (int k = i + 1; k <= n; k++) {
                    B[j][k] -= B[j][i] * B[i][k];
                }
            }
        }
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = B[i][n];
        }
        return x;
    }

    /**
     * @return 行列とベクトルの積
     */
    static double[] mul(double[][] mat, double[] v) {
        int n = mat.length;
        int m = mat[0].length;
        assert m == v.length;
        double[] ret = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                ret[i] += mat[i][j] * v[j];
            }
        }
        return ret;
    }

    /**
     * @return 行列同士の積
     */
    static double[][] mul(double[][] mat1, double[][] mat2) {
        int n = mat1.length;
        int m = mat1[0].length;
        int l = mat2[0].length;
        assert m == mat2.length;

        double[][] ret = new double[n][l];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < l; j++) {
                for (int k = 0; k < m; k++) {
                    ret[i][j] += mat1[i][k] * mat2[k][j];
                }
            }
        }
        return ret;
    }

    static double[][] makeIndentityMatrixDouble(int n) {
        double[][] ret = new double[n][n];
        for (int i = 0; i < n; i++) {
            ret[i][i] = 1;
        }
        return ret;
    }

    /**
     * @param n
     * @return n未満のnと互いと素な数の個数φ(n)
     * @see プログラミングコンテストチャレンジブック 第1版 p.242
     */
    static int getEulerφ(int n) {
        int ret = n;
        for (int p : getPrimeFactorsIgnoringCount(n)) {
            ret = ret / p * (p - 1);
        }
        return ret;
    }

    int[] euler;

    /**
     * @param n
     * @see プログラミングコンテストチャレンジブック 第1版 p.242
     */
    void makeEulerφArray(int n) {
        euler = new int[n];
        for (int i = 0; i < n; i++) {
            euler[i] = i;
        }
        for (int i = 2; i < n; i++) {
            if (euler[i] == i) {
                for (int j = i; j < n; j += i) {
                    euler[j] = euler[j] / i * (i - 1);
                }
            }
        }
    }

    /**
     * O(min(k, n-k)).
     * 
     * @param n 非負の整数
     * @param k
     * @return nCk
     * @see ModTools.CachedCombinationFactory
     * @see ModTools#modComb(long, int, long)
     */
    static long comb(int n, int k) {
        long ret = 1;
        k = Math.min(k, n - k);
        if (k < 0) return 0;
        for (int i = 0; i < k; i++) {
            ret = ret * (n - i) / (i + 1);
        }
        return ret;
    }

    /**
     * 順列。SGIのnext_permutaionを参考に実装した。
     */
    static class Permutations {
        final int[] data;

        public Permutations(int size) {
            data = new int[size];
            for (int i = 0; i < size; i++) {
                data[i] = i;
            }
        }

        public Permutations(int[] data) {
            this.data = data;
        }

        boolean nextPermutaion() {
            for (int i = data.length - 2; i >= 0; i--) {
                if (data[i] < data[i + 1]) {
                    int swapI = i + 1;
                    for (; swapI + 1 < data.length && data[i] < data[swapI + 1]; swapI++)
                        continue;
                    swap(data, i, swapI);
                    for (int j = i + 1, k = data.length - 1; j < k; j++, k--)
                        swap(data, j, k);
                    return true;
                }
            }
            Arrays.sort(data);
            return false;
        }
    }

}
