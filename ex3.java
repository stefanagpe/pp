package org.example;

import org.graalvm.polyglot.*;
import java.util.Scanner;

public class ex3{
    public static void main(String[] args) {
        // date de intrare, n nr aruncari,m max pajura
        Scanner sc = new Scanner(System.in);

        System.out.print("nr arunc(n): ");
        int n = sc.nextInt();

        System.out.print("maxim pajura(x): ");
        int x = sc.nextInt();

        // 0 <= x <= n
        if (n < 0 || x < 0 || x > n) {
            System.out.println("nu is bune datele: trebuie 0 <= x <= n si n >= 0.");
            return;
        }
        try (Context ctx = Context.newBuilder().allowAllAccess(true).build()) {

            // combinatii
            // P(X=k) = C(n,k) * (0.5^n)
            Value probFunc = ctx.eval("python",
                    "from math import comb\n" +
                    "def p_binom_leq(n, x):\n" +
                    "    p = 0.0\n" +
                    "    for k in range(x+1):\n" +
                    "        p += comb(n, k) * (0.5 ** n)\n" +
                    "    return p\n" +
                    "p_binom_leq\n"
            );

            // rez ca double
            double p = probFunc.execute(n, x).asDouble();

            // afis prob
            Value printer = ctx.eval("js",
                    "(n, x, p) => {\n" +
                    "  console.log('n = ' + n + ', x = ' + x);\n" +
                    "  console.log('probabil P(X <= x) = ' + p);\n" +
                    "}\n"
            );
            printer.execute(n, x, p);

            // afis din java
            System.out.println("P(X <= " + x + ") = " + p);
        }
    }
}
