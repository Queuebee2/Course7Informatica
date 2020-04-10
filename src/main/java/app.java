import orfgui.ORFVisualiser;

import java.sql.SQLException;

public class app {
    public static void main(String[] args) {
        try {
            new ORFVisualiser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
