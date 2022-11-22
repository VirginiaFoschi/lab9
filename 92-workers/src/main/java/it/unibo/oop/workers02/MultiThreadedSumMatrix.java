package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix{

    private int nthreads;

    public MultiThreadedSumMatrix(final int nthread){
        this.nthreads = nthread;
    }

    private static class Worker extends Thread{
        private double[][] matrix;
        private int rows;
        private int columns;
        private final int startpos_x;   //colonna
        private final int startpos_y;   //riga
        private final int nelem;
        private long res;

        Worker(final double[][] matrix, final int rows, final int columns, final int startpos_x, final int startpos_y, final int nelem) {
            super();
            this.matrix = matrix;
            this.rows = rows;
            this.columns=columns;
            this.startpos_x = startpos_x;
            this.startpos_y = startpos_y;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            int sum_elem = 0;
            int final_x = (this.startpos_x+nelem-1) < this.columns ? (this.startpos_x+nelem-1): (this.startpos_x+nelem-1 -this.columns);
            int final_y = (this.startpos_x+nelem-1) < this.columns ? this.startpos_y : (this.startpos_y+1);
            //int final_x = (this.startpos_x+nelem-1) >= this.columns ? (nelem-(this.columns-this.startpos_x-1)-2) : (this.startpos_x+nelem-1);
            //int final_y = (this.startpos_x+nelem-1) >= this.columns ? this.startpos_y+1 : this.startpos_y;
            System.out.println("Working from position [" + startpos_y + "][" + startpos_x + "] to position [" + final_y + "][" + final_x + "]");
            for (int i = this.startpos_y; i < rows && i<=final_y; i++) {
                int j= sum_elem == 0? this.startpos_x : 0;
                for (; j < columns && sum_elem < nelem; j++) {
                    this.res += this.matrix[i][j];
                    sum_elem++;
                }
            }
        }

        public long getResult() {
            return this.res;
        }

    }


    @Override
    public double sum(double[][] matrix) {
        // TODO Auto-generated method stub
        int righe =matrix.length;
        int colonne =matrix[0].length;
        final int size = colonne % nthreads + righe / nthreads;
        System.out.println(""+size);
        /*
         * Build a matrix of workers
         */
        final List<Worker> workers = new ArrayList<>(nthreads);
        int last_col=0;
        int last_row=0;
        for (; last_row < righe ; ) {
            workers.add(new Worker(matrix,righe,colonne,last_col, last_row,size));
            last_col = (last_col +size) < colonne ? (last_col+size) : (last_col+size-colonne);
            last_row = (last_col+size) < colonne ? last_row : last_row+1;
            //last_col = (last_col+size) >= colonne ? (size-(colonne-last_col-1)-1) : (last_col+size);
            //last_row = (last_col+size) >= colonne ? last_row+1 : last_row;
        }
        /*
         * Start them
         */
        for (final Worker w: workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        long sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }
    
}