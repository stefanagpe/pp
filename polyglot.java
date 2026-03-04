import org.graalvm.polyglot.*;
import java.util.*;

class Polyglot {

    // token la uppercase
    private static String pyUpper(Context ctx, String token) {
        String safe = token.replace("\\", "\\\\").replace("\"", "\\\"");
        return ctx.eval("python", "(\"" + safe + "\").upper()").asString();
    }

    // elim primu si ultimu carac
    // suma de control folosind polinom de grad 5
    private static long polyCrc(Context ctx, String tokenUpper) {
        String mid;
        if (tokenUpper.length() >= 2) {
            mid = tokenUpper.substring(1, tokenUpper.length() - 1);
        } else {
            mid = "";
        }
        String safe = mid.replace("\\", "\\\\").replace("'", "\\'");

        // suma coduri ascii+polinom grad 5
        String script =
                "x = sum(ord(ch) for ch in '" + safe + "')\n" +
                "p = (3*(x**5)) - (7*(x**4)) + (11*(x**3)) - (13*(x**2)) + (17*x) - 19\n" +
                "p\n";

        return ctx.eval("python", script).asLong();
    }

    public static void main(String[] args) {
        try (Context ctx = Context.newBuilder().allowAllAccess(true).build()) {

            // array de cuv
            Value array = ctx.eval("js",
                "[\"If\",\"we\",\"run\",\"the\",\"java\",\"command\",\"included\",\"in\",\"GraalVM\",\"we\",\"will\",\"be\",\"automatically\",\"using\",\"the\",\"Graal\",\"JIT\",\"compiler\",\"no\",\"extra\",\"configuration\",\"is\",\"needed\",\"I\",\"will\",\"use\",\"the\",\"time\",\"command\",\"to\",\"get\",\"the\",\"real\",\"wall\",\"clock\",\"elapsed\",\"time\",\"it\",\"takes\",\"to\",\"run\",\"the\",\"entire\",\"program\",\"from\",\"start\",\"to\",\"finish\",\"rather\",\"than\",\"setting\",\"up\",\"a\",\"complicated\",\"micro\",\"benchmark\",\"and\",\"I\",\"will\",\"use\",\"a\",\"large\",\"input\",\"so\",\"that\",\"we\",\"arent\",\"quibbling\",\"about\",\"a\",\"few\",\"seconds\",\"here\",\"or\",\"there\",\"The\",\"large.txt\",\"file\",\"is\",\"150\",\"MB\"];"
            );

            // coleziuni+suma de control
            Map<Long, List<String>> grupuriCRC = new LinkedHashMap<>();

            for (int i = 0; i < array.getArraySize(); i++) {
                String w = array.getArrayElement(i).asString();

                // upper in py
                String up = pyUpper(ctx, w);

                // crc
                long crc = polyCrc(ctx, up);

                // adaug cuv
                grupuriCRC.computeIfAbsent(crc, k -> new ArrayList<>()).add(up);
            }

            System.out.println("cuv cu aceeasi suma de control:");
            boolean gasit = false;

            // asare cuv cu min 2 coliiuni
            for (Map.Entry<Long, List<String>> e : grupuriCRC.entrySet()) {
                if (e.getValue().size() > 1) {
                    gasit = true;
                    System.out.println("suma " + e.getKey() + ": " + e.getValue());
                }
            }

            if (!gasit) {
                System.out.println("Nu s-au gasit cuvinte cu aceeasi suma de control.");
            }
        }
    }
}
