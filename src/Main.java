import perlin.Perlin;

import java.io.IOException;
import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static int num_tiles = 4;

    public static void main(String[] args) throws IOException {
         //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        Perlin p = new Perlin();
        double r = p.getRandomNum();
        double[][] noise = p.GenerateWhiteNoise(200,200);
        p.printArray(noise);
        double[][] perlin = p.GeneratePerlinNoise(noise, 3);
        System.out.println("perlin");
        p.printArray(perlin);

        int[][] tilemap = p.GetTileMap(perlin, num_tiles);
        System.out.println("tilemap:");
        System.out.println(Arrays.deepToString(tilemap));

        p.exportTileMap(tilemap, "C:/Users/mgikh/Desktop/GameDev/Java/2DGame/res/maps/map01.txt");
    }
}