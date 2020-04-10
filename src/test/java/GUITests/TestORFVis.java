package GUITests;

import orfgui.ORFVisualiser;

import java.sql.SQLException;

public class TestORFVis {

    public static void main(String[] args) {
        try {
            new ORFVisualiser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
