package Labb3;

import java.sql.*;
import java.util.Scanner;

public class LabbMain {
    static Scanner scanner = new Scanner(System.in);

    private static Connection connect() {
        String url = "jdbc:sqlite:/home/cly/MarcusGustavssonJava24_Labb3_SQLite.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            String meny = """
                Startmeny:
                1. Lägg till en ny öl
                2. Visa all öl
                3. Uppdatera en öl
                4. Ta bort en öl
                5. Visa en öl med anteckningar
                6. Sök efter öl
                e. Avsluta
                """;
            System.out.println(meny);

            switch (scanner.nextLine().toLowerCase()) {
                case "1":
                    addBeer(scanner);
                    break;
                case "2":
                    showBeers();
                    break;
                case "3":
                    updateBeer(scanner);
                    break;
                case "4":
                    deleteBeer(scanner);
                    break;
                case "5":
                    showBeerWithNotes();
                    break;
                case "6":
                    searchBeer(scanner);
                    break;
                case "e":
                    System.out.println("Program avslutat.");
                    running = false;
                    break;
                default:
                    System.out.println("Felaktigt val. Välj igen.");
                    break;
            }
        }
    }

    public static void addBeer(Scanner scanner) {
        System.out.println("Ange ölnamn: ");
        String beerName = scanner.nextLine();

        System.out.println("Ange ölsort (t.ex. Lager, IPA, Stout): ");
        String beerType = scanner.nextLine();

        System.out.println("Ange ursprungsland: ");
        String beerOriginCountry = scanner.nextLine();

        System.out.println("Ange alkoholhalt (promille): ");
        double alcoholContent = scanner.nextDouble();
        scanner.nextLine();  // För att konsumera newline

        String sql = "INSERT INTO Beer(beerName, beerType, beerOriginCountry, alcoholContent) VALUES(?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, beerName);
            pstmt.setString(2, beerType);
            pstmt.setString(3, beerOriginCountry);
            pstmt.setDouble(4, alcoholContent);
            pstmt.executeUpdate();
            System.out.println("Öl tillagd.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void showBeers() {
        String sql = "SELECT * FROM Beer";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("ID\tNamn\t\tSort\t\tLand\t\tAlkoholhalt (promille)");
            while (rs.next()) {
                System.out.println(rs.getInt("beerId") + "\t" +
                        rs.getString("beerName") + "\t" +
                        rs.getString("beerType") + "\t" +
                        rs.getString("beerOriginCountry") + "\t" +
                        rs.getDouble("alcoholContent") + "‰");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateBeer(Scanner scanner) {
        System.out.println("Ange ID för ölen du vill uppdatera: ");
        int beerId = scanner.nextInt();
        scanner.nextLine();  // Läs in newline

        System.out.println("Ange nytt ölnamn: ");
        String beerName = scanner.nextLine();

        System.out.println("Ange ny ölsort: ");
        String beerType = scanner.nextLine();

        System.out.println("Ange nytt ursprungsland: ");
        String beerOriginCountry = scanner.nextLine();

        System.out.println("Ange ny alkoholhalt (promille): ");
        double alcoholContent = scanner.nextDouble();
        scanner.nextLine();  // Läs in newline

        String sql = "UPDATE Beer SET beerName = ?, beerType = ?, beerOriginCountry = ?, alcoholContent = ? WHERE beerId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, beerName);
            pstmt.setString(2, beerType);
            pstmt.setString(3, beerOriginCountry);
            pstmt.setDouble(4, alcoholContent);
            pstmt.setInt(5, beerId);
            pstmt.executeUpdate();
            System.out.println("Öl uppdaterad.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteBeer(Scanner scanner) {
        System.out.println("Ange ID för ölen du vill radera: ");
        int beerId = scanner.nextInt();
        scanner.nextLine();  // Konsumera newline

        String sql = "DELETE FROM Beer WHERE beerId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, beerId);
            pstmt.executeUpdate();
            System.out.println("Öl raderad.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addNoteToBeer(Scanner scanner) {
        System.out.println("Ange ID för ölen du vill lägga till en anteckning till: ");
        int beerId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.println("Ange anteckning: ");
        String note = scanner.nextLine();

        String sql = "INSERT INTO Note (beerNoteId, note) VALUES(?, ?)";

        try (Connection conn = SQLiteJDBC.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, beerId);
            pstmt.setString(2, note);
            pstmt.executeUpdate();
            System.out.println("Anteckning tillagd.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void showBeerWithNotes() {
        String sql = """
                SELECT Beer.beerName, Note.note 
                FROM Beer 
                JOIN Note ON Beer.beerId = Note.beerNoteId
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("Öl: " + rs.getString("beerName") + " - Anteckning: " + rs.getString("note"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void searchBeer(Scanner scanner) {
        while (true) {
            String searchMenu = """
            Sök efter öl baserat på:
            1. Land
            2. Ölsort
            3. Alkoholhalt (promille)
            4. Gå tillbaka till huvudmenyn
            """;
            System.out.println(searchMenu);

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    searchBeerByCountry(scanner);
                    break;
                case "2":
                    searchBeerByType(scanner);
                    break;
                case "3":
                    searchBeerByAlcoholContent(scanner);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Felaktigt val. Välj igen.");
                    break;
            }
        }
    }

    public static void searchBeerByCountry(Scanner scanner) {
        System.out.println("Ange ursprungsland att söka efter: ");
        String country = scanner.nextLine();

        String sql = "SELECT * FROM Beer WHERE beerOriginCountry = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, country);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("ID\tNamn\t\tSort\t\tLand\t\tAlkoholhalt (promille)");
            while (rs.next()) {
                System.out.println(rs.getInt("beerId") + "\t" +
                        rs.getString("beerName") + "\t" +
                        rs.getString("beerType") + "\t" +
                        rs.getString("beerOriginCountry") + "\t" +
                        rs.getDouble("alcoholContent") + "‰");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void searchBeerByType(Scanner scanner) {
        System.out.println("Ange ölsort att söka efter (t.ex. Lager, IPA, Stout): ");
        String type = scanner.nextLine();

        String sql = "SELECT * FROM Beer WHERE beerType = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("ID\tNamn\t\tSort\t\tLand\t\tAlkoholhalt (promille)");
            while (rs.next()) {
                System.out.println(rs.getInt("beerId") + "\t" +
                        rs.getString("beerName") + "\t" +
                        rs.getString("beerType") + "\t" +
                        rs.getString("beerOriginCountry") + "\t" +
                        rs.getDouble("alcoholContent") + "‰");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void searchBeerByAlcoholContent(Scanner scanner) {
        System.out.println("Ange alkoholhalt (promille) att söka efter: ");
        double alcoholContent = scanner.nextDouble();
        scanner.nextLine();  // Konsumera newline

        String sql = "SELECT * FROM Beer WHERE alcoholContent = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, alcoholContent);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("ID\tNamn\t\tSort\t\tLand\t\tAlkoholhalt (promille)");
            while (rs.next()) {
                System.out.println(rs.getInt("beerId") + "\t" +
                        rs.getString("beerName") + "\t" +
                        rs.getString("beerType") + "\t" +
                        rs.getString("beerOriginCountry") + "\t" +
                        rs.getDouble("alcoholContent") + "‰");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
