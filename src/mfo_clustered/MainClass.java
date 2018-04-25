package mfo_clustered;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainClass {

    ReadWriteFile ioFile = new ReadWriteFile();
    //ReadFileByTien ioFile = new ReadFileByTien();
    InitializeChromosome initChromo = new InitializeChromosome();
    Evaluate eva = new Evaluate(); //danh gia
    Chromosome chromo = new Chromosome(); //
    Crossover cross = new Crossover();
    Mutation mutation = new Mutation();
    private static double crossOverRate = 0.9;
    private static double mutationRate = 0.9;
    static Random rnd = new Random(1);
    public int maxGroup; // so dinh cua cluster co so dinh nhieu nhat
    public static int defaultPopLength = 100;
    ArrayList<Chromosome> population = new ArrayList<Chromosome>();

    public static void main(String[] args) {
        MainClass mainClass = new MainClass();
        int numOfCity = ReadWriteFile.numberOfCity;
        int numOfCluster = ReadWriteFile.numberOfCluster;
        int vertexInCluster[][] = ReadWriteFile.vertexInCluster;
        ArrayList<Chromosome> pop = init();
        //ReadFileByTien.clusterReadFiles("C:/Users/VIETTO/Desktop/DA/ClusteredTree_1/" + args[0] + ".clt");
        //PrintWriter pw1 = null;
        System.out.print(pop.get(0).getFactorialCost()[1] + " ");
        sortByCostIndex(pop, 0);
        System.out.println(pop.get(0).getFactorialCost()[0]);
        for (int i = 0; i < 100; i++) {
            ArrayList<Chromosome> tempPop = new ArrayList<Chromosome>();
            while (tempPop.size() < defaultPopLength) {
                double r = 0 + (1 - 0) * rnd.nextDouble();
                int par1 = rnd.nextInt(defaultPopLength);
                int par2 = rnd.nextInt(defaultPopLength);
                Chromosome temp1 = new Chromosome();
                Chromosome temp2 = new Chromosome();
                temp1.setEdgesMatrix(pop.get(par1).getEdgesMatrix());
                temp2.setEdgesMatrix(pop.get(par2).getEdgesMatrix());
                if (pop.get(par1).getSkillFactor() == pop.get(par2).getSkillFactor() || r < crossOverRate) {
                    Chromosome child = new Chromosome();
                    child.setEdgesMatrix(mainClass.cross.primRSTClusterCrossover(temp1.getEdgesMatrix(),
                        temp2.getEdgesMatrix(), numOfCity, numOfCluster, vertexInCluster, rnd));
                    tempPop.add(child);
                } else {
                    temp1
                        .setEdgesMatrix(mainClass.mutation.edgeClusteredTreeMutation(pop.get(par1).getEdgesMatrix(),
                            numOfCity, mutationRate, numOfCluster, vertexInCluster, rnd));
                    tempPop.add(temp1);
                    temp2
                        .setEdgesMatrix(mainClass.mutation.edgeClusteredTreeMutation(pop.get(par2).getEdgesMatrix(),
                            numOfCity, mutationRate, numOfCluster, vertexInCluster, rnd));
                    tempPop.add(temp2);
                }
            }
            if (tempPop.size() > defaultPopLength) {
                tempPop.remove(rnd.nextInt(defaultPopLength));
            }
            calculate(tempPop);
            evaluatePopulation(tempPop);
            ArrayList<Chromosome> newPop = new ArrayList<Chromosome>();
            for (int j = 0; j < defaultPopLength / 2; j++) {
                newPop.add(pop.get(j));
                newPop.add(tempPop.get(j));
            }
            pop = newPop;
            if (i % 10 == 0) {
                System.out.print(pop.get(0).getFactorialCost()[1] + " ");
                sortByCostIndex(pop, 0);
                System.out.println(pop.get(0).getFactorialCost()[0]);
                // drawResult(pop.get(0).getEdgesMatrix());
            }
        }
        calculate(pop);
        evaluatePopulation(pop);
//            System.out.print(pop.get(0).getFactorialCost()[1] + " ");
//            sortByCostIndex(pop, 0);
//            System.out.println(pop.get(0).getFactorialCost()[0]);
//       drawResult(pop.get(0).getEdgesMatrix());
//        try {
//            pw1 = new PrintWriter(new FileWriter(new File(args[1]), true));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        pw1.println(pop.get(0).getFactorialCost()[0]);
//        //pw1.println(pop.get(0).getCost());
//        pw1.close();
//        for (int j = 0; j < defaultPopLength; j++) {
//            System.out.print(pop.get(j).getFactorialCost()[0] + " ");
//        }
    }

    public static void evaluatePopulation(ArrayList<Chromosome> pop) {
        sortByCostIndex(pop, 0);
        for (int i = 0; i < defaultPopLength; i++) {
            pop.get(i).setFactorialRank(i + 1, 0);
        }
        sortByCostIndex(pop, 1);
        for (int i = 0; i < defaultPopLength; i++) {
            pop.get(i).setFactorialRank(i + 1, 1);
        }
        for (int i = 0; i < defaultPopLength; i++) {
            if (pop.get(i).getFactorialRank()[0] < pop.get(i).getFactorialRank()[1]) {
                pop.get(i).setSkillFactor(0);
                pop.get(i).setScalarFitness(1.0 / (pop.get(i).getFactorialRank()[0]));
            } else {
                pop.get(i).setSkillFactor(1);
                pop.get(i).setScalarFitness(1.0 / (pop.get(i).getFactorialRank()[1]));
            }

        }
        Collections.sort(pop, ChromosomeCmp.compareByScalarFitness);
    }

    public static void drawResult(double[][] weightMatrix) {
        City cities[] = ReadWriteFile.cities;
        for (int i = 0; i < weightMatrix.length; i++) {

            for (int j = 0; j < weightMatrix[i].length; j++) {
                if (weightMatrix[i][j] > 0) {
                    DrawLines d = new DrawLines();
                    d.draw(cities[i].getX(), cities[i].getY(), cities[j].getX(), cities[j].getY(), i + 1 + "",
                        j + 1 + "");
                }
            }
        }
    }

    public static ArrayList<Chromosome> init() {
        MainClass mc = new MainClass();
        int numOfCity = ReadWriteFile.numberOfCity;
        int numOfCluster = ReadWriteFile.numberOfCluster;
        double weightMatrix[][] = ReadWriteFile.weightMatrix;
        int vertexInCluster[][] = ReadWriteFile.vertexInCluster;
        int maxGroupValue = 0;
        for (int i = 0; i < numOfCluster; i++) {
            if (maxGroupValue < vertexInCluster[i].length) {
                maxGroupValue = vertexInCluster[i].length;
            }
        }
        mc.maxGroup = maxGroupValue; // lay duoc cluster co so dinh lon nhat. 
        for (int i = 0; i < defaultPopLength; i++) {
            Chromosome c = new Chromosome();
            c.setEdgesMatrix(mc.initChromo.primRSTForClusteredTree(numOfCity, numOfCluster, weightMatrix, vertexInCluster, rnd));

            mc.population.add(c);
        }

        calculate(mc.population);
        return mc.population;
    }

    public static void calculate(ArrayList<Chromosome> pop) {
        MainClass mc = new MainClass();
        int numOfCity = ReadWriteFile.numberOfCity;
        int numOfCluster = ReadWriteFile.numberOfCluster;
        double weightMatrix[][] = ReadWriteFile.weightMatrix;
        int vertexInCluster[][] = ReadWriteFile.vertexInCluster;
        int startVertex = ReadWriteFile.root;
        int maxGroupValue = 0;
        for (int i = 0; i < numOfCluster; i++) {
            if (maxGroupValue < vertexInCluster[i].length) {
                maxGroupValue = vertexInCluster[i].length;
            }
        }
        for (int i = 0; i < defaultPopLength; i++) {
            double[] temp = new double[2];
            temp[0] = mc.eva.evaluationDemo(pop.get(i).getEdgesMatrix(), vertexInCluster, weightMatrix, numOfCity, numOfCluster);
            //temp[0] = mc.eva.evaluation(pop.get(i).getEdgesMatrix(), vertexInCluster, weightMatrix, numOfCity, numOfCluster);
            //temp[0] = mc.eva.evaluation1(pop.get(i).getEdgesMatrix(), weightMatrix, numOfCity, startVertex);
            //System.out.println(maxGroupValue);
            temp[1] = mc.eva.evaluationSubPro(mc.eva.decodingMFOVertexByCluster(pop.get(i).getEdgesMatrix(), vertexInCluster, weightMatrix, numOfCluster),
                numOfCluster);
            pop.get(i).setFactorialCost(temp); // HOI TIEN CHO NAY
        }

    }

    public static void sortByCostIndex(ArrayList<Chromosome> pop, int index) {
        for (int i = 0; i < defaultPopLength; i++) {
            pop.get(i).cost = pop.get(i).getFactorialCost()[index];
        }
        Collections.sort(pop, ChromosomeCmp.compareByFactorialCost);
    }

}
