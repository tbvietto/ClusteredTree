package mfo_clustered;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Arrays;

public class Evaluate {

    GraphMethods graph_Method_Class = new GraphMethods();
    Mutation mutation_Class = new Mutation();
    InitializeChromosome init_Chromo = new InitializeChromosome();

    public double evaluation1(double[][] tree, double[][] weightMatrix, int num_vertex, int startVertex) {
        double[] distances = new double[num_vertex];// distance between root and
        // the others
        double sum = 0;
        distances[startVertex] = 0;
        boolean[] mark = new boolean[num_vertex];
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < num_vertex; i++) {
            mark[i] = true;
        }
        queue.add(startVertex);
        while (!queue.isEmpty()) {
            int u = queue.poll();
            mark[u] = false;
            for (int i = 0; i < num_vertex; i++) {
                if (tree[u][i] > 0 && mark[i]) {
                    queue.add(i);
                    mark[i] = false;
                    distances[i] = distances[u] + weightMatrix[u][i];
                    sum += distances[i];
                }
            }
        }
        return sum;
    }

    public double evaluationDemo(double[][] Tree, int[][] vertex_in_Cluster, double[][] weightMatrix, int num_Vertex, int num_Cluster) {
        ArrayList<Integer> pre1 = new ArrayList<Integer>();
        GraphMethods gra = new GraphMethods();
        double distance = 0;

        for (int i = 0; i < num_Cluster; i++) {
            for (int j = i + 1; j < num_Cluster; j++) {
                for (int k = 0; k < vertex_in_Cluster[i].length; k++) {
                    for (int l = 0; l < vertex_in_Cluster[j].length; l++) {
                        pre1 = gra.printPath(vertex_in_Cluster[i][k], vertex_in_Cluster[j][l],
                            gra.dijkstraBetweenTwoVertex(convertToNewTypeMatrix(weightMatrix, Tree, num_Vertex),
                            num_Vertex, vertex_in_Cluster[i][k], vertex_in_Cluster[j][l]));
                        distance = distance + distance_Bettwen_Two_Vertex(weightMatrix, pre1);
                    }
                }
            }
        }
        return distance / 2;
    }

    public double evaluationSubPro(double[][] spanningClusterTree, int numOfCluster) {
        double sumDistance = 0;
        for (int i = 0; i < numOfCluster; i++) {
            for (int j = i + 1; j < numOfCluster; j++) {
                sumDistance = sumDistance + spanningClusterTree[i][j];
            }
        }
        return sumDistance;
    }

    public double[][] convertToNewTypeMatrix(double[][] weightMatrix, double[][] edgeMatrix, int num_vertex) {
        double temp[][] = new double[num_vertex][num_vertex];
        for (int i = 0; i < num_vertex; i++) {
            for (int j = 0; j < num_vertex; j++) {
                if (edgeMatrix[i][j] == 0) {
                    temp[i][j] = 0;
                } else {
                    temp[i][j] = weightMatrix[i][j];
                }
            }
        }
        return temp;
    }

    public  double distance_Bettwen_Two_Vertex(double[][] weightMatrix, ArrayList<Integer> pre1){
        double distance = 0;
        for (int i = 0; i < pre1.size() - 1; i++) {
            distance = distance + weightMatrix[pre1.get(i)][pre1.get(i+1)];
//            System.out.print(pre1.get(i)+ " ");
//            if(i == (pre1.size() -2)){
//                System.out.println(pre1.get(i+1));
//            }
        }
        //System.out.println("");        
        return distance;
    }
    public double distance_in_two_Vertex(double[][] weightMatrix, int[] pre) {
        double distance = 0;
        for (int i = 0; i < pre.length - 1; i++) {
            if (pre[i] != -1) {
                distance = distance + weightMatrix[i][i + 1];
            }
        }
        return distance;
    }

    /**
     * *******************************************************************************************************************************************
     * Decoding cho bai toan cay khung Giam tu n dinh ve thanh m dinh 01. Xóa
     * các đỉnh và cạnh liên thuộc với nó > m 02. Tìm các thành phần liên thông
     * 03. Nối các thành phần liên thông thứ i -> i + 1 + Chọn ngẫu nhiên ở mỗi
     * thành phần liên thông 1 đỉnh + Chọn đỉnh đầu tiên
     * ******************************************************************************************************************************************
     */
    public double[][] decodingMFOVertexByCluster(double[][] edgesMatrix, int[][] vertexInCluster, double[][] weightMatrix, int numOfCluster) {
        double[][] spanningClusterTree = new double[numOfCluster][numOfCluster];
        int temp = 0;
        for (int i = 0; i < numOfCluster; i++) {
            for (int j = i + 1; j < numOfCluster; j++) {
                for (int k = 0; k < vertexInCluster[i].length; k++) {
                    for (int l = 0; l < vertexInCluster[j].length; l++) {
                        if (edgesMatrix[vertexInCluster[i][k]][vertexInCluster[j][l]] == 1) {
                            spanningClusterTree[i][j] = weightMatrix[vertexInCluster[i][k]][vertexInCluster[j][l]];
                            temp = 1;
                        } else {
                            continue;
                        }
                    }
                }
                if (temp == 0) {
                    spanningClusterTree[i][j] = 0;
                }
            }
        }
        return spanningClusterTree;
    }

    public double[][] decodingMFOVertexInSubGraph(double[][] ind_Matrix, int max_Genes, int num_Gen_of_Task_j) {
        double[][] tmp_Matrix = new double[max_Genes][max_Genes];
        // 01. Tìm các đỉnh lớn hơn num_Gen_of_Task_j và xóa nó cùng cạnh liên
        // thuộc
        for (int i = 0; i < num_Gen_of_Task_j; i++) {
            for (int j = 0; j < num_Gen_of_Task_j; j++) {
                if ((ind_Matrix[i][j] > 0) && (ind_Matrix[i][j] < Double.MAX_VALUE)) {
                    tmp_Matrix[i][j] = 1.0f;
                } else {
                    tmp_Matrix[i][j] = 0.0f;
                }
            }
        }
        // 02. Tìm các thành phần liên thông va lay moi tp lien thong 1 dinh
        int[] tp_LT = graph_Method_Class.get_Vertex_In_Each_SubGraph(tmp_Matrix, num_Gen_of_Task_j);

        // 03. Nối các thành phần liên thông thứ i -> i + 1
        for (int i = 0; i < tp_LT.length - 1; i++) {
            tmp_Matrix[tp_LT[i]][tp_LT[i + 1]] = 1.0f;
            tmp_Matrix[tp_LT[i + 1]][tp_LT[i]] = 1.0f;
        }

        // Tao ra ma tran ket qua
        double[][] final_Matrix = new double[num_Gen_of_Task_j][num_Gen_of_Task_j];
        for (int i = 0; i < num_Gen_of_Task_j; i++) {
            for (int j = 0; j < num_Gen_of_Task_j; j++) {
                final_Matrix[i][j] = (int) tmp_Matrix[i][j];
            }
        }
        return final_Matrix;
    }

}
