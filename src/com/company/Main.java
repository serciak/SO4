package com.company;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static ArrayList<Page> generatorReq(int reqAmount, int proAmount, int pgAmount, int maxSubsetSize, double localProb, int localMax) {
        Random random = new Random();
        ArrayList<Page> requests = new ArrayList<>();
        int pid;

        for(int i = 0; i<reqAmount; i++) {
            if(random.nextDouble()<localProb) {
                pid = random.nextInt(0, proAmount);
                int subsetSize = random.nextInt(1, maxSubsetSize);
                ArrayList<Integer> subset = new ArrayList<>();

                for(int j = 0; j<subsetSize; j++) {
                    subset.add(random.nextInt(0, pgAmount));
                }
                for(int j = 0; j<localMax; j++) {
                    int toAdd = subset.get(random.nextInt(0, subsetSize));
                    if(j == reqAmount) return requests;
                    requests.add(new Page(toAdd, pid, 0));
                }
            }
        }
        return requests;
    }

    public static ArrayList<Process> generatorPro(int processesAmount, ArrayList<Page> req) {
        ArrayList<Process> processes = new ArrayList<>();

        for(int i = 0; i < processesAmount; i++) {
            processes.add(new Process(new ArrayList<>(), 0));
            for(Page r : req) {
                if(r.getPid() == i) {
                    processes.get(i).addRequest(r);
                }
            }
        }

        return processes;
    }

    public static void main(String[] args) {

        int framesAmount = 200;
        int processesAmount = 10;
        int pagesAmount = 250;
        int requestsAmount = 5000;
        int zoneCoef = 40;
        int maxSubsetSize = 10;
        int maxLocal = 100;
        double localProb = 0.01;
        double ppf = 0.7;

        Algorithms algorithms = new Algorithms(requestsAmount, processesAmount, pagesAmount, maxSubsetSize, localProb, maxLocal, framesAmount, zoneCoef, ppf);

        System.out.println("Przydzial rowny: " + algorithms.equal());
        System.out.println("Przydzial proporcjonalny: " + algorithms.proportional());
        System.out.println("Strategia sterowania czestotliwoscia bledu: " + algorithms.steeringErrorFreq());
        System.out.println("Model strefowy: " + algorithms.localityModel());
    }
}
