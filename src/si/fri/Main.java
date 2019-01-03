package si.fri;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

public class Main {
    private final static int TEST_SIZE = 100, FROM_SIZE = 100, TO_SIZE = 1000, STEP = 100;
    private final static Mode mode = Mode.MEASURE;
    private final static IProblem problem = new VertexCover();

    public static void main(String[] args) {
        List<Results> results = new ArrayList<>();

        if(mode == Mode.MEASURE) {
            try {
                for (int nVertices = FROM_SIZE; nVertices <= TO_SIZE; nVertices += STEP)
                    results.add(runAlgorithm(problem, nVertices));

                String fileName = Results.saveResults(results, problem.toString());
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

    private static Results runAlgorithm(IProblem problem, int nVertices) throws AlgorithmException {
        Results.Result[] resultsForOneProblemSize = new Results.Result[mode.repetitions];

        for (int i = 0; i < mode.repetitions + mode.skippedRepetitions; i++) {
            long start;
            IGraph graph = problem.generateGraph(nVertices, nVertices * 10);
            start = System.nanoTime();
            Solution greedySolution = problem.greedy(graph);
            long tGreedy = System.nanoTime() - start;
            if(!problem.test(graph, greedySolution)) throw new AlgorithmException();

            start = System.nanoTime();
            Solution reverseGreedySolution = problem.reverseGreedy(graph);
            long tReverseGreedy = System.nanoTime() - start;
            if(!problem.test(graph, reverseGreedySolution)) throw new AlgorithmException();

            if (i >= mode.skippedRepetitions) {
                resultsForOneProblemSize[i - mode.skippedRepetitions] = new Results.Result(
                        tGreedy,
                        greedySolution.getQuality(),
                        tReverseGreedy,
                        reverseGreedySolution.getQuality()
                );
            }
        }
        return new Results(nVertices, resultsForOneProblemSize);
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

        static String saveResults(List<Results> results, String problemName) {
            PrintWriter pw;
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                Date date = new Date();
                String fileName = String.format("%s - %s.csv", problemName, dateFormat.format(date));
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
