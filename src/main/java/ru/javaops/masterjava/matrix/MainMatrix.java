package ru.javaops.masterjava.matrix;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * gkislin
 * 03.07.2016
 */
public class MainMatrix {
    private static final int MATRIX_SIZE = 1000;
    private static final int THREAD_NUMBER = 10;

    private final static ExecutorService executor = Executors.newFixedThreadPool(MainMatrix.THREAD_NUMBER);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int[][] matrixA = MatrixUtil.create(MATRIX_SIZE);
        final int[][] matrixB = MatrixUtil.create(MATRIX_SIZE);

        double singleThreadSum0 = 0.;
        double singleThreadSum1 = 0.;
        double singleThreadSum2 = 0.;
        double singleThreadSum3 = 0.;
        double concurrentThreadSum = 0.;
        int count = 1;
        while (count < 6) {
            System.out.println("Pass " + count);
            long start = System.currentTimeMillis();
            final int[][] matrixC0 = MatrixUtil.singleThreadMultiply0(matrixA, matrixB);
            double duration = (System.currentTimeMillis() - start) / 1000.;
            out("SingleThreadSum0 time, sec: %.10f", duration);
            singleThreadSum0 += duration;

            start = System.currentTimeMillis();
            final int[][] matrixC1 = MatrixUtil.singleThreadMultiply1(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("SingleThreadSum1 time, sec: %.10f", duration);
            singleThreadSum1 += duration;

            start = System.currentTimeMillis();
            final int[][] matrixC2 = MatrixUtil.singleThreadMultiply2(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("SingleThreadSum2 time, sec: %.10f", duration);
            singleThreadSum2 += duration;

            start = System.currentTimeMillis();
            final int[][] matrixC3 = MatrixUtil.singleThreadMultiply3(matrixA, matrixB);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("SingleThreadSum3 time, sec: %.10f", duration);
            singleThreadSum3 += duration;

            start = System.currentTimeMillis();
            final int[][] concurrentMatrixC = MatrixUtil.concurrentMultiply(matrixA, matrixB, executor);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("ConcurrentThreadSum time, sec: %.10f", duration);
            concurrentThreadSum += duration;

            if (!MatrixUtil.compare(matrixC0, matrixC1)) {
                System.err.println("Comparison matrixC0 and matrixC1 failed");
                break;
            }
            if (!MatrixUtil.compare(matrixC0, matrixC2)) {
                System.err.println("Comparison matrixC0 and matrixC2 failed");
                break;
            }
            if (!MatrixUtil.compare(matrixC0, matrixC3)) {
                System.err.println("Comparison matrixC0 and matrixC3 failed");
                break;
            }
            if (!MatrixUtil.compare(matrixC0, concurrentMatrixC)) {
                System.err.println("Comparison matrixC0 and concurrentMatrixC failed");
                break;
            }
            count++;
        }
        executor.shutdown();
        out("\nAverage SingleThreadSum0 / 5 time, sec: %.10f", singleThreadSum0 / 5.);
        out("\nAverage SingleThreadSum1 / 5 time, sec: %.10f", singleThreadSum1 / 5.);
        out("\nAverage SingleThreadSum2 / 5 time, sec: %.10f", singleThreadSum2 / 5.);
        out("\nAverage SingleThreadSum3 / 5 time, sec: %.10f", singleThreadSum3 / 5.);
        out("\nAverage concurrentThreadSum time, sec: %.10f", concurrentThreadSum / 5.);
    }

    private static void out(String format, double ms) {
        System.out.println(String.format(format, ms));
    }
}
