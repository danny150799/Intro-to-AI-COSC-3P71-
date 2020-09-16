import java.util.*;
import java.lang.*;
import static java.lang.Math.abs;

/**
 * Danny Nguyen, dn17hg, 6334502
 * I used IntelliJ
 */
public class Cryptanalysis {

    //This is a very simple fitness function based on the expected frequency of each letter in english
    //There is lots of room for improvement in this function.
    private static double fitness(String k, String c) {
        //The expected frequency of each character in english language text according to
        //http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/english-letter-frequencies/
        double[] expectedFrequencies = new double[26];
        expectedFrequencies[0] = 0.085; //Expected frequency of a
        expectedFrequencies[1] = 0.016; //Expected frequency of b
        expectedFrequencies[2] = 0.0316; //Expected frequency of c
        expectedFrequencies[3] = 0.0387; //Expected frequency of d
        expectedFrequencies[4] = 0.121; //Expected frequency of e
        expectedFrequencies[5] = 0.0218; //Expected frequency of f
        expectedFrequencies[6] = 0.0209; //Expected frequency of g
        expectedFrequencies[7] = 0.0496; //Expected frequency of h
        expectedFrequencies[8] = 0.0733; //Expected frequency of i
        expectedFrequencies[9] = 0.0022; //Expected frequency of j
        expectedFrequencies[10] = 0.0081; //Expected frequency of k
        expectedFrequencies[11] = 0.0421; //Expected frequency of l
        expectedFrequencies[12] = 0.0253; //Expected frequency of m
        expectedFrequencies[13] = 0.0717; //Expected frequency of n
        expectedFrequencies[14] = 0.0747; //Expected frequency of o
        expectedFrequencies[15] = 0.0207; //Expected frequency of p
        expectedFrequencies[16] = 0.001; //Expected frequency of q
        expectedFrequencies[17] = 0.0633; //Expected frequency of r
        expectedFrequencies[18] = 0.0673; //Expected frequency of s
        expectedFrequencies[19] = 0.0894;//Expected frequency of t
        expectedFrequencies[20] = 0.0268;//Expected frequency of u
        expectedFrequencies[21] = 0.0106; //Expected frequency of v
        expectedFrequencies[22] = 0.0183;//Expected frequency of w
        expectedFrequencies[23] = 0.0019;//Expected frequency of x
        expectedFrequencies[24] = 0.0172;//Expected frequency of y
        expectedFrequencies[25] = 0.0011;//Expected frequency of z

        //Sanitize the cipher text and key
        String d = c.toLowerCase();
        d = d.replaceAll("[^a-z]", "");
        d = d.replaceAll("\\s", "");
        int[] cipher = new int[c.length()];
        for(int x = 0; x < c.length(); x++) {
            cipher[x] = ((int)d.charAt(x))-97;
        }

        String ke = k.toLowerCase();
        ke = ke.replaceAll("[^a-z]", "");
        ke = ke.replaceAll("\\s", "");

        char[] key = ke.toCharArray();
        for(int i = 0; i < key.length; i++) key[i] = (char)(key[i]-97);


        int[] charCounts = new int[26];
        for(int i = 0; i < charCounts.length; i++) charCounts[i] = 0;

        int[] plain = new int[cipher.length];

        //Decrypt each character
        int keyPtr = 0;
        for(int i = 0; i < cipher.length; i++) {
            char keyChar = (char)0;
            if(key.length > 0) {
                //Ignore any value not in the expected range
                while(key[keyPtr] >25 || key[keyPtr] < 0) {
                    keyPtr = (keyPtr + 1)%key.length;
                }
                keyChar = key[keyPtr];
                keyPtr = (keyPtr + 1)%key.length;
            }
            plain[i] = ((26 + cipher[i] - keyChar)%26);

        }

        //Count the occurences of each character
        for(int x : plain) {
            charCounts[x]++;
        }
        //Calculate the total difference between the expected frequencies and the actual frequencies
        double score = 0;
        for(int y =0; y < charCounts.length; y++) {
            score += abs((((float)charCounts[y])/plain.length)-expectedFrequencies[y]);
        }

        return score;
    }

    /**
     * This methods represents tournament selection. This method randomly picks a certain amount of chromosomes depends
     * on the given input, then it chooses the best out of the chosen chromosomes and inserts the fittest into the next
     * generation.
     * @param gen1 - th population
     * @param amount - the amount of chromosomes
     * @param code - the string to compute the fitness function
     * @param rundomS - the seed
     * @return - a new generation made from tournament selection
     */
    private static char[][] tSelection(char[][] gen1, int amount, String code, Random rundomS){
        char [][] gen2 = new char[gen1.length][gen1[0].length];
        char [][] temp = new char[amount][gen1[0].length];
        char[] min;

        for(int a=0; a<gen1.length; a++) {
            for (int i = 0; i < amount; i++) {
                int ran = rundomS.nextInt(gen1.length-1);

                temp[i] = gen1[ran];
            }
            min = temp[0];
            for (int i = 1; i < temp.length; i++) {
                if (fitness(String.valueOf(temp[i]), code) < fitness(String.valueOf(min), code)) {
                    min = temp[i];
                }
            }
            gen2[a] = min;
        }
        return gen2;
    }

    /**
     * This method represents uniform crossover. This method chooses that % of population that is not going to crossover
     * randomly, then continue to perform uniform crossover.
     * @param gen1 - the population
     * @param r - the crossover rate
     * @param rundomS - the seed
     * @return - the new generation created from the crossover of gen1
     */
    private static char[][] uniformC(char[][] gen1, double r, Random rundomS){
        char[][] gen2 = new char[gen1.length][gen1[0].length];
        char[] temp = new char[gen1[0].length];
        char[] temp2 = new char[gen1[0].length];

        for(int i=0; i<gen1.length-(int)(gen1.length*r);i++){
            gen2[i] = gen1[rundomS.nextInt(gen1.length-1)];
        }

        for(int i=gen1.length-(int)(gen1.length*r); i<gen1.length; i+=2){
            if((int)(gen1.length*r)%2==1 && i==gen1.length-1){
                for(int j=0; j<gen1[0].length; j++) {
                    double chance = rundomS.nextInt(1);
                    if (chance<0.5) {
                        temp[j] = gen1[i-1][j];
                        temp2[j] = gen1[i][j];
                    }else{
                        temp[j] = gen1[i][j];
                        temp2[j] = gen1[i-1][j];
                    }
                }
                gen2[i-1] = temp;
                gen2[i] = temp2;
                temp = new char[gen1[0].length];
                temp2 = new char[gen1[0].length];
            }else {
                for (int j = 0; j < gen1[0].length; j++) {
                    double chance = rundomS.nextDouble();
                    if (chance < 0.5) {
                        temp[j] = gen1[i][j];
                        temp2[j] = gen1[i + 1][j];
                    } else {
                        temp[j] = gen1[i + 1][j];
                        temp2[j] = gen1[i][j];
                    }
                }
                gen2[i] = temp;
                gen2[i + 1] = temp2;
                temp = new char[gen1[0].length];
                temp2 = new char[gen1[0].length];
            }
        }
        return gen2;
    }

    /**
     * This method represents the one-point crossover in genetic algorithm. This method will choose the % of population
     * that is not going to crossover, then continue to crossover.
     * @param gen1 - the population
     * @param r - crossover rate
     * @param rundomS - the seed
     * @return - a new population created from one-point crossover of the gen1
     */
    private static char[][] onePointC(char[][] gen1, double r, Random rundomS){
        char[][] gen2 = new char[gen1.length][gen1[0].length];
        char[] temp = new char[gen1[0].length];
        char[] temp2 = new char[gen1[0].length];

        for(int i=0; i<gen1.length-(int)(gen1.length*r);i++){
            gen2[i] = gen1[rundomS.nextInt(gen1.length-1)];
        }

        for(int i=gen1.length-(int)(gen1.length*r); i<gen1.length; i+=2){
            int chance = rundomS.nextInt(gen1[0].length-1 + 0); //the point
            if((int)(gen1.length*r)%2==1 && i==gen1.length-1){
                for(int j=0; j<gen1[0].length; j++) {
                    if (j<=chance-1) {
                        temp[j] = gen1[i-1][j];
                        temp2[j] = gen1[i][j];
                    }else{
                        temp[j] = gen1[i][j];
                        temp2[j] = gen1[i-1][j];
                    }
                }
                gen2[i-1] = temp;
                gen2[i] = temp2;
                temp = new char[gen1[0].length];
                temp2 = new char[gen1[0].length];
            }else {
                for (int j = 0; j < gen1[0].length; j++) {
                    if (j <= chance - 1) {
                        temp[j] = gen1[i][j];
                        temp2[j] = gen1[i + 1][j];
                    } else {
                        temp[j] = gen1[i + 1][j];
                        temp2[j] = gen1[i][j];
                    }
                }
                gen2[i] = temp;
                gen2[i + 1] = temp2;
                temp = new char[gen1[0].length];
                temp2 = new char[gen1[0].length];
            }
        }
        return gen2;
    }

    /**
     * This method represents inversion mutation. It chooses a random size for the sub-array and the random position
     * in side the chromosome. The sub-array starts at the chosen random position. Every characters in the sub-array is
     * rearranged backward, which is how inversion mutation works.
     * @param gen1 - the 2d char array that contains the population.
     * @param mRate - the mutation rate
     * @param rundomS  - the seed
     * @return - 2d array that had been mutated
     */
    private static char[][] mutation(char[][] gen1, double mRate, Random rundomS) {
        char[][] gen2 = new char[gen1.length][gen1[0].length];
        for (int i = 0; i < gen1.length - (int) (gen1.length * mRate); i++) {   //the % that did not mutate
            gen2[i] = gen1[rundomS.nextInt(gen1.length-1)];
        }

        for (int i = gen1.length - (int) (gen1.length * mRate) ; i < gen1.length; i++){
            int rand = rundomS.nextInt(gen1[0].length);                          //randomly pick the size of the sub array
            int pos = (rundomS.nextInt(gen1[0].length  - rand));                //randomly pick the position of the sub array
            char[] subarray = new char[rand];
            char[] temp = new char[rand];
            int pos2 = 0;

            for(int j=pos; j<rand+pos;j++){
                subarray[pos2] = gen1[i][j];
                pos2++;
            }
            pos2=subarray.length-1;

            for(int j=0; j<subarray.length; j++){
                temp[pos2] = subarray[j];
                pos2--;
            }
            pos2=0;
            gen2[i] = gen1[i];
            for(int j=pos; j<rand+pos;j++){
                gen2[i][j] = temp[pos2];
                pos2++;
            }
        }
        return gen2;
    }

    /**
     * This method prints out the fittest chromosome, computes the average population and prints the average as well.
     * @param gen1 - the population
     * @param code - the string inorder to evaluate the population
     */
    private static void printBestAvg(char[][] gen1, String code){
        char[] best = gen1[0];
        for(int a=0; a<gen1.length; a++) {
            if (fitness(String.valueOf(gen1[a]), code) < fitness(String.valueOf(best), code)) {
                best = gen1[a];
            }
        }
        System.out.println("Best fitness value of this generation: " + fitness(String.valueOf(best),code));
        double avg = 0.0;
        for(int i=0; i<gen1.length; i++){
            avg+=fitness(String.valueOf(gen1[i]),code);
        }
        avg /= gen1.length;
        System.out.println("Average population fitness value of this generation: " + avg);
        System.out.println(best);
    }

    private static int maxgen = 100;

    /**
     * This program represents a simple genetic algorithm by finding an "optimal key" that would decrypt a string
     * to produce a decent decrypted phrase.
     * @param args
     */
    public static void main(String[] args){
        int kSize = 40;                                         // the key size
        int popSize = 5000;                                     // the population size
        String chars = "abcdefghijklmnopqrstuvwsyz-";           // the alphabet + "-" to create the initial population
        int k = 2;                                              // k value used for tournament selection
        Random rnd = new Random(5);                       // random number with the seed
        char[] char1 = new char[kSize];
        char[][] gen = new char[popSize][popSize];
        String c = "lbtqrtttisjskmxbgaixizptcftdhglhbwalsijeeybbztnixirbviwrqblpbbhjmwlesnwidcttkfclkicvagokwbkqdpvwzan" +
                "olafymgvuszntlryiyllhpczbrircqhrqchnzwcgtigplzfkiuvdeampcabatntokdgztyuloceekmtbdyajwfzagavvrbmneasstu" +
                "wnlwxxxngmtomkhgdpawxvvlbvitsmuwpohlgmvaiwcrmihbitbsmfbvgxbtvtskhbvcfsewhambgsnpnrpgzptdbecxzwmdephfgl" +
                "dfsfyimkkszlisyzppjqxbjequwrnwxbvtsmkuycxltiparrryplatxmpxetatlzrtyifvmlzpmcgdewnetkzazwmbjicaccecdhkvu" +
                "uhhypvrpcpatwtnmxijdqpkpipejuddrmrmgoyaprnlepfktoupbzxucvqxinduxgvpopwtytrxgteqsxrkiogvnzkrdipezxscuqhc" +
                "gfiuizihemjenovpbqywwvxvzelbowiphqskmtieqnepjzlrcxqftbghmpztznwvglwmcxcgwkctepjciiszjkxzxeqdzyephbdgdyj" +
                "jiimeqfyqhvatlepwgjasqwmrzjvstdslkwhvpzuhcmfuexasmsklqjfinicawwpbvyakmjifhnlbziejiemvtciypiqaxqqqnqbyvl" +
                "iilzpkepfktnqdjdthgqxnpagmesgvhbwuuhxzpgznyyencrmynvkrqwmvlawdkbgofcccxfvhpqwglgvpbxkwoaexkhephwtavilkqt" +
                "vvhicmirtaaamuntkeobirvqquuigswlociorllqsvdcmcmkxmprbpztsmvwvmczlzuislvbcmfbdaztvympgrbmbthwrdrwgclaicwk" +
                "jedbtimhccalnxqrrhaiighotaoagfilejoacafgpxwlkzxlqtmdaieqrbnijyddydjacvlajktnmhqjxaqjqwmadbucpwacusftbtj" +
                "ayojgarxtbsmqpktxbhephooincfyccxvnltojeckwqiznogsrijrpinchqbwsfxtwtgneofjuvwybzxxnektbiepdrqkqojjysxfyacl" +
                "xdijvtozmwhxetbwptihjibxlzyhtvetcwxtovmewoaqeletpaoiwcpkslwkigxvfiylntazmoietauscutaxqquiigwzayuppjyozt" +
                "xetuzdagoymqwinpvrfowimnwfdgzvyewbrrjaepalmcvqwbhtamsvwtzajyweudenwrvitdtaautgeydctlyxotbslhsmixnglgmmv" +
                "cuuaijxlkxqdicztrguizjmxzdjwnaxmxldjmytqtvfzfdteybomuyicjlysslvoqbmvpriymltahpxbqnrodggafokzysslvoqilln" +
                "gatvyntcvinipazrdtqonwhbgejgiexwfvkljmlmpgrbmbdlgwvgzsqskhdxyknrwkkhoatvlamremtzspffsrbofalnaieqtpqskh" +
                "kllqdrbgpbvzaapdbfbvyoglahngneqszgtwcifvmqjlcmoqbksizopwknseeiecayyazmgmjmptiximnplwvgpigsflpgvkmtomknu" +
                "bsinxpgeoswfephstcdnaghpxrnlsiiznubxmlhokpsnbhpehznsbiofuhxiqnzujiazwebwkajetwmwlalaombmwdstbtktplfktnm" +
                "ymoliphfcbhpmaqgagixzchjvgltvljitdtbwwugymiwtlshovcfhoanwlzotsiyeimpeqftaevriqnjwihjmfyvhfprvviyauztkwqi" +
                "debjeqwissisdgvsxkahrizutttqiesmxjwkbjeqkqgttystgrcklccgknyepjslgkvifwakpbcbomahfxihijqnwijjaowbvdriybwkv" +
                "vlodeiyodtgmpfwyfdalroybmvfrwzzagbjizdznpzwvgahysvsimtmiyotwtnmntgvsysozwfephhgtsmugjtxygltbyceyttbagbjio" +
                "dwflvrpnwbahjiuyefiegbztnbsmkmithrhbsezhommruujihwzvorqqmyswgmvtjqyqxvvtalpnmpolsosmsnewwtbitoepjhcilq" +
                "wmtpthgewdygfyhencctzhceunomwijnybpvdephzkbhfwjijrurllvjkscqxuagokrqwmftmorkbgyweyswlehltnktrmepagousygq" +
                "gsbdbfaaudduchjviwtkritbwgetzmialqtsbuopajyjkyhxikppafedyttozmtajipbtpvhrhzcglzyeiihenbwfutlmcllwnmqitet" +
                "bzouacmadptvpyacufgitasmswwhpfvpttbzouigcxanfyzxecmisuzzpidegvlfheadbksvmzykuieimkbciyznmetbzmpgeziqvtbbc" +
                "hbvyudironqrvbmrtqmablamrpxcmttvywgeomaouigygdepjglgvpbkxmoiaiwgcwzzczuyjshswdclwmwrnjbzivoipgbpvdcmfsfmp" +
                "ollbpxncsdqrglebsilfggcblisequsf";

        double crossRate = 1.0;                             // the crossover rate
        double mutationRate = 0.2;                          // the mutation crate
        int generation = 1;

        // create the initial population randomly using Random with the seed number
        for(int j=0; j<popSize; j++) {
            for (int i = 0; i < kSize; i++) {
                char1[i] = chars.charAt(rnd.nextInt(chars.length()));
            }
            gen[j] = char1;
            char1 = new char[kSize];

        }
        // perform the genetic algorithm
        while(generation<=maxgen) {
            System.out.println("Generation: " + generation);
            System.out.println("Population size: " + popSize);
            System.out.println("Maximum generations: " + maxgen);
            System.out.println("Probability of crossover: " + (int)(crossRate*100) + "%");
            System.out.println("Probability of mutation: " + (int)(mutationRate*100) + "%");
            printBestAvg(gen,c);
            System.out.println();
            gen = tSelection(gen, k, c,rnd);
            gen = uniformC(gen, crossRate,rnd);
            gen = mutation(gen,mutationRate,rnd);
            generation++;
        }
    }
}