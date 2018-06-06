package com.company;

/* Author: Abdul El Badaoui
 * Student Number: 5745716
 * Description: The following program provides the average estimation of 5 randomly chosen path to check estimation
 * value of each level for an (n-1)(n-1) d Array. The first column and row of the latin Square are prefilled in the
 * reduced form so they are not required in the d Array and the estimation Array and the average Array calculation.
 * */

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    //global parameters
    static Scanner userInput;
    static int n;
    static long count;
    static int fillCount;
    static BigInteger [][] estimate,  averages;
    static int [][] d;
    static Random random;
    static PrintWriter fileOutput;

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        n = 0;
        userInput = new Scanner(System.in);
        //while loop to input a valid integer for an existing file path name or insert your own
        while (n>15 || n<9 ){
            System.out.println("Please enter an integer value from 9 to 15 for the latin squares");
            //check if an integer was input
            while (!userInput.hasNextInt()) {
                System.out.println("That's not a preferred number!");
                userInput.next();
            }
            n = userInput.nextInt();
        }
        fileOutput = new PrintWriter("a2q2out for n="+n+".txt", "UTF-8");// output file creation
        int [][]latinSquare =  new int[n][n];
        estimate = new BigInteger[n-1][n-1];
        //initialize the latin square with column and row initialized to its reduced form
        for (int i = 1; i<=n; i++){
            for (int j = 1; j<=n; j++){
                if (i==1){
                    latinSquare[i-1][j-1] = j;
                }
                else if (j==1){
                    latinSquare[i-1][j-1] = i;
                }
                else{
                    latinSquare[i-1][j-1]=0;
                }
            }
        }


        //initialize the fillCount to 0
        fillCount = 0;
        // uses the fillCount to increment the row and column to passes to the next cell
        int row = ((fillCount/(n-1))+1);
        int column = ((fillCount%(n-1))+1);

        //initialize the lists
        ArrayList<int [][]> listOfDifferentD =  new ArrayList<>();
        ArrayList<BigInteger[][]> listOfEstimatedArray =  new ArrayList<>();
        averages = new BigInteger[n-1][n-1];
        //run the backtrack algorithms to get the d array and estimate array and make copies of them to add to their
        //respective lists
        for (int i = 0; i<5; i++){
            random = new Random();
            d = null;
            backTrack(row, column, latinSquare);
            int [][] coppiedSquareD = squareCopy(d);
            listOfDifferentD.add(coppiedSquareD);
            estimate = generateEstimate(coppiedSquareD);
            BigInteger[][] copiedEstimate = squareCopyBI(estimate);
            listOfEstimatedArray.add(copiedEstimate);
        }
        //get the averages of the estimates for each level

        fileOutput.println("For n = "+n+" the average estimation for each level is:");
        fileOutput.println("------------------------------------------------------");
        for (int i = 0; i<(n-1)*(n-1); i++){
            averages[(i/(n-1))][i%(n-1)] = BigInteger.ZERO;
            for (int j = 0; j<5; j++){
                averages[(i/(n-1))][i%(n-1)] = averages[(i/(n-1))][i%(n-1)].add(listOfEstimatedArray.get(j)[(i/(n-1))][i%(n-1)]);
            }

            averages[(i/(n-1))][i%(n-1)] = averages[(i/(n-1))][i%(n-1)].divide(BigInteger.valueOf(5));
            fileOutput.println("Level "+(i+1)+": "+ averages[(i/(n-1))][i%(n-1)]);
        }
        fileOutput.println("------------------------------------------------------");
        fileOutput.println(Arrays.deepToString(averages).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
        fileOutput.close();

    }
    //method to fill the estimate Big Integer array using an initial scale of 1 and the passed in d Array
    public static BigInteger[][] generateEstimate( int [][] dArray){
        BigInteger scale = BigInteger.ONE;
        BigInteger [][] estimatedArray = new BigInteger[n-1][n-1];
        for (int i = 0; i<(n-1); i++){
            for (int j = 0; j<(n-1); j++){
                estimatedArray[i][j] = scale.multiply(BigInteger.valueOf(dArray[i][j]));
                scale = scale.multiply(BigInteger.valueOf(dArray[i][j]));
            }
        }
        return estimatedArray;

    }
    //method to return a list of Viable Candidates per cell location
    public static ArrayList<Integer> checkCandidateD(int row, int column, int [][] currentSquare){

        ArrayList<Integer> existingInteger = new ArrayList<>(n+n);
        ArrayList<Integer> acceptableCandidate = new ArrayList<>(n);
        //adds all the current values placed at the current row
        for (int column_index =0; column_index<column; column_index++){
                existingInteger.add(currentSquare[row][column_index]);
        }
        //adds the current values placed at the current column
        for (int row_index =0; row_index<row; row_index++){
            existingInteger.add(currentSquare[row_index][column]);
        }
        java.util.Collections.sort(existingInteger);// sorts the existing values list
        //removes duplicates in that list
        for (int i = 1; i< existingInteger.size(); i++){
            if ((existingInteger.get(i-1) ==existingInteger.get(i)) ){
                existingInteger.remove(i);
                i--;
            }
        }
        //initialize the acceptable candidates list
        for (int i = 1; i<=n; i++){
            acceptableCandidate.add(i);
        }
        //removes the candidates in the acceptable candidates list that also exist in the existing Integer list
        for (int i = existingInteger.size()-1; i>=0; i--){
            acceptableCandidate.remove(existingInteger.get(i)-1);
        }

        return acceptableCandidate;// returns the acceptable candidates
    }
    //method to check if the candidate can be placed at the current cell in the latin square
    public static int checkCandidate(int row, int column, int candidate, int [][] currentSquare){

        for (int column_index =0; column_index<column; column_index++){
            if (currentSquare[row][column_index]==candidate){
                return 0;
            }
        }
        //checks column if candidate already exists
        for (int row_index =0; row_index<row; row_index++){
            if (currentSquare[row_index][column]==candidate){
                return 0;
            }
        }

        return candidate;
    }
    //backtrack method
    public static void backTrack(int row, int column, int [][] latinSquare){

        if (fillCount==(n-1)*(n-1)){// if the square is filled initialize the d array
             d = new int [n-1][n-1];
        }
        else{
            //arraylist containing the viable candidates of the current cell
            ArrayList<Integer> viableCandidates = checkCandidateD(row, column, latinSquare);
            //if there are available viable candidates run the following
            if (!viableCandidates.isEmpty()){
                //loop to randomly select a viable candidate
                while(!viableCandidates.isEmpty()){
                    //grabs a random viable candidate from the list
                    int grabCandidate = (int)(random.nextDouble()*viableCandidates.size());
                    int num = viableCandidates.remove(grabCandidate);

                    //checks to see if it can be placed in the current cell
                    latinSquare[row][column] = checkCandidate(row, column, num, latinSquare);
                    //if the candidate was able to be placed in the current cell, move to the next cell and run the
                    // backtrack algorithm
                    if (latinSquare[row][column] != 0){
                        fillCount++;
                        row = ((fillCount/(n-1))+1);
                        column = ((fillCount%(n-1))+1);
                        //only runs if the d array is not null
                        if (d == null) backTrack(row, column, latinSquare);
                        fillCount--;
                        row=((fillCount/(n-1))+1);
                        column=((fillCount%(n-1))+1);
                        //resets the latinSquare to wht was
                        latinSquare[row][column] = 0;
                        //get the number of viable candidates to place the number of viable candidates at that cell
                        ArrayList<Integer> viableCandidates2 = checkCandidateD(row, column, latinSquare);
                        if (d != null) d[row-1][column-1]= viableCandidates2.size();
                    }
                }
            }
            else{//if it hit a dead end, the d array is initialized
                d = new int[n-1][n-1];
            }


        }
    }
    //method to make a copy of the d int 2d-Array
    public static int [][] squareCopy(int [][] squareToCopy){
        int [][] duplicateSquare = new int[squareToCopy.length][];
        for(int j = 0; j < squareToCopy.length; j++){
            duplicateSquare[j] = squareToCopy[j].clone();
        }
        return duplicateSquare;
    }
    //method to make a copy of the estimate BigInteger 2d-Array
    public static BigInteger [][] squareCopyBI(BigInteger [][] squareToCopy){
        BigInteger [][] duplicateSquare = new BigInteger[squareToCopy.length][];
        for(int j = 0; j < squareToCopy.length; j++){
            duplicateSquare[j] = squareToCopy[j].clone();
        }
        return duplicateSquare;
    }
}
