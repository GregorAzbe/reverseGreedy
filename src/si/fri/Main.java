package si.fri;

import si.fri.dataStructures.IGraph;
import si.fri.problems.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

enum Mode{
    TEST(1, 0),
    MEASURE(10, 100);

    public int repetitions;
    public int skippedRepetitions;

    Mode(int repetitions, int skippedRepetitions) {
        this.repetitions = repetitions;
        this.skippedRepetitions = skippedRepetitions;
    }
}

enum Problem {
    DOMINATING_SET(new DominatingSet(), true),
    GRAPH_COLORING(new GraphColoring(), false),
    VERTEX_COVER(new VertexCover(), true);

    public IProblem implementation;
    public boolean doIncreaseVertexNumber;

    Problem(IProblem implementation, boolean doIncreaseVertexNumber) {
        this.implementation = implementation;
        this.doIncreaseVertexNumber = doIncreaseVertexNumber;
    }
}

public class Main {
    private final static int TEST_SIZE = 100, FROM_SIZE = 100, TO_SIZE = 1000, STEP = 100;
    private final static Mode mode = Mode.MEASURE;
    private final static Problem problem = Problem.DOMINATING_SET;

    public static void main(String[] args) {
        List<Results> results = new ArrayList<>();

        if(mode == Mode.MEASURE) {
            try {
                for (int nVertices = FROM_SIZE; nVertices <= TO_SIZE; nVertices += STEP)
                    results.add(runAlgorithm(problem, nVertices));

                Scanner reader = new Scanner(System.in);
                reader.useDelimiter("");
//                while (true) {
                System.out.println("Oznaka datoteke:");
                String label = reader.nextLine();
                    /*if (saveAns.equals("y")){
                        break;
                    } else if(saveAns.equals("n")) {
                        return;
                    }*/
//                }
                reader.close();

                String fileName = Results.saveResults(results, problem.toString(), label);
                try {
                    if (fileName != null) {
                        BufferedReader error = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(
                                String.format("python Charts/charts.py \"%s\" \"%s\"", fileName, problem.toString()))
                                .getErrorStream()));
                        String errLine;

                        while ((errLine = error.readLine()) != null)
                            System.out.println(errLine);
                    }
                } catch (Exception e) {
                    System.out.println("Zaganjanje skripte za izris grafa ni uspelo");
                }
            } catch (AlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            try {
                results.add(runAlgorithm(problem, TEST_SIZE));
                System.out.println(results.get(0));
            } catch (AlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    private static Results runAlgorithm(Problem problem, int problemSize) throws AlgorithmException {
        Results.Result[] resultsForOneProblemSize = new Results.Result[mode.repetitions];

        for (int i = 0; i < mode.repetitions + mode.skippedRepetitions; i++) {
            long start;
            IGraph graph;
            if(problem.doIncreaseVertexNumber){
                graph = problem.implementation.generateGraph(problemSize, problemSize * 10);
            } else {
                graph = problem.implementation.generateGraph(100, problemSize);
            }
            start = System.nanoTime();
            Solution greedySolution = problem.implementation.greedy(graph);
            long tGreedy = System.nanoTime() - start;
            if(!problem.implementation.test(graph, greedySolution)) throw new AlgorithmException();

            start = System.nanoTime();
            Solution reverseGreedySolution = problem.implementation.reverseGreedy(graph);
            long tReverseGreedy = System.nanoTime() - start;
            if(!problem.implementation.test(graph, reverseGreedySolution)) throw new AlgorithmException();

            if (i >= mode.skippedRepetitions) {
                resultsForOneProblemSize[i - mode.skippedRepetitions] = new Results.Result(
                        tGreedy,
                        greedySolution.getQuality(),
                        tReverseGreedy,
                        reverseGreedySolution.getQuality()
                );
            }
        }
        return new Results(problemSize, resultsForOneProblemSize);
    }

    static class Results {
        int problemSize;

        Results(int problemSize, Result[] results) {
            this.results = results;
            this.problemSize = problemSize;
        }

        Result[] results;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            for (Result result : results) {
                sb.append(problemSize);
                sb.append(';');
                sb.append(result);
            }

            return sb.toString();
        }

        static String saveResults(List<Results> results, String problemName, String label) {
            PrintWriter pw;
            try {
                if(label.isEmpty()) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                    Date date = new Date();
                    label = dateFormat.format(date);
                }
                String fileName = String.format("%s - %s.csv", problemName, label);
                pw = new PrintWriter(new File(fileName));
                StringBuilder sb = new StringBuilder();
                for(Results result : results) {
                    sb.append(result.toString());
                }
                pw.write(sb.toString());
                pw.close();
                return fileName;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        static class Result {
            long greedyTime;
            int greedyResultSize;
            long reverseGreedyTime;
            int reverseGreedyResultSize;

            Result(long greedyTime, int greedyResultSize, long reverseGreedyTime, int reverseGreedyResultSize) {
                this.greedyTime = greedyTime;
                this.greedyResultSize = greedyResultSize;
                this.reverseGreedyTime = reverseGreedyTime;
                this.reverseGreedyResultSize = reverseGreedyResultSize;
            }


            @Override
            public String toString() {
                return String.format("%s;%d;%s;%d\n", String.valueOf(greedyResultSize), greedyTime, String.valueOf(reverseGreedyResultSize), reverseGreedyTime);
            }
        }
    }
}
