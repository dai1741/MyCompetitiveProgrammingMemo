package jp.dai1741.competitive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.StringTokenizer;

public class ContestUtils {

    static class InputReader extends BufferedReader {
        StringTokenizer st = new StringTokenizer("");

        public InputReader() {
            super(new InputStreamReader(System.in));
        }

        public InputReader(String s) {
            super(new StringReader(s));
        }

        String next() {
            try {
                while (!st.hasMoreTokens())
                    st = new StringTokenizer(readLine());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }

        long nextLong() {
            return Long.parseLong(next());
        }

        double nextDouble() {
            return Double.parseDouble(next());
        }

        int[] nextInts(int n) {
            int[] ret = new int[n];
            for (int i = 0; i < n; i++) {
                ret[i] = nextInt();
            }
            return ret;
        }
    }

    String concatAll(String[] ss) {
        StringBuilder sb = new StringBuilder(ss.length * ss[0].length());
        for (String s : ss)
            sb.append(s);
        return sb.toString();
    }

}
