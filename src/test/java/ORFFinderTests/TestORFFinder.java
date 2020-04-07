package ORFFinderTests;

import orffinder.ORFFinder;

import java.io.File;
import java.io.IOException;

public class TestORFFinder {

    public static void main(String[] args) {
        try {
            File testfile = new File("D:\\GitHub\\Course7Informatica\\src\\test\\resources\\data\\DNA.txt");
            ORFFinder orfFinder = new ORFFinder(testfile);
            orfFinder.findOrfs();//
            orfFinder.printStats();
            orfFinder.getallOrfs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
