package mfo_clustered;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class InitializeChromosome {

    /**
     * *******************************************************************************************************************************************
     * Tìm danh sách các cạnh có đỉnh kề với đỉnh v mà không thuộc tập C voi tap
     * C la tap luu cac dinh da duyet qua.
     * ******************************************************************************************************************************************
     */
    public ArrayList<Edge> findEdges(int num_Verties, int v, double[][] weight_Matrix, List<Integer> c) {
        ArrayList<Edge> lst_Edge = new ArrayList<Edge>();
        for (int i = 0; i < num_Verties; i++) {
            if (i != v) {
                if (!c.contains(i)) {
                    if (weight_Matrix[v][i] > 0) {
                        Edge edge_tmp = new Edge(v, i);
                        lst_Edge.add(edge_tmp);
                    }
                }
            }
        }
        return lst_Edge;
    }

    /**
     * *******************************************************************************************************************************************
     * G. R. Raidl and B. A. Julstrom, “Edge sets: an effective evolutionary
     * coding of spanning trees,” IEEE Transactions on evolutionary computation,
     * vol. 7, no. 3, pp. 225–239, 2003. Thuật toán tạo cây khung ngẫu nhiên mô
     * phỏng thuật toán PRIM Trả về NULL nếu không tìm được cây khung
     * ******************************************************************************************************************************************
     */
    public double[][] primRST(int num_Verties, double[][] weight_Matrix, Random rnd) {
        int rnd_Verties = -1, idx_Rnd_Edge = -1;
        ArrayList<Integer> C = new ArrayList<Integer>();
        ArrayList<Edge> A = new ArrayList<Edge>();

        double[][] T = new double[num_Verties][num_Verties];
        for (int i = 0; i < num_Verties; i++) {
            for (int j = i; j < num_Verties; j++) {
                T[i][j] = 0;
                T[j][i] = 0;
            }
        }

        rnd_Verties = rnd.nextInt(num_Verties);
        C.add(rnd_Verties);
        A.addAll(findEdges(num_Verties, rnd_Verties, weight_Matrix, C));

        while (C.size() < num_Verties) {
            if (A.size() == 0) // Không tìm được cây khung
            {
                return null;
            }

            idx_Rnd_Edge = rnd.nextInt(A.size()); // Do các cạnh thuộc A có ít
            // nhất 1 đỉnh thuộc C ->
            // thuật toán tạo thì là
            // start_point
            Edge edge_Tmp = new Edge(A.get(idx_Rnd_Edge).getStartVertex(), A.get(idx_Rnd_Edge).getEndVertex());
            A.remove(idx_Rnd_Edge);
            if (!C.contains(edge_Tmp.getEndVertex())) {
                T[edge_Tmp.getStartVertex()][edge_Tmp.getEndVertex()] = 1.0f;
                T[edge_Tmp.getEndVertex()][edge_Tmp.getStartVertex()] = 1.0f;

                C.add(edge_Tmp.getEndVertex());
                A.addAll(findEdges(num_Verties, edge_Tmp.getEndVertex(), weight_Matrix, C));
            }
        }
        return T;
    }

    /**
     * *******************************************************************************************************************************************
     * Tạo ma trận trọng số cho cluster từ ma trận của đồ thị G ban đầu Ma trận
     * kết quả có kích thước: num_Vertex_in_Cluster * num_Vertex_in_Cluster
     *
     *******************************************************************************************************************************************
     */
   
    public double[][] createWeightMatrixForCluster(int num_Vertex, int num_Vertex_in_Cluster, double[][] weight_Matrix,
        int[] vertex_In_Cluster) {
        double[][] weight_Cluster_Matrix = new double[num_Vertex_in_Cluster][num_Vertex_in_Cluster];

        for (int i = 0; i < num_Vertex_in_Cluster; i++) {
            for (int j = 0; j < num_Vertex_in_Cluster; j++) {
                weight_Cluster_Matrix[i][j] = weight_Matrix[vertex_In_Cluster[i]][vertex_In_Cluster[j]];
            }
        }
        return weight_Cluster_Matrix;
    }

    /**
     * *******************************************************************************************************************************************
     * Tạo cây khung theo thuật toán primRST: G. R. Raidl and B. A. Julstrom,
     * “Edge sets: an effective evolutionary coding of spanning trees,” IEEE
     * Transactions on evolutionary computation, vol. 7, no. 3, pp. 225–239,
     * 2003. Tạo cây khung cho tập các đỉnh trong: vertex_In_Cluster
     * ******************************************************************************************************************************************
     */
    public double[][] primRSTForClusteredTree(int num_Vertex, int num_Cluster, double[][] weight_Matrix,
        int[][] vertex_In_Cluster, Random rnd) {
        double[][] T = new double[num_Vertex][num_Vertex];
        // Khoi tao
        for (int i = 0; i < num_Vertex; i++) {
            for (int j = i; j < num_Vertex; j++) {
                T[i][j] = 0;
                T[j][i] = 0;
            }
        }

        // Tao cay khung cho tung cluster
        double[][] weight_Cluster_Matrix;
        double[][] spanning_Tree_of_Cluster;
        for (int i = 0; i < num_Cluster; i++) {
            int num_Vertex_in_Cluster = vertex_In_Cluster[i].length;
            weight_Cluster_Matrix = createWeightMatrixForCluster(num_Vertex, num_Vertex_in_Cluster, weight_Matrix,
                vertex_In_Cluster[i]);
            spanning_Tree_of_Cluster = primRST(num_Vertex_in_Cluster, weight_Cluster_Matrix, rnd);
            // Chuyen ra cay khung cua do thi G
            for (int k = 0; k < num_Vertex_in_Cluster; k++) {
                for (int j = 0; j < num_Vertex_in_Cluster; j++) {
                    if (spanning_Tree_of_Cluster[k][j] > 0) {
                        T[vertex_In_Cluster[i][k]][vertex_In_Cluster[i][j]] = spanning_Tree_of_Cluster[k][j];
                    }
                }
            }
        }
        // Tạo cây khung cho đại diện các nhóm
        // Chọn mỗi nhóm 1 đỉnh
        int[] idx_Cluster = new int[num_Cluster];
        for (int i = 0; i < num_Cluster; i++) {
            int vt = rnd.nextInt(vertex_In_Cluster[i].length);
            idx_Cluster[i] = vertex_In_Cluster[i][vt];
        }

        weight_Cluster_Matrix = createWeightMatrixForCluster(num_Vertex, num_Cluster, weight_Matrix, idx_Cluster);
        spanning_Tree_of_Cluster = primRST(num_Cluster, weight_Cluster_Matrix, rnd);

        // Chuyen ra cay khung cua do thi G
        for (int k = 0; k < num_Cluster; k++) {
            for (int j = 0; j < num_Cluster; j++) {
                if (spanning_Tree_of_Cluster[k][j] > 0) {
                    T[idx_Cluster[k]][idx_Cluster[j]] = spanning_Tree_of_Cluster[k][j];
                }
            }
        }
        
        return T;
    }

    public double[][] localSearch(int numCluster, double[][] weightMatrix, int[][] vertex_In_Cluster, int[] idx,
        double[][] edMat) {
        double temp[][] = new double[weightMatrix.length][weightMatrix.length];
        for (int i = 0; i < weightMatrix.length; i++) {
            for (int j = 0; j < weightMatrix.length; j++) {
                temp[i][j] = edMat[i][j];
            }
        }
        for (int i = 0; i < numCluster; i++) {
            for (int j = i + 1; j < numCluster; j++) {
                if (edMat[idx[i]][idx[j]] > 0) {
                    int[] arr = findMinDistance(weightMatrix, vertex_In_Cluster[i], vertex_In_Cluster[j]);
                    temp[idx[i]][idx[j]] = 0;
                    idx[i] = arr[0];
                    idx[j] = arr[1];
                    temp[idx[i]][idx[j]] = 1;
                }
            }
        }
        return temp;
    }

    public int[] findMinDistance(double[][] weightMatrix, int[] a1, int a2[]) {
        int result[] = new int[2];
        double min = weightMatrix[a1[0]][a2[0]];
        for (int i = 0; i < a1.length; i++) {
            for (int j = 0; j < a2.length; j++) {
                if (weightMatrix[a1[i]][a2[j]] < min) {
                    min = weightMatrix[a1[i]][a2[j]];
                    result[0] = a1[i];
                    result[1] = a2[j];
                }
            }
        }
        return result;
    }
    
    public double[][] BFSTree(double[][] weightMatrix, int num_vertex, int startVertex) {

        double[][] BFStree = new double[num_vertex][num_vertex];
        boolean[] mark = new boolean[num_vertex];
        Queue<Integer> queue = new LinkedList<>();
        // initialize label for each vertex,  
        for (int i = 0; i < num_vertex; i++) {
            mark[i] = true;
        }
        mark[startVertex] = false;
        queue.add(startVertex);

        while (!queue.isEmpty()) {
            int considerVertex = queue.poll();
            for (int i = 0; i < num_vertex; i++) {
                if (weightMatrix[considerVertex][i] > 0 && mark[i]) {
                    queue.add(i);
                    mark[i] = false;
                    BFStree[considerVertex][i] = 1.0f;
                    BFStree[i][considerVertex] = 1.0f;

                }
            }

        }
        return BFStree;
    }
}
