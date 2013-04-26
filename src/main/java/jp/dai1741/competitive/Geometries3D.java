package jp.dai1741.competitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 以下が前提
 * - 右手系。右方向がx軸正、上方向がy軸正、手前方向がz軸正、角は左回りが正
 * - 全ての凸多角形は左回りに与えられる
 */
public class Geometries3D {

    static final double EPS = 1e-9;

    static boolean approxEquals(double a, double b) {
        return Math.abs(a - b) < EPS;
    }

    static class Point implements Comparable<Point> {

        final double x, y, z;

        public Point(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /** 内積　dot(v1,v2)=|v1||v2|cosθ */
        double dot(Point p) {
            return x * p.x + y * p.y + z * p.z;
        }

        /** 外積　|cross(v1,v2)|=|v1||v2||sinθ| */
        Point cross(Point p) {
            return new Point(y * p.z - z * p.y, z * p.x - x * p.z, x * p.y - y * p.x);
        }

        double distanceSqr() {
            return x * x + y * y + z * z;
        }

        double distance() {
            return Math.sqrt(distanceSqr());
        }

        double distanceSqr(Point p) {
            return subtract(p).distanceSqr();
        }

        double distance(Point p) {
            return subtract(p).distance();
        }

        Point add(Point p) {
            return new Point(x + p.x, y + p.y, z + p.z);
        }

        Point multiply(double k) {
            return new Point(k * x, k * y, k * z);
        }

        Point subtract(Point p) {
            return new Point(x - p.x, y - p.y, z - p.z);
        }

        @Override
        public boolean equals(Object obj) {  // この関数はEclipseで生成して座標の比較だけ書き換えればいい
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Point other = (Point) obj;
            if (!approxEquals(x, other.x)) return false;
            if (!approxEquals(y, other.y)) return false;
            if (!approxEquals(z, other.z)) return false;
            return true;
        }

        @Override
        public int compareTo(Point o) {
            if (!approxEquals(x, o.x)) return (int) Math.signum(x - o.x);
            if (!approxEquals(y, o.y)) return (int) Math.signum(y - o.y);
            if (!approxEquals(z, o.z)) return (int) Math.signum(z - o.z);
            return 0;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + "," + z + ")";
        }

    }

    /**
     * @param a
     * @param b
     * @param c
     * @param world 世界座標の上側
     * @return 上から見たときのccw
     * @see Geometries2D#ccw(Point, Point, Point)
     */
    static int ccw(Point a, Point b, Point c, Point world) {
        b = b.subtract(a);
        c = c.subtract(a);
        double d = world.dot(b.cross(c));
        if (d > EPS) return +1;                           // counter clockwise
        if (d + EPS < 0) return -1;                       // clockwise
        // bとcはworldに投影して考えないといかんな
        //        if (b.dot(c) + EPS < 0) return +2;                // c--a--b on plane
        //        if (b.distanceSqr() < c.distanceSqr()) return -2; // a--b--c on plane
        return 0;                                         // on same projected line
    }

    /**
     * @param n 単位方向ベクトル
     * @return ベクトルpをベクトルn周りにtheta[rad]だけ回したベクトル
     * @see http://hooktail.sub.jp/vectoranalysis/vectorRot/
     */
    static Point rotate(Point p, Point n, double theta) {
        Point on = n.multiply(n.dot(p));
        Point nv = p.subtract(on).multiply(Math.cos(theta));
        Point qv = p.cross(n).multiply(Math.sin(theta));
        return on.add(nv).subtract(qv);
    }

    /**
     * @param a 平面上の1点
     * @param n 平面の法線ベクトル
     * @param p
     * @return 点pを平面に投影したときの点
     */
    static Point projectOnPlane(Point a, Point n, Point p) {
        Point ap = p.subtract(a);
        double t = n.dot(ap) / n.distanceSqr();
        return p.subtract(n.multiply(t));
    }

    /** @return 直線aに点pを投影したときの点 */
    static Point projectOnLine(Point a1, Point a2, Point p) {
        Point a = a2.subtract(a1);
        p = p.subtract(a1);
        double t = a.dot(p) / a.distanceSqr();
        return a1.add(a.multiply(t));
    }

    static double distancePlaneP(Point a, Point n, Point p) {
        return projectOnPlane(a, n, p).distance(p);
    }

    static double distanceLP(Point a1, Point a2, Point p) {
        return projectOnLine(a1, a2, p).distance(p);
    }

    static double distanceSP(Point a1, Point a2, Point p) {
        Point r = projectOnLine(a1, a2, p);
        // 投影した点が線分上にあるなら、点pからその点への距離を返せばいい
        Point ra1 = a1.subtract(r);
        Point ra2 = a2.subtract(r);
        double dot = ra1.dot(ra2);
        if (dot < EPS && approxEquals(dot * dot, ra1.distanceSqr() * ra2.distanceSqr())) return r
                .distance(p);
        // そうでなければ近い方の端点との距離
        return Math.min(a1.distance(p), a2.distance(p));
    }

    /**
     * もし最も距離が短くなる点を得たいならcrosspointLL()を呼ぶ。
     * 
     * @see http://mathforum.org/library/drmath/view/51980.html
     */
    static double distanceLL(Point a1, Point a2, Point b1, Point b2) {
        Point n = a2.subtract(a1).cross(b2.subtract(b1));
        // nが0なら2直線は平行。任意の点を選んでよい
        if (n.distanceSqr() < EPS) return distanceLP(a1, a2, b1);
        // nが張り点b1を通る平面はベクトルaに垂直であるので、
        // ベクトルaの任意の点からこの平面への距離は一定値となる
        return distancePlaneP(b1, n, a1);
    }

    static double distanceLS(Point a1, Point a2, Point b1, Point b2) {
        Point n = a2.subtract(a1).cross(b2.subtract(b1));
        // nが0なら直線・線分は平行
        if (n.distanceSqr() < EPS) return distanceLP(a1, a2, b1);
        // nが張る平面から見て線分bが直線aを跨いでいるなら2直線の距離と同じ
        if (ccw(a1, a2, b1, n) * ccw(a1, a2, b2, n) <= 0) return distancePlaneP(b1, n, a1);
        // そうでなければ近い方の端点との距離
        return Math.min(distanceLP(a1, a2, b1), distanceLP(a1, a2, b2));
    }

    static double distanceSS(Point a1, Point a2, Point b1, Point b2) {
        Point n = a2.subtract(a1).cross(b2.subtract(b1));
        // 2線分が平行でなく、nが張る平面から見て線分a,b互いを跨いでいるなら2直線の距離と同じ
        if (n.distanceSqr() > EPS && ccw(a1, a2, b1, n) * ccw(a1, a2, b2, n) <= 0
                && ccw(b1, b2, a1, n) * ccw(b1, b2, a2, n) <= 0) return distancePlaneP(
                b1, n, a1);
        // そうでなければ端点含む4パターンのうちのどれか
        return Math.min(Math.min(distanceSP(a1, a2, b1), distanceSP(a1, a2, b2)), Math
                .min(distanceSP(b1, b2, a1), distanceSP(b1, b2, a2)));
    }

    /**
     * @return 2直線の交点。2直線が交わらないときは、2直線間の距離が最も近くなる直線a側の点
     * @see http://www.deqnotes.net/acmicpc/2d_geometry/lines#intersection_of_lines
     */
    static Point crosspointLL(Point a1, Point a2, Point b1, Point b2) {
        // 法線方向に投影すれば2次元の場合と同じように処理できる
        Point n = a2.subtract(a1).cross(b2.subtract(b1));
        if (n.distanceSqr() < EPS) return a1;  // 2線が平行
        // ベクトルaの長さをd1/d2倍すると直線bに接するようにd1,d2を設定
        Point a = a2.subtract(a1);
        Point b = b2.subtract(b1);
        double d1 = n.dot(b.cross(b1.subtract(a1)));
        double d2 = n.dot(b.cross(a));
        return a1.add(a.multiply(d1 / d2));
    }


    /*
     * 球（球面）
     * 中心cで半径rの球pは |p-c|=r
     * 
     * 円（円周）
     * 中心cで半径rで法線がnの円pは |p-c|=r かつ dot((p-c),n)=0
     * 3次元の円は連立なしのベクトル方程式では表せない。不便。円柱座標？
     * 円pは以下のようにパラメータ表示もできる。自由度が減るので便利かもしれない。
     * p = r*(cos(t)*u + sin(t)*(n×u)) + c
     * c:中心, r:半径, n:単位法線ベクトル, u: nと垂直な任意の単位ベクトル, t: 角度(パラメータ)
     * 
     * @see http://www.physicsforums.com/showpost.php?p=1007562&postcount=6
     */

    /**
     * 計算内容は{@link Geometries2D#crosspointLC(Point, Point, Point, double)}と同一。
     * 
     * @return 直線aと球の交点
     */
    static Point[] crosspointLSphere(Point a1, Point a2, Point c, double r) {
        Point foot = projectOnLine(a1, a2, c);
        double footLenSqr = foot.distanceSqr(c);
        Point dir = a2.subtract(a1);
        if (approxEquals(r * r, footLenSqr)) {  // 一点で接する場合（誤差死回避のため分岐）
            return new Point[] { foot };
        }
        if (r * r < footLenSqr) return new Point[0];

        double len = Math.sqrt(r * r - footLenSqr) / dir.distance();
        dir = dir.multiply(len);
        return new Point[] { foot.add(dir), foot.subtract(dir), };
    }

    /**
     * @return 2球の交差点となる円の中心。
     *         円はベクトルabを法線として半径はコードの通りとなる。
     */
    static Point crosspointSphereSphere(Point a, double ar, Point b, double br) {
        Point ab = b.subtract(a);
        double d = ab.distance();

        // 余弦定理で球の中心から2球の交点となる円への距離を求め、
        // 三平方の定理で2球の交点となる円の半径を求める
        double lenToCross = (ab.distanceSqr() + ar * ar - br * br) / (2 * d);
        // double radius = Math.sqrt(ar * ar - lenToCross * lenToCross);
        if (d < EPS || ar < Math.abs(lenToCross)) return null;  // 交点なし（か無限個存在）

        Point crosspoint = a.add(ab.multiply(lenToCross / d));
        return crosspoint;
    }

    /**
     * @return 球と平面の交差点となる円の中心。
     *         円はベクトルnを法線として半径はsqrt(r^2-d^2)となる。
     */
    static Point crosspointPlaneSphere(Point a, Point n, Point c, double r) {
        Point d = projectOnPlane(a, n, c);
        if (r*r < d.distanceSqr()) return null;  // 交点なし
        return d;
    }

    /*
     * 球座標
     * [x] [rsinθcosφ]  [r ] [sqrt(x^2+y^2+z^2)]
     * [y]=[rsinθsinφ], [θ]=[acos(z/r)        ]
     * [z] [rcosθ     ]  [φ] [atan2(y,x)       ]
     * 
     * 緯度 (latitude)  = θ - 90[deg]
     * 経度 (longitude) = φ
     */

    Point sphericalPolar(double r, double t, double p) {
        return new Point(r * Math.sin(t) * Math.cos(p), r * Math.sin(t) * Math.sin(p), r
                * Math.cos(t));
    }

    /**
     * @return 半径1の球での球面距離。r倍すれば半径rの球での球面距離となる
     */
    double sphericalDistance(double lat1, double long1, double lat2, double long2) {
        // 球面三角法の第二余弦定理を使うのが定石かつ高精度らしいがよくわからん。愚直にやる。
        Point p1 = sphericalPolar(1, lat1 + Math.PI / 2, long1);
        Point p2 = sphericalPolar(1, lat2 + Math.PI / 2, long2);
        double d = p1.dot(p2);
        return Math.acos(d);
    }

    /** @return nに垂直なベクトルのうちの1つ。単位ベクトルとは限らない。 */
    static Point perpendicular(Point n) {
        return n.cross(approxEquals(n.x, 0) ? new Point(1, 0, 0) : new Point(0, 1, 0));
    }

    /**
     * @return スカラー三重積。3ベクトルが張る平行六面体の符号付体積。
     *         この値を1/6倍すると3ベクトルが張る四面体の体積となる。
     * @see http://hooktail.sub.jp/vectoranalysis/Triprod/
     */
    static double box(Point a, Point b, Point c) {
        return a.dot(b.cross(c));
    }

    // 3次元空間の（凸）多面体の体積を求めるのは実装量が膨大という意味で困難。
    // 向き付け可能（≒ねじれのない）な多面体は、面を列挙できれば体積は
    // 1/3 * Σdot(xi,ni)*Ai
    // となる。（xi:面iの幾何学的重心, ni:面iの単位法線ベクトル, Ai:面iの面積）
    // see: http://en.wikipedia.org/wiki/Polyhedron#Volume

    // 線形計画問題として与えられる凸多面体は平面による切断により面を列挙できる。
    // これは強実装。切断のあと2次元凸包が必要。愚直にやってO(n^3)かかる。誤差も多い。
    // 頂点群からの面列挙には3次元凸包を使う。
    // これも実装量が多い。O(n^4)の全探索かO(nlogn)の分割統治法が実用的。
    // 二重積分の形になるならシンプソン則で近似できる。

    /**
     * 愚直に3次元凸包を求める。O(n^4)。
     * 
     * @param ps
     * @return
     */
    static Point[][] convexHull(Point[] ps) {
        int n = ps.length;
        ArrayList<Point[]> faces = new ArrayList<Point[]>();
        ArrayList<Point> samePlanePoints = new ArrayList<Point>();
        for (int i1 = 0; i1 < n; i1++) {
            for (int i2 = i1 + 1; i2 < n; i2++) {
                mainLoop: for (int i3 = i2 + 1; i3 < n; i3++) {
                    Point normal = ps[i2].subtract(ps[i1]).cross(ps[i3].subtract(ps[i1]));
                    if (normal.distanceSqr() < EPS) continue;  // 縮退面を無視

                    samePlanePoints.clear();
                    boolean isConvexFace = true;
                    boolean invIsConvexFace = true;
                    for (int k = 0; k < n; k++) {
                        double dir = ps[k].subtract(ps[i1]).dot(normal);
                        if (approxEquals(dir, 0)) {
                            if (i1 != k
                                    && i2 != k
                                    && k < i3
                                    && ps[i2].subtract(ps[i1]).cross(
                                            ps[k].subtract(ps[i1])).distanceSqr() > EPS) {
                                // すでにkを含む3頂点が選ばれたはずなので、この面は追加済みなはず
                                isConvexFace = invIsConvexFace = false;
                            }
                            samePlanePoints.add(ps[k]);
                        }
                        else if (dir > 0) isConvexFace = false;
                        else invIsConvexFace = false;

                        // 凸包の面でないなら飛ばす
                        if (!isConvexFace && !invIsConvexFace) continue mainLoop;
                    }
                    // samePlanePointsにこの面を作る点が入る。
                    Point[] facePoints = samePlanePoints
                            .toArray(new Point[samePlanePoints.size()]);

                    // 2次元凸包生成にO(nlogn)かかるので一見すると計算量がO(n^4logn)に見えるが、
                    // 面の数はO(n)なのでこの部分はO(n^2logn)である。

                    // 法線の順方向と逆方向で凸包を取り、面を加える。
                    if (isConvexFace) faces.add(convexHull2D(facePoints, normal));
                    if (invIsConvexFace) faces.add(convexHull2D(facePoints, normal
                            .multiply(-1)));
                }
            }
        }
        return faces.toArray(new Point[faces.size()][]);
    }


    /**
     * @param ps 点集合。同一平面上になくてもいい。
     * @param normal 面の方向
     * @return ある方向から見たときの2次元凸包。
     * @see Geometries2D#convexHull(jp.dai1741.competitive.Geometries2D.Point[])
     */
    static Point[] convexHull2D(Point[] ps, Point normal) {
        final Point dir = perpendicular(normal);  // 任意の点が相異ならdir=ps[1]-ps[0]でよい
        Arrays.sort(ps, new Comparator<Point>() {

            @Override
            public int compare(Point o1, Point o2) {
                double d = dir.dot(o2.subtract(o1));
                return !approxEquals(d, 0) ? (int) Math.signum(d) : 0;
            }
        });

        int n = ps.length;
        Point[] hull = new Point[2 * n];
        int k = 0; // k == hull.size()
        // 下側凸包（向き的に上側凸包というべきかも）
        for (int i = 0; i < n; hull[k++] = ps[i++]) {
            while (k >= 2 && ccw(hull[k - 2], hull[k - 1], ps[i], normal) <= 0)
                --k;  // 最後のベクトルから見て候補点が右側にあるなら、最後の点をはずす
            // 現状ccwが+2を返さないので縮退してると死ぬかもしれない
        }
        // 下側凸包の後に上側を加える
        int lowerEnd = k;
        for (int i = n - 2; i >= 0; hull[k++] = ps[i--]) {
            while (k > lowerEnd && ccw(hull[k - 2], hull[k - 1], ps[i], normal) <= 0)
                --k;
        }
        return Arrays.copyOf(hull, k - 1);  // 最後は重複しているので消す
    }

    // あれなんかおかしい、全部の凸平面の回転方向逆な気がしてきた

    /**
     * 面の法線ベクトルを得る。面は縮退がなく、無駄な点を含んでいないこと。
     * 無駄な点を含む場合は (1)0でない位置ベクトル (2)0でない外積 をこの順で探索すればいい。
     * 面が凸でない場合は、さらに符号付面積が負なら法線を反転するといいかもしれない。
     * 
     * @param surface
     * @return
     */
    static Point getNormal(Point[] surface) {
        return surface[1].subtract(surface[0]).cross(surface[2].subtract(surface[1]));
    }

    /**
     * 多面体が凸であるかどうかを返す。O(面の数*点の数)。多面体は穴がなく連結であること。
     * 
     * @param polyhedron
     * @return
     */
    static boolean isConvex(Point[][] polyhedron) {
        for (Point[] face : polyhedron) {
            Point normal = getNormal(face);
            for (Point[] otherface : polyhedron) {
                if (face != otherface) for (Point p : otherface) {
                    double d = p.subtract(face[0]).dot(normal);
                    if (d > EPS) return false;
                }
            }
        }
        return true;
    }

    /**
     * O(面の数)。
     * 
     * @return 内部に存在するなら1、面上なら2、外部なら0
     */
    static int isInConvex(Point p, Point[][] polyhedron) {
        boolean on = false;
        for (Point[] face : polyhedron) {
            double d = p.subtract(face[0]).dot(getNormal(face));
            if (approxEquals(d, 0)) on = true;
            else if (d > 0) return 0;
        }
        return on ? 2 : 1;
    }

    /**
     * @return 多角形の符号付面積
     */
    static double area(Point[] face) {
        double a = 0;
        Point n = getNormal(face);
        for (int i = 0; i < face.length; i++) {
            a += n.dot(face[i].cross(face[(i + 1) % face.length]));
        }
        return a / 2 / n.distance();
        // nを単位ベクトルだったことにするため長さで割る（後から割った方が誤差的に良い）
    }

    /**
     * @return 多角形の幾何学的重心。
     * @see http://izumi-math.jp/F_Nakamura/heso/heso3.htm
     */
    static Point centroid(Point[] ps) {
        int n = ps.length;
        Point normal = getNormal(ps);
        normal = normal.multiply(1 / normal.distance());
        double areaSum = 0;
        Point centroid = new Point(0, 0, 0);
        for (int i = 0; i < n; i++) {
            double area = normal.dot(ps[i].cross(ps[(i + 1) % n]));
            areaSum += area;
            Point center3 = ps[i].add(ps[(i + 1) % n]);
            centroid = centroid.add(center3.multiply(area));
        }
        centroid = centroid.multiply(1 / (areaSum * 3));
        // この重心は法線方向の成分は考慮していないので、最後に元の面に投影する必要がある。
        // …という認識なのだが根拠なし（適当に書きかえてたらテスト通った）
        return projectOnPlane(ps[0], normal, centroid);
    }

    /**
     * @param polyhedron
     * @return 多面体の体積
     */
    static double volume(Point[][] polyhedron) {
        double a = 0;
        for (Point[] face : polyhedron) {
            Point n = getNormal(face);
            n = n.multiply(1 / n.distance());
            a += n.dot(centroid(face)) * area(face);
            // 面の重心と面積計算を別関数に投げているが、
            // 精度・時間・実装量どれもよくないので展開すべき。
        }
        return a / 3;
    }

    /**
     * 凸多面体の平面aの上側にある部分を消す。エンバグしている。
     * 
     * @param polyhedron 凸多面体
     * @param a 平面上の一点
     * @param n 平面の法線
     * @return
     */
    static Point[][] convexCut(Point[][] polyhedron, Point a, Point n) {
        ArrayList<Point> cutPoints = new ArrayList<Point>();
        ArrayList<Point[]> newConvex = new ArrayList<Point[]>();

        for (Point[] face : polyhedron) {
            Point faceNormal = getNormal(face);
            Point dir = faceNormal.cross(n);  // 順番?
            Point aOnFace = projectOnPlane(face[0], faceNormal, a);
            Point aOnFaceDired = aOnFace.add(dir);

            if (dir.distanceSqr() < EPS) {  // 2面が平行
                if (a.subtract(face[0]).dot(n) > -EPS) newConvex.add(face);
                continue;
            }
            ArrayList<Point> newFace = new ArrayList<Point>();
            for (int i = 0; i < face.length; i++) {
                int ccw = ccw(aOnFace, aOnFaceDired, face[i], faceNormal);
                if (ccw == 0) {  // TODO: +2と-2も考慮する
                    newFace.add(face[i]);
                    cutPoints.add(face[i]);
                    continue;
                }
                if (ccw != -1) newFace.add(face[i]);  // TODO: 誤差を頑張る
                int ccwn = ccw(aOnFace, aOnFaceDired, face[(i + 1) % face.length],
                        faceNormal);
                if (ccw * ccwn == -1) {  // FIXME XXX: BUG AROUND HERE :XXX FIXME
                    Point cp = crosspointLL(aOnFace, aOnFaceDired, face[i], face[(i + 1)
                            % face.length]);
                    newFace.add(cp);
                    cutPoints.add(cp);
                }
            }
            if (newFace.size() > 2) {
                newConvex.add(newFace.toArray(new Point[newFace.size()]));
            }
        }
        if (cutPoints.size() > 2) {
            Point[] newFace = convexHull2D(
                    cutPoints.toArray(new Point[cutPoints.size()]), n);
            if (newFace.length > 2) newConvex.add(newFace);
        }
        return newConvex.toArray(new Point[newConvex.size()][]);
    }

    /**
     * 合成シンプソン公式で二重積分を計算する。
     * 普通のシンプソン則はこちら：
     * 　integral x=a..b f(x) \approx (b-a)/6 * (f(a) + 4f((a+b)/2) + f(b))
     * 
     * @param x1 左端
     * @param x2 右端
     * @param y1 上端
     * @param y2 下端
     * @param n 縦方向の分割数。偶数でなければならない
     * @param m 横方向の分割数。偶数でなければならない
     * @return funcの二重積分の近似値
     * @see http://www.math.ohiou.edu/courses/math3600/lecture24.pdf
     */
    static double simpson2(double x1, double x2, double y1, double y2, int n, int m) {
        double ret = 0;
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                int multiplier = 1;
                if (i > 0) multiplier *= 2;
                if (j > 0) multiplier *= 2;
                if (i % 2 == 1) multiplier *= 2;
                if (j % 2 == 1) multiplier *= 2;
                ret += multiplier * func(x1 + (x2 - x1) * j / m, y1 + (y2 - y1) * i / n);
            }
        }
        return (x2 - x1) * (y2 - y1) / (9 * n * m) * ret;
    }

    static double func(double x, double y) {
        return x * y;
    }

    /**
     * さいころ
     * 
     * @see http://www.prefield.com/algorithm/misc/dice.html
     */
    static class Dice {
        static final int TOP = 0, BOTTOM = 1, FRONT = 2, BACK = 3, LEFT = 4, RIGHT = 5;

        final int[] id = new int[6];

        Dice() {
            id[TOP] = 0;
            id[FRONT] = 1;
            id[LEFT] = 2;
            id[RIGHT] = 3;
            id[BACK] = 4;
            id[BOTTOM] = 5;
        }

        void roll(int a, int b, int c, int d) {
            int temp = id[a];
            id[a] = id[b];
            id[b] = id[c];
            id[c] = id[d];
            id[d] = temp;
        }

        void rollX() {
            roll(TOP, BACK, BOTTOM, FRONT);
        }

        void rollY() {
            roll(TOP, RIGHT, BOTTOM, LEFT);
        }

        void rollZ() {
            roll(FRONT, LEFT, BACK, RIGHT);
        }

        int[][] allRolls() {
            int[][] ret = new int[24][];
            int i = 0;
            for (int k = 0; k < 6; k++) {
                for (int j = 0; j < 4; j++) {
                    ret[i++] = id.clone();
                    rollZ();
                }
                if (k % 2 == 1) rollY();
                else rollX();
            }
            return ret;
        }

        boolean canEqual(int[] other) {
            for (int[] roll : allRolls()) {
                if (Arrays.equals(roll, other)) return true;
            }
            return false;
        }
    }
}
