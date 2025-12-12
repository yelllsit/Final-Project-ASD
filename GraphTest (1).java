package graphlinked;

public class GraphTest {
    public static void main(String[] args) {

        GraphIUP jakarta = new GraphIUP();

        jakarta.addCity("Monas", 1);
        jakarta.addCity("Kota Tua", 0);
        jakarta.addCity("Sudirman", 2);
        jakarta.addCity("Senayan", 1);
        jakarta.addCity("Kemang", 1);
        jakarta.addCity("Blok M", 2);
        jakarta.addCity("Tanah Abang", 1);
        jakarta.addCity("Mangga Dua", 0);

        jakarta.addConnection("Monas", "Kota Tua", 4.5);
        jakarta.addConnection("Monas", "Sudirman", 3.7);
        jakarta.addConnection("Monas", "Tanah Abang", 2.1);
        jakarta.addConnection("Kota Tua", "Mangga Dua", 1.8);
        jakarta.addConnection("Mangga Dua", "Tanah Abang", 5.2);
        jakarta.addConnection("Tanah Abang", "Sudirman", 2.5);
        jakarta.addConnection("Sudirman", "Senayan", 2.1);
        jakarta.addConnection("Sudirman", "Kemang", 5.8);
        jakarta.addConnection("Senayan", "Blok M", 1.5);
        jakarta.addConnection("Blok M", "Kemang", 3.2);
        jakarta.addConnection("Senayan", "Kemang", 4.6);

        testRoute(jakarta, "Kota Tua", "Kemang");
        testRoute(jakarta, "Mangga Dua", "Blok M");
        testRoute(jakarta, "Tanah Abang", "Senayan");
    }

    private static void testRoute(GraphIUP jakarta, String from, String to) {
        System.out.println("\nTesting: " + from + " → " + to);
        GraphIUP.PathResult result = jakarta.findOptimalPath(from, to);

        if (result != null) {
            System.out.println(result);
        }
    }
}
