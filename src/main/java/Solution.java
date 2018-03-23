
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Solution {

    public static void main(String[] args) {
        try {
            System.setIn(new FileInputStream(new File("input00.txt")));
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }

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
            int i1 = in.nextInt();
            int j1 = in.nextInt();
            int i2 = in.nextInt();
            int j2 = in.nextInt();
            maze.fillTunnel(i1,j1,i2,j2);
        }
        maze.depthFirstSearch();
        //maze.dbPMatrix();
        System.out.print(maze.computeP());
    }

    public enum CellTypeEnum{
        NONE,
        WALL,
        FREE,
        MINE,
        EXIT;

        public static CellTypeEnum convert(char c){
            switch(c){
                case '#': return WALL;
                case 'O': return FREE;
                case '*': return MINE;
                case 'A': return FREE;
                case '%': return EXIT;
                default: return NONE;
            }
        }
    }

    static class FMaze {
        public static final String SEPARATOR = " ";
        private static final double EPSILON = 1e-10;

        private int n, m, sX, sY;
        private Map<Integer,Node> tunnels;
        private Map<Integer,Node> mazeNode;
        private double[][] pMatrix;
        private double[] pRes;
        public FMaze(int n, int m, int k){
            this.n = n;
            this.m = m;
            mazeNode = new HashMap<>(n*m);
            pMatrix = new double[n*m][n*m];
            pRes = new double[n*m];
            tunnels = new HashMap<>(k);
        }

        public void fillMaze(int i, String r){

            IntStream.range(0,m).forEach(j->{
                mazeNode.put(getKey(i,j), new Node(i,j,getKey(i,j),CellTypeEnum.convert(r.charAt(j))));
                if(r.charAt(j) == 'A'){
                    sX = i;
                    sY = j;
                }
            });

        }

        public void fillTunnel(int x1, int y1, int x2, int y2){
            tunnels.put(getKey(x1-1,y1-1), mazeNode.get(getKey(x2-1,y2-1)));
            tunnels.put(getKey(x2-1,y2-1), mazeNode.get(getKey(x1-1,y1-1)));
        }

        public void depthFirstSearch(){
            List<Node> unvisitedNode = new LinkedList<>();
            List<Node> visitedNode = new LinkedList<>();
            Node start = mazeNode.get(getKey(sX,sY));
            unvisitedNode.add(start);

            while (!unvisitedNode.isEmpty()){
                Node tmp = unvisitedNode.remove(0);

                if(visitedNode.contains(tmp) /*|| tmp.type.equals(CellTypeEnum.MINE) /*p in questo caso e' zero*/) continue;
                visitedNode.add(tmp);

                final int pIndex = tmp.index;
                if(tunnels.containsKey(getKey(tmp.x,tmp.y))) {
                    //System.out.print("tunnell: "  + tmp + "->");
                    tmp = tunnels.get(getKey(tmp.x, tmp.y));
                    //System.out.println(tmp);
                }
                final Node currN = tmp;

                List<Node> children = getChildren(currN)
                        .filter(n -> n.x >= 0 && n.x < this.n && n.y >= 0 && n.y < this.m)
                        .filter(n -> !visitedNode.contains(n))
                        .filter(n -> n.type != CellTypeEnum.WALL)
                        .collect(Collectors.toList());

                PMatrixCreator pmCreator = getChildren(currN).
                        map(c -> {
                            if(c.type == CellTypeEnum.FREE) pMatrix[pIndex][c.index] = -1;
                            return c;
                        }).
                        collect(PMatrixCreator::new, PMatrixCreator::accept, PMatrixCreator::combine);

                List<Integer> res = pmCreator.compute();

                if(res.get(0)==0) {
                    pMatrix[pIndex][pIndex] = 1;
                    return;
                }

                pMatrix[pIndex][pIndex] += res.get(0);
                pRes[pIndex] = res.get(1);

                //System.out.println("Index: "+pIndex +" a:"+res.get(0)+" c:"+res.get(1));
                //System.out.println("Visited "+currN +" children:");
                //children.forEach(System.out::println);

                unvisitedNode.addAll(0,children);
            }
        }

        private Stream<Node> getChildren(Node n){
            return Stream.of(mazeNode.get(getKey(n.x-1,n.y)),
                    mazeNode.get(getKey(n.x+1,n.y)),
                    mazeNode.get(getKey(n.x,n.y-1)),
                    mazeNode.get(getKey(n.x,n.y+1)))
                    .filter(Objects::nonNull);
        }

        private Integer getKey(int x, int y){
            if(x<0 || y<0 || x>=n || y>=m)
                return null;
            return x*m + y;
        }

        public void dbPMatrix(){
            for(int i = 0 ; i < n*m; i++) {
                for (int j = 0; j < n * m; j++)
                    System.out.print(pMatrix[i][j] + SEPARATOR);
                System.out.println(pRes[i] + SEPARATOR);
            }
        }

        public double computeP(){
            double[] r = gElimination(pMatrix, pRes);
            return r[getKey(sX,sY)];
        }

        // Gaussian elimination with partial pivoting
        private double[] gElimination(double[][] A, double[] b) {
            int n = b.length;

            for (int p = 0; p < n; p++) {

                // find pivot row and swap
                int max = p;
                for (int i = p + 1; i < n; i++) {
                    if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                        max = i;
                    }
                }
                double[] temp = A[p]; A[p] = A[max]; A[max] = temp;
                double   t    = b[p]; b[p] = b[max]; b[max] = t;

                // singular or nearly singular
                if (Math.abs(A[p][p]) <= EPSILON) {
                    continue;
                }

                // pivot within A and b
                for (int i = p + 1; i < n; i++) {
                    double alpha = A[i][p] / A[p][p];
                    b[i] -= alpha * b[p];
                    for (int j = p; j < n; j++) {
                        A[i][j] -= alpha * A[p][j];
                    }
                }
            }

            // back substitution
            double[] x = new double[n];
            for (int i = n - 1; i >= 0; i--) {
                double sum = 0.0;
                for (int j = i + 1; j < n; j++) {
                    sum += A[i][j] * x[j];
                }
                if(A[i][i] > EPSILON)
                    x[i] = (b[i] - sum) / A[i][i];
            }
            return x;
        }

    }

    static class PMatrixCreator implements Consumer<Node> {
        int a = 0;
        int c = 0;

        public List<Integer> compute() {
            return Stream.of(a,c).collect(Collectors.toList());
        }

        public void accept(Node n) {
            if(n.type != CellTypeEnum.WALL) a++;
            if(n.type == CellTypeEnum.EXIT) c++;
        }

        public void combine(PMatrixCreator other) {
            a += other.a;
            c += other.c;
        }

    }

    static class Node{
        int x,y,index;
        CellTypeEnum type;

        public Node(int x, int y, int index, CellTypeEnum type){
            this.x = x;
            this.y = y;
            this.type = type;
            this.index = index;
        }

        @Override
        public String toString() {
            return "(" +x+ "," +y+ ")";
        }



    }
}


