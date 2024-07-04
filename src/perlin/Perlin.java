package perlin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Math.floor;
import static java.lang.Math.pow;

public class Perlin {
    public int width;
    public int height;
    public int octaves;
    public float persistance;

    public Perlin() {

    }

    public void printArray(double[][] m) {
        System.out.println(Arrays.deepToString(m));
    }

    // public void printArray1D(float[] m){
    //     System.out.println(Arrays.deepToString(m));
    // }
    public double getRandomNum() {
        Random rand = new Random();
        float max = 1.00F;
        float min = 0.00F;

        return min + rand.nextFloat() * (max - min);
    }

    public double[][] GetEmptyArray(int width, int height) {
        double[][] map = new double[width][height];

        this.width = width;
        this.height = height;

        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                map[row][col] = 0.0F;
            }
        }
        return map;
    }

    ;

    public double[][] GenerateWhiteNoise(int width, int height) {
        double[][] tmp = this.GetEmptyArray(width, height);
        for (int j = 0; j < this.height; j++) {
            for (int i = 0; i < this.width; i++) {
                tmp[j][i] = getRandomNum();
            }
        }
        System.out.println("White noise:");
        // printArray(tmp);
        return tmp;
    }

    ;

    public double[][] GenerateSmoothNoise(double[][] baseNoise, int octave) {
        this.width = baseNoise.length;
        this.height = baseNoise[0].length;
        System.out.println("base noise");
        System.out.println(baseNoise);

        double[][] smoothNoise = this.GetEmptyArray(this.width, this.height);

        double samplePeriod = floor(pow(2.0F, octave)); // calculates 2 ^ k
        double sampleFrequency = 1.0F / samplePeriod;

        for (int i = 0; i < this.width; i++) {
            //calculate the horizontal sampling indices
            int sample_i0 = (int) (floor(i / samplePeriod) * samplePeriod);
            int sample_i1 = (int) (floor(sample_i0 + samplePeriod) % this.width); //wrap around
            double horizontal_blend = (i - sample_i0) * sampleFrequency;

            for (int j = 0; j < this.height; j++) {
                //calculate the vertical sampling indices
                int sample_j0 = (int) (floor(j / samplePeriod) * samplePeriod);
                int sample_j1 = (int) floor(sample_j0 + samplePeriod) % this.height; //wrap around
                double vertical_blend = ((j - sample_j0) * sampleFrequency);
                System.out.println("sample_j0");
                System.out.println(sample_j0);
                System.out.println("sample_j1");
                System.out.println(sample_j1);

                //blend the top two corners
                double top = this.Interpolate(baseNoise[sample_i0][sample_j0], baseNoise[sample_i1][sample_j0], horizontal_blend);
                System.out.println("top");
                System.out.println(top);

                //blend the bottom two corners
                double bottom = this.Interpolate(baseNoise[sample_i0][sample_j1], baseNoise[sample_i1][sample_j1], horizontal_blend);
                System.out.println("bottom");
                System.out.println(bottom);
                //final blend
                smoothNoise[i][j] = this.Interpolate(top, bottom, vertical_blend);
            }
        }
        System.out.println("Smooth noise:");
        // printArray(smoothNoise);
        return smoothNoise;
    }

    ;

    public double Interpolate(double x0, double x1, double alpha) {
        return x0 * (1 - alpha) + alpha * x1;
    }

    ;

    public double[][] GeneratePerlinNoise(double[][] baseNoise, int octaveCount) {
        this.octaves = octaveCount;
        this.width = baseNoise.length;
        this.height = baseNoise[0].length;
        System.out.println("octaveCount:");
        System.out.println(octaveCount);
        //Array[] smoothNoise = new Array[octaveCount];
        // create a new ArrayList
        List<double[][]> smoothNoise = new ArrayList<>();

        double persistance = 0.2F;

        //generate smooth noise
        for (int i = 0; i < octaveCount; i++) {
            smoothNoise.add(this.GenerateSmoothNoise(baseNoise, i));
            System.out.println("Smoothing:");
            printArray(this.GenerateSmoothNoise(baseNoise, i));
        }

        double[][] perlinNoise = this.GetEmptyArray(this.width, this.height);
        float amplitude = 1.0F;
        float totalAmplitude = 0.0F;

        //blend noise together
        int octave = this.octaves - 1;

        while (octave >= 0) {
            amplitude *= persistance;
            totalAmplitude += amplitude;
            for (int i = 0; i < this.width; i++) {
                for (int j = 0; j < this.height; j++) {
                    //System.out.println("smoothNoise.get(octave)[j][i]:");
                    //printArray(smoothNoise.get(octave));
                    perlinNoise[j][i] = perlinNoise[j][i] + smoothNoise.get(octave)[j][i] * amplitude;
                }
            }
            octave--;
        }

        // Normalisation
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                perlinNoise[j][i] /= totalAmplitude;
            }
        }

        System.out.println("Smooth noise:");
        // printArray(perlinNoise);

        return perlinNoise;
    }

    public int[][] GetTileMap(double[][] perlinNoise, int k) {
        int width = perlinNoise.length;
        int height = perlinNoise[0].length;
        int[][] tilemap;
        tilemap = new int[this.height][this.width];
        for (var i = 0; i < this.width; i++) {
            for (var j = 0; j < this.height; j++) {
                double n = perlinNoise[j][i];
                tilemap[j][i] = (int) Math.floor(n / (1.0 / k));
            }
        }
        return tilemap;
    }

    public void exportTileMap(int[][] tilemap, String filename) throws IOException {
        StringBuilder builder = new StringBuilder();
        String delimiter = new String(" ");
        for (int i = 0; i < tilemap.length; i++) {
            for (int j = 0; j < tilemap.length; j++) {
                builder.append(tilemap[i][j] + "");//append to the output string
                if (j < tilemap.length - 1)//if this is not the last row element
                    builder.append(delimiter);//then add comma (if you don't like commas you can use spaces)
            }
            builder.append("\n");//append new line at the end of the row
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(builder.toString());//save the string representation of the board
        writer.close();
    }
}