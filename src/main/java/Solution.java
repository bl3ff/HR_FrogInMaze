
import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Solution {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int m = in.nextInt();
        int k = in.nextInt();
        for(int a0 = 0; a0 < n; a0++){
            String row = in.next();
            // Write Your Code Here
        }
        for(int a0 = 0; a0 < k; a0++){
            int i1 = in.nextInt();
            int j1 = in.nextInt();
            int i2 = in.nextInt();
            int j2 = in.nextInt();
            // Write Your Code Here
        }
        // Write Your Code Here
    }

    static class FMaze {
        public static final String SEPARATOR = " ";

        enum CellTypeEnum{
            NONE,
            WALL,
            FREE,
            MINE,
            EXIT;

            public static CellTypeEnum convert(String c){
                switch(c){
                    case "#": return WALL;
                    case "O": return FREE;
                    case "*": return MINE;
                    case "A": return FREE;
                    case "%": return EXIT;
                    default: return NONE;
                }
            }
        }

        int sX, sY;
        int nTunnels;
        CellTypeEnum[][] maze;
        public FMaze(int n, int m, int k){
            maze = new CellTypeEnum[n][m];
            nTunnels = k;
        }

        public void fillMaze(int i, String r){
            String[] s = r.split(SEPARATOR);
            for (int j=0; j< s.length;j++){
                maze[i][j] = CellTypeEnum.convert(s[j]);
                if(s[j].equals("A")){
                    sX = i;
                    sY = j;
                }
            }
        }
    }
}


