import java.lang.*;
import java.util.*;

public class A3 {
    private static int popSize = 30;
    private static int maxGen = 1000;
    private static double inertia = 1.0;
    private static double c1 = 2.0;
    private static double c2 = 2.0;
    private static particle[] population;
    private static int generation;

    /**
     * This method reads in the input from the user and uses it for the PSO algorithm.
     */
    private static void readParameters(){
        Scanner scn = new Scanner(System.in);
        System.out.println("Please enter the Population size: ");
        popSize = scn.nextInt();
        System.out.println("Please enter the Maximum generations: ");
        maxGen = scn.nextInt();
        System.out.println("Please enter the inertia value: ");
        inertia = scn.nextDouble();
        System.out.println("Please enter the c1 value: ");
        c1 = scn.nextDouble();
        System.out.println("Please enter the c2 value: ");
        c2 = scn.nextDouble();
    }

    /**
     * A helper method that copies the fitness value and positions of a particle to another particle
     * @param best - the particle that retrieves the new values
     * @param pop - the particle that gives the new values
     */
    private static void swapData(particle best, particle pop){
        best.fitnessValue = pop.fitnessValue;
        for(int i=0; i<best.position.length; i++){
            best.position[i] = pop.position[i];
        }
    }

    /**
     * This method represents the vanilla PSO algorithm by generating particles and updating the global best,
     * velocity and positions.
     * @param rnd - The random object with a seed to get the same random positions.
     * @return - the global best particle.
     */
    private static particle pso(Random rnd){
        generation = 1;
        population = new particle[popSize];
        for(int i=0; i<population.length; i++){
            population[i] = new particle();
            population[i].initialize(rnd);
        }
        particle gBest = population[0];
        while(generation<=maxGen) {
            System.out.println("Generation: " +generation);
            for(int i=0; i<population.length; i++){
                double oldFitness = population[i].pBestFitness;
                population[i].fitnessValue = population[i].fitnessFunction(population[i]);
                if(Math.abs(oldFitness)<Math.abs(population[i].fitnessValue)){
                    population[i].pBestPosition = population[i].position;
                }
            }

            for(int i=0; i<population.length; i++){
                if(population[i].fitnessValue<gBest.fitnessValue){
                    swapData(gBest,population[i]);
                }
            }
            printOut(gBest);
            for(int i=0; i<population.length;i++){
                population[i].updatePosVel(inertia, c1, c2, rnd, gBest.position);
            }
            generation++;
        }
        return gBest;
    }

    /**
     * This method represents the random search by generating particles with random positions and prints out the
     * global best in each generation/iteration.
     * @param rnd - random object with the seed to get the same random positions
     */
    private static void randomSearch(Random rnd){
        generation = 1;
        particle gBest = new particle();
        gBest.fitnessValue = Double.MAX_VALUE;
        double[] ranPos = new double[gBest.position.length];

        while(generation<=maxGen){
            for(int i=0; i<ranPos.length; i++){
                ranPos[i] = rnd.nextDouble()*5.12*2 - 5.12;       // put this in the range of [-5.12, 5.12]
            }

            double ranFitness = 10*ranPos.length;
            for(int i=0; i<ranPos.length; i++){
                ranFitness+= Math.pow(ranPos[i],2) - 10*Math.cos(2*Math.PI*ranPos[i]);
            }

            if(ranFitness<gBest.fitnessValue){
                gBest.position = ranPos;
                gBest.fitnessValue = ranFitness;
            }
            System.out.println("Generation: " +generation);
            printOut(gBest);
            generation++;
        }
    }

    /**
     * This method prints out the fitness value and positions of the global best particle in each generation.
     * @param p - the global best particle
     */
    private static void printOut(particle p){
        System.out.print("Global best Value: ");
        System.out.println(p.fitnessValue);
        System.out.print("Global best position: ");
        for(int i=0; i<p.position.length; i++){
            System.out.print(p.position[i] +" ");
        }
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        System.out.println("Dimension is 30, the range is [-5.12, 5.12]");
        System.out.println("Type 1 to use pso, 2 to use random search: ");
        generation = 1;
        Random rnd = new Random(1);
        if(scan.nextInt()==1){
            readParameters();
            pso(rnd);
        }else{
            randomSearch(rnd);
        }
    }
}
