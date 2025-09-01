import org.json.JSONObject;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PolynomialSolver {
    static class Point {
        int x;
        double y;
        Point(int x, double y) { this.x = x; this.y = y; }
    }

    public static void main(String[] args) throws Exception {
        // Read JSON from file (e.g., "input.json")
        String input = new String(Files.readAllBytes(Paths.get("input.json")));
        JSONObject obj = new JSONObject(input);

        int n = obj.getJSONObject("keys").getInt("n");
        int k = obj.getJSONObject("keys").getInt("k");

        // Step 1: Parse points
        List<Point> points = new ArrayList<>();
        for (String key : obj.keySet()) {
            if (key.equals("keys")) continue;
            JSONObject entry = obj.getJSONObject(key);
            int base = Integer.parseInt(entry.getString("base"));
            BigInteger val = new BigInteger(entry.getString("value"), base);
            points.add(new Point(Integer.parseInt(key), val.doubleValue()));
        }

        // Step 2: Sort points by x
        points.sort(Comparator.comparingInt(p -> p.x));

        // Step 3: Take first k points
        List<Point> subset = points.subList(0, k);

        // Step 4: Get polynomial coefficients
        double[] coeffs = lagrange(subset);

        // Step 5: Print result
        System.out.println("Degree: " + (k - 1));
        System.out.print("Coefficients (high to low): ");
        for (int i = coeffs.length - 1; i >= 0; i--) {
            System.out.print(coeffs[i] + " ");
        }
        System.out.println();
    }

    // ---------- Lagrange Interpolation ----------
    static double[] lagrange(List<Point> pts) {
        int k = pts.size();
        double[] poly = new double[k];
        Arrays.fill(poly, 0);

        for (int i = 0; i < k; i++) {
            double[] term = {1}; // start with constant 1
            double denom = 1;

            for (int j = 0; j < k; j++) if (j != i) {
                term = multiply(term, new double[]{-pts.get(j).x, 1}); // (x - xj)
                denom *= (pts.get(i).x - pts.get(j).x);
            }

            double scale = pts.get(i).y / denom;
            for (int t = 0; t < term.length; t++) {
                poly[t] += term[t] * scale;
            }
        }
        return poly;
    }

    // Multiply two polynomials
    static double[] multiply(double[] a, double[] b) {
        double[] res = new double[a.length + b.length - 1];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b.length; j++)
                res[i + j] += a[i] * b[j];
        return res;
    }
}
