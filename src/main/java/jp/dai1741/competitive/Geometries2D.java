package jp.dai1741.competitive;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * 以下が前提
 * - 右方向がx軸正、上方向がy軸正
 * - 角は左回りが正、外積は右手系
 * 各toString()はデバッグ用途のみで使っているので実装する必要はない
 */
public class Geometries2D {

    static final double EPS = 1e-9;

    static boolean approxEquals(double a, double b) {
        return Math.abs(a - b) < EPS;
    }

    static class Point extends Point2D.Double implements Comparable<Point> {

        public Point() {
        }

        public Point(double x, double y) {
            super(x, y);
            // xとyはfinalではないが、このライブラリ関数の一部は変更されないことを前提としているので注意。
        }

        /** 内積　dot(v1,v2)=|v1||v2|cosθ */
        double dot(Point p) {
            return x * p.x + y * p.y;
        }

        /** 外積　cross(v1,v2)=|v1||v2|sinθ */
        double cross(Point p) {
            return x * p.y - y * p.x;
        }

        double distanceSqr() {
            return x * x + y * y;
        }

        double distance() {
            return Math.hypot(x, y);
        }

        Point add(Point p) {
            return new Point(x + p.x, y + p.y);
        }

        Point multiply(double k) {
            return new Point(k * x, k * y);
        }

        Point multiply(Point p) {  // complex mul: (x+yi)*(p.x+p.yi)
            return new Point(x * p.x - y * p.y, x * p.y + p.x * y);
        }

        Point subtract(Point p) {
            return new Point(x - p.x, y - p.y);
        }

        @Override
        public boolean equals(Object obj) {  // この関数はEclipseで生成して座標の比較だけ書き換えればいい
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Point other = (Point) obj;
            if (!approxEquals(x, other.x)) return false;
            if (!approxEquals(y, other.y)) return false;
            return true;
        }

        @Override
        public int compareTo(Point o) {
            if (!approxEquals(x, o.x)) return (int) Math.signum(x - o.x);
            if (!approxEquals(y, o.y)) return (int) Math.signum(y - o.y);
            return 0;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

    }

    static class Line {
        Point a, b;  // タイプ数減らすために書いてないが頂点たちはfinal

        // コンストラクタとequalsは自動生成でいい
        public Line(Point a, Point b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Line other = (Line) obj;
            if (a == null) {
                if (other.a != null) return false;
            }
            else if (!a.equals(other.a)) return false;
            if (b == null) {
                if (other.b != null) return false;
            }
            else if (!b.equals(other.b)) return false;
            return true;
        }

        @Override
        public String toString() {
            return a + "->" + b;
        }
    }

    /**
     * @return ベクトル a->b から見てベクトル b->c が左向きなら1、右向きなら-1。
     *         後はコード参照。
     * @see http://www.prefield.com/algorithm/geometry/ccw.html
     */
    static int ccw(Point a, Point b, Point c) {
        b = b.subtract(a);
        c = c.subtract(a);
        if (b.cross(c) > EPS) return +1;                  // counter clockwise
        if (b.cross(c) + EPS < 0) return -1;              // clockwise
        if (b.dot(c) + EPS < 0) return +2;                // c--a--b on line and a!=c
        if (b.distanceSqr() < c.distanceSqr()) return -2; // a--b--c on line or a==b　※基本的にa==bとなるべきでない　
        return 0;                                         // a--c--b on line or b==c
    }

    /*
     * 交差判定
     * @see http://www.prefield.com/algorithm/geometry/intersection.html
     */

    /** @return 直線aと直線bが交差しているならtrue */
    static boolean intersectsLL(Point a1, Point a2, Point b1, Point b2) {
        Point a = a2.subtract(a1);
        Point b = b2.subtract(b1);
        return !intersectsLP(a, b, new Point()) || intersectsLP(a1, b1, b2);
    }

    /** @return 直線aと線分bが交差しているならtrue */
    static boolean intersectsLS(Point a1, Point a2, Point b1, Point b2) {
        // a1を原点に移動
        Point a = a2.subtract(a1);
        b1 = b1.subtract(a1);
        b2 = b2.subtract(a1);
        return a.cross(b1) * a.cross(b2) < EPS;  // 線分bが直線aをまたぐならtrue
    }

    /** @return 直線aと点bが交差しているならtrue */
    static boolean intersectsLP(Point a1, Point a2, Point b) {
        int ccw = ccw(a1, a2, b);
        return ccw != 1 && ccw != -1;
    }

    static boolean intersectsSS(Point a1, Point a2, Point b1, Point b2) {
        // 互いの端点が自身の左右に分かれているならtrue
        return ccw(a1, a2, b1) * ccw(a1, a2, b2) <= 0
                && ccw(b1, b2, a1) * ccw(b1, b2, a2) <= 0;
    }

    static boolean intersectsSP(Point a1, Point a2, Point b) {
        return ccw(a1, a2, b) == 0;
    }

    /*
     * 距離
     * @see http://www.prefield.com/algorithm/geometry/distance.html
     */

    /** @return 直線aに点pを投影したときの点 */
    static Point projection(Point a1, Point a2, Point p) {
        Point a = a2.subtract(a1);
        p = p.subtract(a1);
        double t = a.dot(p) / a.distanceSqr();
        // |a||p|cosθ=t|a|^2, a,tが固定でθが動くとき、点pの軌跡は直線
        return a1.add(a.multiply(t));
    }

    /** @return 点pを直線aで鏡映変換したときの点 */
    static Point reflection(Point a1, Point a2, Point p) {
        Point dir = projection(a1, a2, p).subtract(p);
        return p.add(dir.multiply(2));
    }

    static double distanceLP(Point a1, Point a2, Point p) {
        return projection(a1, a2, p).distance(p);
    }

    static double distanceLL(Point a1, Point a2, Point b1, Point b2) {
        if (intersectsLL(a1, a2, b1, b2)) return 0;
        return distanceLP(a1, a2, b1);
    }

    static double distanceLS(Point a1, Point a2, Point b1, Point b2) {
        if (intersectsLS(a1, a2, b1, b2)) return 0;
        return Math.min(distanceLP(a1, a2, b1), distanceLP(a1, a2, b2));
    }

    static double distanceSP(Point a1, Point a2, Point b) {
        Point r = projection(a1, a2, b);
        // 投影した点が線分上にあるなら、点pからその点への距離を返せばいい
        if (intersectsSP(a1, a2, r)) return r.distance(b);
        return Math.min(b.distance(a1), b.distance(a2));
    }

    static double distanceSS(Point a1, Point a2, Point b1, Point b2) {
        if (intersectsSS(a1, a2, b1, b2)) return 0;
        return Math.min(Math.min(distanceSP(a1, a2, b1), distanceSP(a1, a2, b2)), Math
                .min(distanceSP(b1, b2, a1), distanceSP(b1, b2, a2)));
    }

    /** @see http://www.deqnotes.net/acmicpc/2d_geometry/lines#intersection_of_lines */
    static Point crosspointLL(Point a1, Point a2, Point b1, Point b2) {
        // ベクトルaの長さをd1/d2倍すると直線bに接するようにd1,d2を設定
        Point a = a2.subtract(a1);
        Point b = b2.subtract(b1);
        double d1 = b.cross(b1.subtract(a1));
        double d2 = b.cross(a);
        if (Math.abs(d1) < EPS && Math.abs(d2) < EPS) return a1;  // same line
        // 同一直線時の線分の交点を考えるなら return intersectsSP(b1,b2,a1) ? a1 : a2; としてはどうか
        if (Math.abs(d2) < EPS) throw new IllegalArgumentException(
                "PRECONDITION NOT SATISFIED");
        return a1.add(a.multiply(d1 / d2));
    }

    /*
     * 円の交差判定と距離判定
     * サークルの先輩が作った資料を参考にして書いた（があまり原形をとどめていない）
     */

    /** @return 直線aと円bの距離 */
    static double distanceLC(Point a1, Point a2, Point b, double r) {
        return Math.max(distanceLP(a1, a2, b) - r, 0);
    }

    static double distanceSC(Point a1, Point a2, Point b, double r) {
        double dSqr1 = b.subtract(a1).distanceSqr();
        double dSqr2 = b.subtract(a2).distanceSqr();
        boolean a1InCircle = dSqr1 < r * r;
        boolean a2InCircle = dSqr2 < r * r;
        if (a1InCircle && a2InCircle) {  // 線分の両端が円の中
            return r - Math.sqrt(Math.max(dSqr1, dSqr2));
        }
        if (a1InCircle ^ a2InCircle) return 0;  // 端点が円の中と外なら明らかに一点で交わる
        // 線分の端点が完全に円の外ならば点との距離判定でよい
        return Math.max(distanceSP(a1, a2, b) - r, 0);
    }

    /**
     * @return 交差点の組。ret.length は交差点の個数となる。
     * @see http://homepage1.nifty.com/gfk/circle-line.htm
     */
    static Point[] crosspointLC(Point a1, Point a2, Point b, double r) {
        // 直線aに円の中心から垂線の足をたらし、直線方向のベクトルを得て、
        // 三平方の定理で足から交点への距離を求める
        Point foot = projection(a1, a2, b);
        double footLenSqr = foot.distanceSq(b);
        Point dir = a2.subtract(a1);
        if (approxEquals(r * r, footLenSqr)) {  // 一点で接する場合（誤差死回避のため分岐）
            return new Point[] { foot };
        }
        if (r * r < footLenSqr) return new Point[0];

        double len = Math.sqrt(r * r - footLenSqr) / dir.distance();
        dir = dir.multiply(len);
        return new Point[] { foot.add(dir), foot.subtract(dir), };
    }

    /** @return 2円の関係を表す数値。値は適当。この関数いらないのでは。 */
    static int intersectionCC(Point a, double ar, Point b, double br) {
        double dSqr = a.distanceSq(b);
        if (approxEquals(dSqr, (ar + br) * (ar + br))) return 2;  // 外接する
        if (approxEquals(dSqr, (ar - br) * (ar - br))) return 3;  // 内接する
        if (dSqr < (ar - br) * (ar - br)) return -1;  // 片方の円はもう片方の内部
        if ((ar + br) * (ar + br) < dSqr) return -2;  // 片方の円はもう片方と全然関係がない
        return 1;  // 2点で交わる
    }

    static double distanceCC(Point a, double ar, Point b, double br) {
        double d = a.distance(b);
        // 片方の円はもう片方の内部
        if (d + EPS < Math.abs(ar - br)) return Math.abs(ar - br) - d;
        // 片方の円はもう片方と離れている
        if (ar + br + EPS < d) return d - ar - br;
        return 0;  // 接するか交わっている
    }

    /** @return 2円の交差点の配列。配列の要素数は交差点の個数となる。 */
    static Point[] crosspointCC(Point a, double ar, Point b, double br) {
        Point ab = b.subtract(a);
        double d = ab.distance();

        // 余弦定理で円の中心から2円の交点を結ぶ直線への距離を求め、
        // 三平方の定理で2円の交点を結ぶ直線の長さを求める
        double lenToCross = (ab.distanceSqr() + ar * ar - br * br) / (2 * d);
        double lenRef = Math.sqrt(ar * ar - lenToCross * lenToCross);
        if (d < EPS || ar < Math.abs(lenToCross)) return new Point[0];  // 交点なし（か無限個存在）

        // 長さから交点の位置を求める
        Point abN = ab.multiply(new Point(0, 1)).multiply(lenRef / d);
        Point crosspoint = a.add(ab.multiply(lenToCross / d));
        Point[] ps = new Point[] { crosspoint.add(abN), crosspoint.subtract(abN) };
        return ps[0].equals(ps[1]) ? new Point[] { ps[0] } : ps;  // 交点が1つのときを処理（不要なら消す）
    }

    /**
     * @param b 円の外部の点
     * @return 接点2つ。交点が1つのとき（bが円周上）のときも2要素の配列を返す。
     */
    static Point[] tangent(Point a, double ar, Point b) {
        Point ba = a.subtract(b);
        double baLen = ba.distance();
        if (baLen < ar) return new Point[0];  // 点が円の内部＝接線なし

        // 三角比で接点方向にベクトルを伸ばす（asin使ってもいいがJavaでは大して実装量減らないしロバストでない）
        double cos = Math.sqrt(ba.distanceSqr() - ar * ar) / baLen;
        double sin = ar / baLen;
        Point dir = ba.multiply(new Point(cos, sin));
        Point dir2 = ba.multiply(new Point(cos, -sin));
        return new Point[] { b.add(dir.multiply(cos)), b.add(dir2.multiply(cos)) };
    }

    /**
     * @return 2円の共通接線の組。ret[0]に共通外接線、ret[1]に共通内接線。
     *         対応する接線がない（or接線が無数にある）場合、ret[i]はnull。
     *         ret[i][j]に各接線が格納される。ret[i][j].aに円aの接点、ret[i][j].bに円bの接点。
     *         ret[i][0].aは線分ABから見て左側に、ret[i][1].aは線分ABから見て右側にある。
     *         接線が重なる場合、ret[i][1]はnull。外接線と内接線が一致する場合は重複して列挙する。
     * @see https://github.com/infnty/acm/blob/master/lib/geometry/CircleTangents.java
     */
    static Line[][] tangentLines(Point a, double ar, Point b, double br) {
        Line[][] lines = new Line[2][];
        double d = a.distance(b);
        Point v = b.subtract(a).multiply(1 / d);  // aからbへの単位ベクトル

        for (int sign = +1, i = 0; sign >= -1; sign -= 2, i++) {  // 外接線 -> 内接線
            double sin = (ar - sign * br) / d;  // ベクトルabが斜辺、半径の和/差は正弦（符号付き）
            if (sin * sin > 1 + EPS || sin != sin) break;  // 距離よりも半径の和/差の方が大きい＝可能な接線を調べつくした

            lines[i] = new Line[2];
            double cos = Math.sqrt(Math.max(1 - sin * sin, 0));

            for (int j = 0; j < 2; j++) {  // 2つの接線を求める
                // 接線の単位法線ベクトルを得る。(1-j*2)は符号
                Point n = v.multiply(new Point(sin, cos * (1 - j * 2)));
                lines[i][j] = new Line(a.add(n.multiply(ar)), b
                        .add(n.multiply(sign * br)));  // 内接線は片方逆側になる
                // 接線が重なるときはret[i][j]は線分にならないので以下のように方向成分を足しておくと便利かもしれない 
                // if (cos < EPS) ret[i][j][1] = ret[i][j][1].add(n.multiply(new Point(0,1)));
                if (cos < EPS) break;  // 接線が重なっている（重複を区別しないならこの行を削除）
            }
            // i++; // 内接線と外接線を区別しないならiのインクリメント位置をここに変える
        }
        return lines;  // 内接線と外接線を区別せずret.lengthで接線の組の数を得るには
                      // return Arrays.copyOf(ret, i); とする（要Java6）
    }

    /** @return 三角形の外心（3点を通る円の中心） */
    static Point circumcenter(Point a, Point b, Point c) {
        // 2本の垂直二等分線の交点を求める
        a = a.subtract(c).multiply(0.5);
        Point an = a.multiply(new Point(0, 1));
        b = b.subtract(c).multiply(0.5);
        Point bn = b.multiply(new Point(0, 1));
        return crosspointLL(a, a.add(an), b, b.add(bn)).add(c);
    }

    /** @return 三角形の内心（3辺に接する円の中心） */
    static Point incenter(Point a, Point b, Point c) {
        // 角の二等分線の交点を求めたほうが直感的だが、重み付き平均の方が軽実装でロバスト
        double lenA = b.distance(c);
        double lenB = c.distance(a);
        double lenC = a.distance(b);
        return a.multiply(lenA).add(b.multiply(lenB)).add(c.multiply(lenC)).multiply(
                1 / (lenA + lenB + lenC));
    }

    /** @return 点aと点bを通り、半径がrの円の中心 */
    static Point[] circlesThru2PointsWithRadius(Point a, Point b, double r) {
        Point abH = b.subtract(a).multiply(0.5);
        double dSqr = abH.distanceSqr();
        if (dSqr == 0 || dSqr > r * r) return new Point[0];  // 2点が一致するか離れすぎている
        double dN = Math.sqrt(r * r - dSqr);  // 三平方の定理で中点から円の中心への距離を求める
        Point normal = abH.multiply(new Point(0, 1)).multiply(dN / Math.sqrt(dSqr));
        if (dN == 0) return new Point[] { a.add(abH) };
        return new Point[] { a.add(abH).add(normal), a.add(abH).subtract(normal) };
    }

    /** @return 点aと点bを通り、直線lに接する円の中心 */
    static Point[] circlesThru2PointsWithTangent(Point a, Point b, Point l1, Point l2) {
        // 点aを通り直線lに接する円の中心pの軌跡は、lの法線ベクトルnとl上の任意の点cを用いて
        // |n||p-a| = dot(n,p-c)
        // と表される。またpはaとbの垂直二等分線（これをmとおく）上の1点なので、ある数kを用いて
        // p = (a+b)/2 + km
        // と表せる。この2式を連立してkを求めれば求めるべき円が得られる。

        Point n = l2.subtract(l1).multiply(new Point(0, 1));
        // Point abC = b.subtract(a).multiply(0.5);
        Point m = b.subtract(a).multiply(new Point(0, 0.5));
        Point abA = a.add(b).multiply(0.5);

        double nDotM = n.dot(m);
        double rConsts = abA.subtract(l1).dot(n);
        double qa = n.distanceSqr() * m.distanceSqr() - nDotM * nDotM;
        double qb = -rConsts * nDotM;  // abC.dot(m)==0
        double qc = n.distanceSqr() * m.distanceSqr() - rConsts * rConsts;  // |m|==|abC|
        double discriminant = qb * qb - qa * qc;  // qa*k^2 + 2*qb*k + qc = 0

        if (discriminant < -EPS) return new Point[0];
        if (approxEquals(qa, 0)) return approxEquals(qb, 0)
                ? new Point[0]
                : new Point[] { abA.add(m.multiply(-qc / qb / 2)) };
        double t = -qb / qa;
        if (discriminant < EPS) return new Point[] { abA.add(m.multiply(t)) };
        return new Point[] {
                abA.add(m.multiply(t + Math.sqrt(discriminant) / qa)),
                abA.add(m.multiply(t - Math.sqrt(discriminant) / qa)) };
    }

    /**
     * psは多角形でなくてよい。O(n)？
     * 
     * @return 最小包含円の中心
     * @see http://www.ipsj.or.jp/07editj/promenade/4309.pdf
     */
    static Point minEnclosingCircle(Point[] ps) {
        int n = ps.length;
        Point c = new Point();
        double move = 0.5;
        for (int i = 0; i < 39; i++) {  // 2^(-39-1) \approx 0.9e-12
            for (int t = 0; t < 50; t++) {
                double max = 0;
                int k = 0;
                for (int j = 0; j < n; j++) {
                    if (max < ps[j].distanceSq(c)) {
                        max = ps[j].distanceSq(c);
                        k = j;
                    }
                }
                c = c.add(ps[k].subtract(c).multiply(move));
            }
            move /= 2;
        }
        return c;
    }

    /*
     * 凸多角形
     */

    /**
     * Andrew's Monotone Chain。引数の配列はソートされる。
     * 
     * @return ccwな凸包
     * @see http://www.prefield.com/algorithm/geometry/convex_hull.html
     * @see http://en.wikibooks.org/wiki/Algorithm_Implementation/Geometry/Convex_hull/Monotone_chain
     */
    static Point[] convexHull(Point[] ps) {
        Arrays.sort(ps);
        int n = ps.length;
        Point[] hull = new Point[2 * n];
        int k = 0; // k == hull.size()
        // 下側凸包
        for (int i = 0; i < n; hull[k++] = ps[i++]) {
            // ↓同一直線上の点を含まないなら「<= 0」に、含むなら「== -1」に 
            while (k >= 2 && ccw(hull[k - 2], hull[k - 1], ps[i]) <= 0)
                --k;  // 最後のベクトルから見て候補点が右側にあるなら、最後の点をはずす
        }
        // 下側凸包の後に上側を加える
        int lowerEnd = k;
        for (int i = n - 2; i >= 0; hull[k++] = ps[i--]) {
            while (k > lowerEnd && ccw(hull[k - 2], hull[k - 1], ps[i]) <= 0)
                --k;  // ↑同一直線上の点を含まないなら「<= 0」に、含むなら「== -1」に 
        }
        Point[] ret = new Point[k - 1];
        System.arraycopy(hull, 0, ret, 0, k - 1);  // 最後は重複しているので消す
        return ret;  // Arrays.copyOf(hull, k - 1);
    }

    /**
     * O(n)。
     * 
     * @return 多角形が左回りの凸（縮退可）ならtrue
     * @see http://www.prefield.com/algorithm/geometry/isconvex.html
     */
    static boolean isCcwConvex(Point[] polygon) {
        int n = polygon.length;
        for (int i = 0; i < n; i++) {
            Point cur = polygon[i];
            Point next = polygon[(i + 1) % n];
            Point next2 = polygon[(i + 2) % n];
            if (ccw(cur, next, next2) == -1) return false;
            // ↑縮退を認めないなら != 1 とする
        }
        return true;
    }

    /**
     * O(n)。凸形の向きはどちらでもいい
     */
    static boolean isConvex(Point[] polygon) {
        int n = polygon.length;
        boolean isClockwise = true;
        boolean isCounterClockwise = true;
        for (int i = 0; i < n; i++) {
            Point cur = polygon[i];
            Point next = polygon[(i + 1) % n];
            Point next2 = polygon[(i + 2) % n];
            int ccw = ccw(cur, next, next2);
            if (ccw == 1) {
                if (!isCounterClockwise) return false;
                isClockwise = false;
            }
            else if (ccw == -1) {
                if (!isClockwise) return false;
                isCounterClockwise = false;
            }
        }
        return true;
    }

    /**
     * O(n)
     * 
     * @return 内部に存在するなら1、線分上なら2、外部なら0
     */
    static int isInConvex(Point p, Point[] polygon) {
        int n = polygon.length;
        int dir = ccw(polygon[0], polygon[1], p);
        for (int i = 0; i < n; i++) {
            Point cur = polygon[i];
            Point next = polygon[(i + 1) % n];
            int ccw = ccw(cur, next, p);
            if (ccw == 0) return 2;  // 線分上に存在
            if (ccw != dir) return 0;
        }
        return 1;
    }

    /**
     * O(log n)
     * 多角形は縮退していないこと
     * 
     * @return 内部に存在するなら1、線分上なら2、外部なら0
     * @see http://www.prefield.com/algorithm/geometry/convex_contains.html
     * @see http://stackoverflow.com/a/5224119/897061
     */
    static int isInCcwConvex(Point p, Point[] polygon) {
        int n = polygon.length;
        // 凸形の内点を任意に選ぶ
        Point g = polygon[0].add(polygon[n / 3]).add(polygon[n * 2 / 3])
                .multiply(1.0 / 3);
        if (g.equals(p)) return 1;
        Point gp = p.subtract(g);

        int l = 0;
        int r = n;
        while (l + 1 < r) {  // 点gにより領域をセクタに分割し、二分探索で点pのあるセクタを探す
            int mid = (l + r) / 2;
            Point gl = polygon[l].subtract(g);
            Point gm = polygon[mid].subtract(g);
            if (gl.cross(gm) > 0) { // glからgmへのセクタが鋭角である
                // glからgmの範囲に点pがあるか調べる
                if (gl.cross(gp) >= 0 && gm.cross(gp) <= 0) r = mid;
                else l = mid;
            }
            else {
                // 鋭角側に点があるか調べる
                if (gm.cross(gp) >= 0 && gl.cross(gp) <= 0) l = mid;
                else r = mid;
            }
        }
        r %= n;
        double cross = polygon[l].subtract(p).cross(polygon[r].subtract(p));
        return approxEquals(cross, 0) ? 2 : cross < 0 ? 0 : 1;
    }

    /**
     * O(n)。
     * JavaならPolygonかPath2Dで図を作ってArea#contains()使ったほうが楽。どれくらい誤差があるか知らないけど。
     * 
     * @return 内部に存在するなら1、線分上なら2、外部なら0
     * @see http://www.prefield.com/algorithm/geometry/contains.html
     */
    static int isInPolygon(Point p, Point[] polygon) {
        int n = polygon.length;
        boolean in = false;
        for (int i = 0; i < n; i++) {
            Point a = polygon[i].subtract(p);
            Point b = polygon[(i + 1) % n].subtract(p);
            if (approxEquals(a.cross(b), 0) && a.dot(b) < EPS) return 2;
            if (a.y > b.y) {  // 点との位置関係を求めるためベクトルabを上向きにする
                Point temp = a;
                a = b;
                b = temp;
            }
            // 線分が直線と完全に交差しているか、線分の端点が
            // 直線の上側にあるなら直線と交差するとみなす
            if (a.y * b.y < 0 || (a.y * b.y < EPS && b.y > EPS)) {
                if (a.cross(b) < EPS) in = !in;  // 外積が負なら半直線とも交差する
            }
        }
        return in ? 1 : 0;
    }

    /**
     * O(nlogm + mlogn)。酷く冗長な実装。
     * JavaならPolygonかPath2Dで図を作ってArea#intersect(Area)を使ったほうが楽な上に
     * 凸じゃない図形にも使えるが、結構派手に摂動するので面積求めるとき以外は誤差がきつそう。
     * 
     * @return pとqの共通部分
     * @see http://www.prefield.com/algorithm/geometry/convex_intersect.html
     * @see http://cgm.cs.mcgill.ca/~godfried/teaching/cg-projects/97/Plante/CompGeomProject-EPlante/algorithm.html
     */
    static ArrayList<Point> convexIntersection(Point[] p, Point[] q) {
        int n = p.length;
        int m = q.length;
        Point minP = Collections.min(Arrays.asList(p));
        Point minQ = Collections.min(Arrays.asList(q));
        int argminP = Arrays.asList(p).indexOf(minP);
        int argminQ = Arrays.asList(q).indexOf(minQ);
        int comp = minP.compareTo(minQ);
        int i = argminP;
        int j = argminQ;
        if (comp == 0) {
            // 最小の要素が一致するなら、2つの凸多角形を同時に進んでいく
            // 最初に見つかった相違点が交差部分である
            do {
                i = (i + 1) % n;
                j = (j + 1) % m;
            } while (i != argminP && p[i].equals(q[j]));
            if (i == argminP) return new ArrayList<Point>(Arrays.asList(p));  // pとqは等しい
            return convexIntersectionPhase3(p, q, (i + n - 1) % n, (j + m - 1) % m);
        }
        if (comp > 0) {  // pがqより右(あるいは上)にある
            return convexIntersection(q, p);  // 怠惰にswap
        }

        // pがqより左にあるならキャリパーする
        int count = 0;
        do {
            Point pVec = p[next(i, n)].subtract(p[i]);
            Point qVec = q[next(j, m)].subtract(q[j]);
            double cross = pVec.cross(qVec);
            Point dir = cross > 0 ? pVec : qVec;
            if (dir.cross(q[j].subtract(p[i])) < EPS) {
                // pのキャリパーから見てqのキャリパーは右側か一直線上にある。ここがpokect lid
                return convexIntersectionPhase2(p, q, i, j);
            }

            if (cross > -EPS) i = next(i, n);
            if (cross < EPS) j = next(j, m);
        } while (count++ < n + m);

        // 一周以上してもキャリパーの位置関係が変わらなかったのでqはpの内部にある
        return new ArrayList<Point>(Arrays.asList(q));
    }

    private static int next(int i, int n) {
        return (i + 1) % n;
    }

    private static ArrayList<Point> convexIntersectionPhase2(Point[] p, Point[] q, int i,
            int j) {
        int n = p.length;
        int m = q.length;
        // pocket lidの中に交差部分があるはずなので探す
        // 今は耳の中にいる
        // 前提条件よりp[0]は共通部分外なので、pはインデックスを進めるごとに耳の奥に行き、
        // qはインデックスを戻すごとに奥へ行く
        boolean updated;
        int count = 0;
        do {
            updated = false;
            while (count < n + m
                    && p[next(i, n)].subtract(p[i]).cross(
                            q[(j + m - 1) % m].subtract(p[i])) < -EPS) {
                j = (j + m - 1) % m;
                updated = true;
                count++;
            }
            while (count < n + m
                    && q[(j + m - 1) % m].subtract(q[j]).cross(
                            p[next(i, n)].subtract(q[j])) > EPS) {
                i = next(i, n);
                updated = true;
                count++;
            }
        } while (updated);
        if (count >= n + m) {  // 共通部分などなかった
            return new ArrayList<Point>();
        }
        j = (j + m - 1) % m;  // qを時計回りに考えていたので戻す
        return convexIntersectionPhase3(p, q, i, j);
    }

    private static ArrayList<Point> convexIntersectionPhase3(Point[] p, Point[] q, int i,
            int j) {
        // 具体的な共通部分が見つかったので、あとは交互弧をトレースしていけばいい…はず
        int n = p.length;
        int m = q.length;
        assert intersectsSS(p[i], p[next(i, n)], q[j], q[next(j, m)]);

        ArrayList<Point> intersection = new ArrayList<Point>();
        Point crossP = crosspointLL(p[i], p[next(i, n)], q[j], q[next(j, m)]);
        intersection.add(crossP);
        boolean pIsInQ = p[next(i, n)].subtract(p[i]).cross(q[next(j, m)].subtract(q[j])) <= 0;
        if (pIsInQ && !p[next(i, n)].equals(q[j])) j = next(j, m);
        else i = next(i, n);

        // ここまでO(n+m)だったがここからO(nlogm + mlogn)になります（設計ミス）
        // キャリパーで事前に交差点列挙すればO(n+m)も可能。
        // Spaghetti Sourceのコードは事前列挙してないけどO(n+m)達成してて何やってるのか謎。
        do {
            Point nextP = p[next(i, n)];
            Point nextQ = q[next(j, m)];
            if (pIsInQ) {
                int inState = isInCcwConvex(nextP, q);
                if (inState == 1
                        || (inState == 2 && nextP.subtract(p[i]).cross(
                                nextQ.subtract(q[j])) < EPS)) {
                    intersection.add(nextP);
                }
                else {
                    // 交差する辺が見つかるまでqの辺を進める
                    while (!intersectsSS(p[i], nextP, q[j], q[next(j, m)])
                            || p[i].equals(q[j]))
                        j = (j + 1) % m;
                    nextQ = q[next(j, m)];
                    Point c = crosspointLL(p[i], nextP, q[j], nextQ);
                    if (approxEquals(nextP.subtract(p[i]).cross(nextQ.subtract(q[j])), 0)) {  // 2ベクトルが同一直線上
                        // qにとって最も進んだ場所へ行くべき
                        if (intersectsSP(p[i], nextP, nextQ)) c = nextQ;
                        else c = nextQ.subtract(nextP).distanceSqr() > nextQ.subtract(
                                p[i]).distanceSqr() ? p[i] : nextP;
                    }
                    intersection.add(c);
                    pIsInQ = false;
                }
                i = (i + 1) % n;
            }
            else {
                int inState = isInCcwConvex(nextQ, p);
                if (inState == 1
                        || (inState == 2 && nextQ.subtract(q[j]).cross(
                                nextP.subtract(p[i])) < EPS)) {
                    intersection.add(nextQ);
                }
                else {
                    while (!intersectsSS(p[i], p[next(i, n)], q[j], nextQ)
                            || p[i].equals(q[j]))
                        i = (i + 1) % n;
                    nextP = p[next(i, n)];
                    Point c = crosspointLL(p[i], nextP, q[j], nextQ);
                    if (approxEquals(nextP.subtract(p[i]).cross(nextQ.subtract(q[j])), 0)) {  // 2ベクトルが同一直線上
                        // pにとって最も進んだ場所へ行くべき
                        if (intersectsSP(q[j], nextQ, nextP)) c = nextP;
                        else c = nextP.subtract(nextQ).distanceSqr() > nextP.subtract(
                                q[j]).distanceSqr() ? q[j] : nextQ;
                    }
                    intersection.add(c);
                    pIsInQ = true;
                }
                j = (j + 1) % m;
            }
        } while (intersection.size() <= (n + m) * 2
                && !intersection.get(0).equals(intersection.get(intersection.size() - 1)));
        if (intersection.size() > (n + m) * 2) throw new IllegalStateException("A BUG");

        // 無駄な辺を消す
        ArrayList<Point> intersection2 = new ArrayList<Point>();
        for (int k = 0; k < intersection.size(); k++) {
            if (intersection2.size() < 2
                    || ccw(intersection2.get(intersection2.size() - 2), intersection2
                            .get(intersection2.size() - 1), intersection.get(k)) == 1) {
                intersection2.add(intersection.get(k));
            }
        }
        if (intersection2.size() > 1
                && intersection2.get(0).equals(
                        intersection2.get(intersection2.size() - 1))) intersection2
                .remove(intersection2.size() - 1);
        return intersection2;
    }

    /**
     * 凸多角形の直線の右側にある部分を切り捨てる
     * 
     * @param ps
     * @param a1
     * @param a2
     * @return
     * @see http://www.prefield.com/algorithm/geometry/convex_cut.html
     */
    static ArrayList<Point> convexCut(Point[] ps, Point a1, Point a2) {
        int n = ps.length;
        ArrayList<Point> ret = new ArrayList<Point>(n + 1);
        for (int i = 0; i < n; i++) {
            int ccw = ccw(a1, a2, ps[i]);
            if (ccw != -1) ret.add(ps[i]);  // ccw != 1 のときは誤差に注意。現状では戻り値は凸でないかも
            int ccwn = ccw(a1, a2, ps[(i + 1) % n]);
            if (ccw * ccwn == -1) {  // ???????
                ret.add(crosspointLL(a1, a2, ps[i], ps[(i + 1) % n]));
            }
        }
        return ret;
    }

    /**
     * O(n)
     * 
     * @return 凸多角形の直径となる2点の組
     * @see http://www.prefield.com/algorithm/geometry/convex_diameter.html
     */
    static int[] convexDiameter(Point[] ps) {
        int n = ps.length;
        int initI = 0, initJ = 0;
        for (int i = 1; i < n; i++) {
            if (ps[i].x < ps[initI].x) initI = i;
            if (ps[i].x > ps[initJ].x) initJ = i;
        }
        int i = initI, j = initJ;
        int maxI = i, maxJ = j;
        double maxDSqr = 0;
        int count = 0;
        do {
            if (maxDSqr < ps[i].distanceSq(ps[j])) {
                maxDSqr = ps[i].distanceSq(ps[j]);
                maxI = i;
                maxJ = j;
            }
            int ni = (i + 1) % n;
            int nj = (j + 1) % n;
            // i側の逆向きのベクトルから見てj側のベクトルが右向きならjを進める
            if (ps[i].subtract(ps[ni]).cross(ps[nj].subtract(ps[j])) <= 0) j = nj;
            else i = ni;
        } while (count++ <= 2 * n);
        // Spaghetti Sourceに倣うとループ条件は(i != initI || j != initJ)とすべきだが、
        // 縮退した多角形に対し無限ループしうるのでロバストな実装にした
        return new int[] { maxI, maxJ };
    }

    /**
     * @return 多角形の符号付面積
     * @see http://www.prefield.com/algorithm/geometry/area2.html
     */
    static double area(Point[] polygon) {
        double a = 0;
        for (int i = 0; i < polygon.length; i++) {
            a += polygon[i].cross(polygon[(i + 1) % polygon.length]);
        }
        return a / 2;
    }

    /**
     * @return 多角形の幾何学的重心
     * @see http://izumi-math.jp/F_Nakamura/heso/heso3.htm
     */
    static Point centroid(Point[] ps) {
        int n = ps.length;
        double areaSum = 0;
        Point centroid = new Point();
        for (int i = 0; i < n; i++) {
            double area = ps[i].cross(ps[(i + 1) % n]);
            areaSum += area;
            Point center3 = ps[i].add(ps[(i + 1) % n]);
            centroid = centroid.add(center3.multiply(area));
        }
        return centroid.multiply(1 / (areaSum * 3));
    }

    /**
     * 愚直にボロノイ領域を1つ求める。O(ps.length * convex.length)
     * 
     * @param p ボロノイ領域の中心
     * @param ps 点集合
     * @param convex 外殻
     * @return 点pを含むボロノイ領域
     */
    static Point[] voronoiCell(Point p, Point[] ps, Point[] convex) {
        for (Point p2 : ps) {
            if (p.equals(p2)) continue;
            Point h = p.add(p2).multiply(0.5);
            ArrayList<Point> newConvex = convexCut(convex, h, h.add(p2.subtract(p)
                    .multiply(new Point(0, 1))));
            convex = newConvex.toArray(new Point[newConvex.size()]);
        }
        return convex;
    }

    /* 幾何グラフ */

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

        void addEdge(Edge e) {
            edges[e.from].add(e);
            edges[e.to].add(new Edge(e.to, e.from, e.cost));
        }
    }

    static class Edge {
        final int from, to;
        final double cost;

        public Edge(int from, int to, double cost) {
            this.from = from;
            this.to = to;
            this.cost = cost;
        }

        @Override
        public String toString() {  // for debug
            return "(" + from + ", " + to + ")=>" + cost;
        }


    }

    /**
     * @param segs 線分のリスト
     * @param ps グラフと座標の対応が入る出力変数
     * @return 線分アレンジメントのグラフ。推移的な辺は省略している。
     * @see http://www.prefield.com/algorithm/geometry/segment_arrangement.html
     */
    static AdjGraph segmentArrangement(Line[] segs, List<Point> ps) {
        int n = segs.length;
        TreeSet<Point> set = new TreeSet<Point>();  // JavaのリストにはC++のuniqueがないので…
        for (int i = 0; i < n; i++) {
            set.add(segs[i].a);
            set.add(segs[i].b);
            for (int j = i + 1; j < n; j++) {
                if (intersectsSS(segs[i].a, segs[i].b, segs[j].a, segs[j].b)) {
                    // assert !intersectsSS(segs[i].a, segs[j].a, segs[i].b, segs[i].b);
                    set.add(crosspointLL(segs[i].a, segs[i].b, segs[j].a, segs[j].b));
                }
            }
        }
        ps.addAll(set);
        AdjGraph g = new AdjGraph(ps.size());

        class CP implements Comparable<CP> {  // JAVAにpairはない!!!
            final int i;
            final double d;

            CP(int i, double d) {
                this.i = i;
                this.d = d;
            }

            @Override
            public int compareTo(CP o) {
                return (int) Math.signum(d - o.d);
            }
        }
        ArrayList<CP> list = new ArrayList<CP>(ps.size());
        for (int i = 0; i < n; i++) {
            list.clear();
            for (int j = 0; j < ps.size(); j++) {
                if (intersectsSP(segs[i].a, segs[i].b, ps.get(j))) list.add(new CP(j,
                        segs[i].a.distanceSq(ps.get(j))));
            }
            Collections.sort(list);
            for (int j = 0; j + 1 < list.size(); j++) {
                int a = list.get(j).i;
                int b = list.get(j + 1).i;
                g.addEdge(new Edge(a, b, ps.get(a).distance(ps.get(b))));
            }
        }
        return g;
    }

    /**
     * 可視グラフ（点集合から見える位置へ辺を張ったグラフ）を作成する。
     * 障害物は凸多角形である。障害物は入れ子になったり交差してもよい。
     * 障害物は周囲が壁に覆われていて、内部が空洞であるとみなされる。
     * 障害物の周上の点はその障害物の外側にあるとみなされる。
     * 推移的な辺は省略されうる。これによりobjsに含まれるがpsにない点があるとバグる。
     * 
     * @param ps グラフの頂点となる平面上の点集合
     * @param objs 障害物の凸多角形のリスト
     * @return 可視グラフ
     * @see http://www.prefield.com/algorithm/geometry/visibility_graph.html
     */
    static AdjGraph visibilityGraph(Point[] ps, Point[][] objs) {
        int n = ps.length;
        AdjGraph graph = new AdjGraph(n);
        for (int i = 0; i < n; i++) {
            mainLoop: for (int j = i + 1; j < n; j++) {
                Point a = ps[i];
                Point b = ps[j];
                if (!a.equals(b)) for (int k = 0; k < objs.length; k++) {
                    Point[] obj = objs[k];
                    int inStateA = isInConvex(a, obj);
                    int inStateB = isInConvex(b, obj);
                    if ((inStateA ^ inStateB) % 2 == 1) continue mainLoop;
                    // 厳密に包含される場合と線上にある場合を区別し、
                    // 厳密に多角形の外にある場合と線上にある場合は区別していない
                    boolean strictlyInner = inStateA * inStateB == 1;
                    if (!strictlyInner && isInConvex(a.add(b).multiply(0.5), obj) == 1) continue mainLoop;
                    // 中点での判定は凸でない多角形では機能しないので注意
                    for (int l = 0; l < obj.length; l++) {
                        Point cur = obj[l];
                        Point next = obj[(l + 1) % obj.length];
                        if (intersectsSS(a, b, cur, next) && !intersectsSP(cur, next, a)
                                && !intersectsSP(cur, next, b)) {
                            continue mainLoop;
                        }
                    }
                }
                graph.addEdge(new Edge(i, j, a.distance(b)));
            }
        }
        return graph;
    }

    /* その他 */

    /**
     * 重複する線分を併合する。
     * 
     * @param segsOrig 線分集合
     * @see http://www.prefield.com/algorithm/geometry/merge_segments.html
     */
    static Line[] mergeSegments(Line[] segsOrig) {
        int n = segsOrig.length;
        Line[] segs = new Line[n];
        for (int i = 0; i < n; i++) {
            segs[i] = segsOrig[i].a.compareTo(segsOrig[i].b) < 0
                    ? segsOrig[i]
                    : new Line(segsOrig[i].b, segsOrig[i].a);
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Line l1 = segs[i];
                Line l2 = segs[j];
                boolean mergable = approxEquals(l1.b.subtract(l1.a).cross(
                        l2.b.subtract(l2.a)), 0)
                        && intersectsLP(l1.a, l1.b, l2.a)
                        && ccw(l1.a, l1.b, l2.b) != 2
                        && ccw(l2.a, l2.b, l1.b) != 2;
                if (mergable) {
                    segs[i--] = new Line(Collections.min(Arrays.asList(l1.a, l2.a)),
                            Collections.max(Arrays.asList(l1.b, l2.b)));
                    segs[j] = segs[--n];
                    break;
                }
            }
        }
        Line[] ret = new Line[n];
        System.arraycopy(segs, 0, ret, 0, n);
        return ret;
        // return Arrays.copyOf(segs, n);  // for Java6
    }
}
