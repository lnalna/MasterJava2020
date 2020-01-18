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
        double concurrentThreadSum0 = 0.;
        double concurrentThreadSum1 = 0.;
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
            final int[][] concurrentMatrixC0 = MatrixUtil.concurrentMultiply0(matrixA, matrixB, executor);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("ConcurrentThreadSum0 time, sec: %.10f", duration);
            concurrentThreadSum0 += duration;

            start = System.currentTimeMillis();
            final int[][] concurrentMatrixC1 = MatrixUtil.concurrentMultiply1(matrixA, matrixB, executor);
            duration = (System.currentTimeMillis() - start) / 1000.;
            out("ConcurrentThreadSum1 time, sec: %.10f", duration);
            concurrentThreadSum1 += duration;

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
            if (!MatrixUtil.compare(matrixC0, concurrentMatrixC0)) {
                System.err.println("Comparison matrixC0 and concurrentMatrixC0 failed");
                break;
            }
            if (!MatrixUtil.compare(matrixC0, concurrentMatrixC1)) {
                System.err.println("Comparison matrixC0 and concurrentMatrixC1 failed");
                break;
            }
            count++;
        }
        executor.shutdown();
        out("\nAverage SingleThreadSum0 / 5 time, sec: %.10f", singleThreadSum0 / 5.);
        out("\nAverage SingleThreadSum1 / 5 time, sec: %.10f", singleThreadSum1 / 5.);
        out("\nAverage SingleThreadSum2 / 5 time, sec: %.10f", singleThreadSum2 / 5.);
        out("\nAverage SingleThreadSum3 / 5 time, sec: %.10f", singleThreadSum3 / 5.);
        out("\nAverage concurrentThreadSum0 time, sec: %.10f", concurrentThreadSum0 / 5.);
        out("\nAverage concurrentThreadSum1 time, sec: %.10f", concurrentThreadSum1 / 5.);
    }

    private static void out(String format, double ms) {
        System.out.println(String.format(format, ms));
    }
}
