import java.util.Random;

public class particle {
    private int dimension = 30;

    public double[] position, velocity, pBestPosition;
    public double fitnessValue, pBestFitness;

    /**
     * The default particle that is created. Initialize the position, velocity, personal best position.
     */
    public particle(){
        position = new double[dimension];
        velocity = new double[dimension];
        pBestPosition = new double[dimension];
    }

    /**
     * This method initializes the particle by generating the positions in the range of [-5.12, 5.12] and set
     * velocities to 0.
     * @param rnd - the random object with the seed to get the same random positions
     */
    public void initialize(Random rnd){
        for(int i=0; i<dimension; i++){
            position[i] = rnd.nextDouble()*5.12*2 - 5.12;       // put this in the range of [-5.12, 5.12]
            velocity[i] = 0;
        }
    }

    /**
     * This method updates the velocities and positions of a particle based on inertia, cognitive, social value,
     * previous position, personal best position and global best position.
     * @param inertia - the inertia value
     * @param c1 - the cognitive value
     * @param c2 - the social value
     * @param rnd - the random object with the seed
     * @param bestGlobal - the position of the global best.
     */
    public void updatePosVel(double inertia, double c1, double c2, Random rnd, double[] bestGlobal){
        for(int i=0; i<velocity.length; i++){// calculate the new velocity
            velocity[i] = (inertia*velocity[i]) + c1*rnd.nextDouble()*(pBestPosition[i] - position[i]) +
                    c2*rnd.nextDouble()*(bestGlobal[i] - position[i]);
        }
        for (int i=0; i<position.length; i++){                  // calculate the new position
            position[i] = position[i] + velocity[i];
        }
    }

    /**
     * This method evaluate the positions of the particle and give a fitness value. Based on the Rastrigin function.
     * @param p - the particle that need to be evaluated
     * @return - a fitness value
     */
    public double fitnessFunction(particle p){
        double fitness = 10*dimension;
        for(int i=0; i<p.position.length; i++){
            fitness+= Math.pow(p.position[i],2) - 10*Math.cos(2*Math.PI*p.position[i]);
        }
        return fitness;
    }
}
