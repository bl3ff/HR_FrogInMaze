import java.util.*;

public class Solution {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int m = in.nextInt();
        int k = in.nextInt();
        FMaze maze = new FMaze(n,m,k);
        for(int a0 = 0; a0 < n; a0++){
            String row = in.next();
            maze.fillMaze(a0,row);
        }
        for(int a0 = 0; a0 < k; a0++){
            String row = in.next();
            maze.fillTunnel(a0,row);
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
        Map<String, int[]> tunnels;
        CellTypeEnum[][] maze;
        
        public FMaze(int n, int m, int k){
            maze = new CellTypeEnum[n][m];
            tunnels = new HashMap<>(2*k);
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

        public void fillTunnel(int i, String r){
            String[] s = r.split(SEPARATOR);
            int[] v1 = new int[2];
            v1[0] = Integer.parseInt(s[2]);
            v1[1] = Integer.parseInt(s[3]);
            tunnels.put(s[0]+SEPARATOR+s[1], v1);
            v1[0] = Integer.parseInt(s[0]);
            v1[1] = Integer.parseInt(s[1]);
            tunnels.put(s[2]+SEPARATOR+s[3], v1);
        }
    }


}


