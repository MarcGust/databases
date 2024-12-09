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
                1. Visa all öl
                2. Visa öl med anteckningar
                3. Sök efter öl
                4. Lägg till öl
                5. Uppdatera öl
                6. Ta bort öl
                e. Avsluta
                """;
            System.out.println(meny);

            switch (scanner.nextLine().toLowerCase()) {
                case "1":
                    showBeers();
                    break;
                case "2":
                    showBeerWithNotes();
                    break;
                case "3":
                    searchBeer(scanner);
                    break;
                case "4":
                    addBeer(scanner);
                    break;
                case "5":
                    updateBeer(scanner);
                    break;
                case "6":
                    deleteBeer(scanner);
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
        scanner.nextLine();

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
        System.out.println("Ange ID för den öl du vill uppdatera: ");
        int beerId = Integer.parseInt(scanner.nextLine());

        if (!beerExists(beerId)) {
            System.out.println("Ingen öl hittades med detta ID.");
            return;
        }

        String updateMenu = """
            Vad vill du uppdatera?
            1. Uppdatera ölinformation
            2. Lägg till en anteckning
            3. Gå tillbaka till huvudmenyn
            """;
        System.out.println(updateMenu);

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                updateBeerInfo(scanner, beerId);
                break;
            case "2":
                addNoteToBeer(scanner, beerId);
                break;
            case "3":
                return;
            default:
                System.out.println("Felaktigt val. Välj igen.");
                break;
        }
    }

    private static boolean beerExists(int beerId) {
        String sql = "SELECT beerId FROM Beer WHERE beerId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, beerId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static void updateBeerInfo(Scanner scanner, int beerId) {
        System.out.println("Ange nytt namn på ölen: ");
        String newName = scanner.nextLine();

        System.out.println("Ange ny typ av öl (t.ex. Lager, IPA, Stout): ");
        String newType = scanner.nextLine();

        System.out.println("Ange nytt ursprungsland: ");
        String newCountry = scanner.nextLine();

        System.out.println("Ange ny alkoholhalt (promille): ");
        double newAlcoholContent = Double.parseDouble(scanner.nextLine());

        String sql = "UPDATE Beer SET beerName = ?, beerType = ?, beerOriginCountry = ?, alcoholContent = ? WHERE beerId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newType);
            pstmt.setString(3, newCountry);
            pstmt.setDouble(4, newAlcoholContent);
            pstmt.setInt(5, beerId);
            pstmt.executeUpdate();
            System.out.println("Ölinformationen har uppdaterats.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addNoteToBeer(Scanner scanner, int beerId) {
        System.out.println("Ange din anteckning för ölen: ");
        String noteText = scanner.nextLine();

        String sql = "INSERT INTO Note (beerNoteId, note) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, beerId);
            pstmt.setString(2, noteText);
            pstmt.executeUpdate();
            System.out.println("Anteckningen har lagts till.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteBeer(Scanner scanner) {
        System.out.println("Ange ID för ölen du vill radera: ");
        int beerId = scanner.nextInt();
        scanner.nextLine();

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

    public static void showBeerWithNotes() {
        String sql = "SELECT Beer.beerName, Note.note FROM Beer JOIN Note ON Beer.beerId = Note.beerNoteId";

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
                    searchBeerByName(scanner);
                case "2":
                    searchBeerByCountry(scanner);
                    break;
                case "3":
                    searchBeerByType(scanner);
                    break;
                case "4":
                    searchBeerByAlcoholContent(scanner);
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Felaktigt val. Välj igen.");
                    break;
            }
        }
    }

    public static void searchBeerByName(Scanner scanner) {
        System.out.println("Ange ölens namn att söka efter: ");
        String beerName = scanner.nextLine();

        String sql = "SELECT * FROM Beer WHERE beerName LIKE ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + beerName + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("ID\tNamn\t\tSort\t\tLand\t\tAlkoholhalt (promille)");
            boolean found = false;
            while (rs.next()) {
                System.out.println(rs.getInt("beerId") + "\t" +
                        rs.getString("beerName") + "\t" +
                        rs.getString("beerType") + "\t" +
                        rs.getString("beerOriginCountry") + "\t" +
                        rs.getDouble("alcoholContent") + "‰");
                found = true;
            }

            if (!found) {
                System.out.println("Ingen öl hittades med det namnet.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void searchBeerByCountry(Scanner scanner) {
        System.out.println("Ange ursprungsland: ");
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
        System.out.println("Ange ölsort att söka efter: ");
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
        scanner.nextLine();

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
