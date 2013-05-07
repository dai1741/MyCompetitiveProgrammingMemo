package jp.dai1741.competitive;

import static jp.dai1741.competitive.MathTools.*;

/**
 * 乗除関係のライブラリ
 */
public class ModTools {

    /**
     * 階乗を求める関数のインターフェース
     * 作る必要なかったのでは
     */
    static interface FactorialFactory {
        long fact(int n);
    }

    static class CachedFactorialFactory implements FactorialFactory {
        final long mod;
        final int n;
        final int maxSize;
        final long[] fact;

        CachedFactorialFactory(long mod, int n) {
            this.mod = mod;
            this.n = n;
            maxSize = (int) Math.min(mod, n);
            fact = new long[maxSize];
            fact[0] = 1;
            for (int i = 1; i < maxSize; i++) {
                fact[i] = fact[i - 1] * i % mod;
            }
        }

        @Override
        public long fact(int n) {
            if (n < maxSize) return fact[n];
            if (n < this.n || mod <= n) return 0;
            throw new IndexOutOfBoundsException();
        }
    }

    static class LinearFactorialFactory implements FactorialFactory {
        final long mod;

        LinearFactorialFactory(long mod) {
            this.mod = mod;
        }

        @Override
        public long fact(int n) {
            long f = 1;
            for (int i = 2; i <= n; i++) {
                f = f * i % mod;
            }
            return f;
        }
    }

    /**
     * @see ModTools#modComb(int, int, int, FactorialFactory)
     */
    static class CachedCombinationFactory {
        final long mod;
        final int n;
        final long[][] comb; // comb[i][j]: iCj

        CachedCombinationFactory(long mod, int n) {
            assert mod > n;
            this.mod = mod;
            this.n = n;
            comb = new long[n][n];
            comb[0][0] = 1;
            for (int i = 0; i < n; i++) {
                comb[i][0] = 1;
            }
            for (int i = 1; i < n; i++) {
                for (int j = 1; j <= i; j++) {
                    comb[i][j] = (comb[i - 1][j - 1] + comb[i - 1][j]) % mod;
                }
            }
        }

        public long combination(int n, int k) {
            return comb[n][Math.min(k, n - k)];
        }
    }

    static long[][] mul(long[][] mat, long[][] mat2, long mod) {
        int n = mat.length;
        int m = mat[0].length;
        int l = mat2[0].length;
        assert m == mat2.length;

        long[][] ret = new long[n][l];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < l; j++) {
                for (int k = 0; k < m; k++) {
                    ret[i][j] = (ret[i][j] + (mat[i][k] * mat2[k][j]) % mod) % mod;
                }
            }
        }
        return ret;
    }

    static long[][] makeIndentityMatrixLong(int n) {
        long[][] ret = new long[n][n];
        for (int i = 0; i < n; i++) {
            ret[i][i] = 1;
        }
        return ret;
    }

    /**
     * @return x^n (mod mod)
     * @see プログラミングコンテストチャレンジブック 第1版 p.125
     */
    static long pow(long x, long n, long mod) {
        long ret = 1;
        while (n > 0) {
            if (n % 2 == 1) ret = ret * x % mod;
            x = x * x % mod;
            n /= 2;
        }
        return ret;
    }

    /**
     * @return x^n (mod mod)
     * @see プログラミングコンテストチャレンジブック 第1版 p.180
     */
    static long[][] pow(long[][] x, long n, long mod) {
        long[][] ret = makeIndentityMatrixLong(x.length);
        while (n > 0) {
            if (n % 2 == 1) ret = mul(ret, x, mod);
            x = mul(x, x, mod);
            n /= 2;
        }
        return ret;
    }

    /**
     * @throws ArithmeticException gcd(a, mod)≠1のとき
     * @return integer x s.t. ax=1 (mod mod)
     * @see プログラミングコンテストチャレンジブック 第1版 p.242
     */
    static int modInverse(int a, int mod) {
        int[] xy = new int[2];
        if (extgcd(a, mod, xy) != 1) throw new ArithmeticException("no inverse exsits");
        return (mod + xy[0] % mod) % mod;
    }

    /**
     * @param size
     * @param p 素数
     * @return inv[i]がiの逆元となる配列inv
     * @see https://twitter.com/rng_58/status/312406292486565889
     */
    static int[] modInverseArray(int size, int p) {
        assert isPrime(p) && size < p;

        int[] inv = new int[size + 1];
        inv[1] = 1;
        for (int i = 2; i <= size; i++) {
            // https://twitter.com/rng_58/status/312413291647492096 より
            // i * (MOD/i) = MOD - MOD%i （除法の原理）
            // (MOD/i) * inv(MOD - MOD%i) = inv(i) （iと(MOD - MOD%i)の逆元を両辺にかける）
            // (MOD/i) * (MOD - inv(MOD%i)) = inv(i) （ax=1 (mod M) iff (M-a)(M-x)=1 (mod M) を利用）
            inv[i] = inv[p % i] * (p - p / i) % p;
        }
        return inv;
    }

    /**
     * 全てのiについて a_i*x=b_i (mod mods_i) を満たすxを求める。
     * 
     * @param a a_i
     * @param b b_i
     * @param mods m_i
     * @return x=ret[0], m=ret[1]
     * @throws ArithmeticException 解が存在しないとき
     * @see プログラミングコンテストチャレンジブック 第1版 p.243
     */
    static int[] solveLinearCongruence(int[] a, int[] b, int[] mods) {
        int x = 0;
        int m = 1;
        for (int i = 0; i < a.length; i++) {
            // 現時点での解： x = c (mod n), x=c+n*k, k>=0
            // 次の式にこの解を代入： a_i*x=b_i (mod mods_i)
            // a_i*(c+n*k)=b_i (mod mods_i)
            // a_i*n*k=b_i-a_i*c (mod mods_i)
            // k=(b_i-a_i*c)*(a_i*n)^-1 (mod mods_i)
            // ここで、 (a_i*n)^-1 exists iff gcd(a_i*n, mods_i) == 1 が成立。　
            // gcd(a_i*n, mods_i) ≠ 1のとき：
            //   - gcd(a_i*n, mods_i) が (b_i-a_i*c)を割り切るなら解あり
            //   - そうでなければ解なし
            // 新しい解(gcd==1とする)： x:=c+n*(b_i-a_i*c)*(a_i*n)^-1, n*=mods_i

            // see also:
            // http://www.trans4mind.com/personal_development/mathematics/numberTheory/CongruenceEquationsLinear.htm#mozTocId540671

            int rhs = b[i] - a[i] * x;
            int lhsDivedK = a[i] * m;
            int d = gcd(lhsDivedK, mods[i]);
            if (rhs % d != 0) throw new ArithmeticException("answer doesnt exist");
            int k = (rhs / d * modInverse(lhsDivedK / d, mods[i] / d)) % (mods[i] / d);
            x += m * ((k + mods[i] / d) % (mods[i] / d));
            m *= mods[i] / d;
        }
        return new int[] { x % m, m };
    }

    /**
     * n!=a*p^e を満たす最大のeと0でないaを求める。
     * 
     * @param n
     * @param p 素数
     * @param e 出力変数
     * @param factory
     * @return a in (0,p)
     * @see プログラミングコンテストチャレンジブック 第1版 p.244
     */
    static long modFact(long n, int p, int[] e, FactorialFactory factory) {
        e[0] = 0;
        if (n == 0) return 1;
        long ret = modFact(n / p, p, e, factory);
        e[0] += n / p;
        if (n / p % 2 != 0) return (int) (ret * (p - factory.fact((int) (n % p))) % p);
        else return (int) (ret * factory.fact((int) (n % p)) % p);
    }

    /**
     * @param n
     * @param k
     * @param p 素数
     * @param factory
     * @return nCk mod p
     * @see プログラミングコンテストチャレンジブック 第1版 p.245
     * @see MathTools#comb(long, long, long)
     */
    static long modComb(long n, long k, int p, FactorialFactory factory) {
        if (n < 0 || k < 0 || n < k) {
            return 0;
        }
        int[] e1 = new int[1], e2 = new int[1], e3 = new int[1];
        long a1 = modFact(n, p, e1, factory);
        long a2 = modFact(k, p, e2, factory);
        long a3 = modFact(n - k, p, e3, factory);
        if (e1[0] > e2[0] + e3[0]) {
            // この条件が常に成立しない（常にn<pである）ならmodInverseで直接combを計算することもできる
            return 0;
        }
        return a1 * modInverse((int) (a2 * a3 % p), p) % p;
    }

    /**
     * 先に約分してからnCkを計算する。O(min(k, n-k) * log log min(k, n-k))
     * 
     * accepted in: http://arc009.contest.atcoder.jp/submissions/49430
     * 
     * @param mod 法。合成数も可能。
     * @return nCk mod m
     * @see http://d.hatena.ne.jp/kadzus/20081211/1229023326
     * @see ModTools#modComb(int, int, int, ModTools.FactorialFactory)
     */
    static long modComb(long n, long kOrg, long mod) {
        int k = (int) Math.min(kOrg, n - kOrg);
        if (k < 0) return 0;
        long[] bunsi = new long[k];
        int[] bunbo = new int[k];

        for (int i = 0; i < k; i++) {
            bunsi[i] = n - k + i + 1;
            bunbo[i] = i + 1;
        }
        for (int i = 2; i <= k; i++) {
            int pivot = bunbo[i - 1];
            if (pivot == 1) continue;
            int offset = (int) ((n - k) % i);  // 割り切れる最初の位置
            for (int j = i - 1; j < k; j += i) {
                bunsi[j - offset] /= pivot;
                bunbo[j] /= pivot;
            }
        }
        long c = 1;
        for (int i = 0; i < k; i++) {
            c = c * (bunsi[i] % mod);
            c %= mod;
        }
        return c;
    }

}
