package si.fri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

    public static void main(String[] args) {
        List<Results> results = new ArrayList<>();

        if(mode == Mode.MEASURE) {
            for (int nVertices = FROM_SIZE; nVertices <= TO_SIZE; nVertices += STEP) {
                results.add(runAlgorithm(nVertices));
            }
            Results.saveResults(results, "Dominantna množica");
        } else {
            results.add(runAlgorithm(TEST_SIZE));
            System.out.println(results.get(0));
        }
    }

    private static Results runAlgorithm(int nVertices){
        Results.Result[] resultsForOneProblemSize = new Results.Result[mode.repetitions];

        for (int i = 0; i < mode.repetitions + mode.skippedRepetitions; i++) {
            long start;
            Graph graph = new Graph(nVertices, nVertices * 10);
            start = System.nanoTime();
            List<Integer> dominantVerticesGreedy = DominatingSet.greedy(graph);
            long tGreedy = System.nanoTime() - start;

            start = System.nanoTime();
            List<Integer> dominantVerticesReverseGreedy = DominatingSet.reverseGreedy(graph);
            long tReverseGreedy = System.nanoTime() - start;

            if (i >= mode.skippedRepetitions) {
                resultsForOneProblemSize[i - mode.skippedRepetitions] = new Results.Result(
                        tGreedy,
                        dominantVerticesGreedy.size(),
                        tReverseGreedy,
                        dominantVerticesReverseGreedy.size()
                );
            }
        }
        return new Results(nVertices, resultsForOneProblemSize);
    }

    /*private static void printList(List<Integer> list){
        for(int l : list){
            System.out.print(l + " ");
        }
        System.out.println();
    }

    private static void printResults(List<Integer> dominantVertices, Graph graph){
        System.out.println(String.format("V dominantni množici je %d vozlišč (deluje %s)", dominantVertices.size(), DominatingSet.test(graph, dominantVertices) ? "pravilno" : "nepravilno"));
        System.out.println("Vozlišča v dominantni množici:");
        dominantVertices.sort(Comparator.comparingInt(o -> o));
        printList(dominantVertices);
    }*/

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

        static void saveResults(List<Results> results, String algorithmName) {
            PrintWriter pw;
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                Date date = new Date();
                pw = new PrintWriter(new File(String.format("%s - %s.csv", algorithmName, dateFormat.format(date))));
                StringBuilder sb = new StringBuilder();
                for(Results result : results) {
                    sb.append(result.toString());
                }

                pw.write(sb.toString());
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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

                return String.format("%s;%d;%s;%d;\n", String.valueOf(greedyResultSize), greedyTime, String.valueOf(reverseGreedyResultSize), reverseGreedyTime);
            }
        }
    }
}
