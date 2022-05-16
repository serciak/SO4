package com.company;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Algorithms {

    private int zoneCoef;
    private ArrayList<Page> req;
    private ArrayList<Process> pro;
    private int reqAmount;
    private int proAmount;
    private int framesAmount;
    private int localMax;
    private double localProb;
    private int maxSubsetSize;
    private int pgAmount;
    private ArrayList<Page> frames;
    private double ppf;

    public Algorithms(int reqAmount, int proAmount, int pgAmount, int maxSubsetSize, double localProb, int localMax, int framesAmount, int zoneCoef, double ppf) {
        this.zoneCoef = zoneCoef;
        this.localMax = localMax;
        this.localProb = localProb;
        this.maxSubsetSize = maxSubsetSize;
        this.proAmount = proAmount;
        this.reqAmount = reqAmount;
        this.pgAmount = pgAmount;
        this.framesAmount = framesAmount;
        this.ppf = ppf;
        frames = new ArrayList<>();
        this.req = generatorReq();
        this.pro = generatorPro();
    }

    public int equal() {
        int errors = 0;
        ArrayList<Process> proCopy = (ArrayList<Process>) pro.clone();

        int framesSize = Math.floorDiv(framesAmount, proAmount);

        for(int i = 0; i < proAmount; i++) {
            errors += LRU(proCopy.get(i).getRequests(), framesSize);
        }
        return errors;
    }

    public int proportional() {
        int errors = 0;
        ArrayList<Process> proCopy = (ArrayList<Process>) pro.clone();

        for(int i = 0; i < proAmount; i++) {
            int frameSize = Math.floorDiv(proCopy.get(i).getRequests().size() * framesAmount, reqAmount);
            pro.get(i).setFramesNum(frameSize);
            System.out.println(pro.get(i));
            int e = LRU(proCopy.get(i).getRequests(), frameSize);
            errors += e;
        }

        return errors;
    }

    public int steeringErrorFreq() {
        int errorMax = (int) ppf*reqAmount;
        int frameSize = Math.floorDiv(framesAmount, pro.size());
        int freeFrames = 0;
        int allErrors = 0;
        int proSize = proAmount;
        ArrayList<Process> proCopy = (ArrayList<Process>) pro.clone();

        for(Process p : proCopy)
            p.setFramesNum(frameSize);

        while(proSize != 0) {
            int min = pgAmount;
            int max = 0;
            int bestIdx = 0;
            int worstIdx = 0;

            for(int i = 0; i < proCopy.size(); i++) {
                Process curr = proCopy.get(i);

                if(curr != null && curr.getRequests().size() > 0) {
                    if(proSize == 1) {
                        proCopy.get(i).setFramesNum(proCopy.get(i).getFramesNum()+freeFrames);
                        freeFrames = 0;
                    }
                    int errorsCoef = curr.getErrors();
                    int e = curr.LRU(frameSize);
                    allErrors += e;

                    if(errorsCoef > max) {
                        max = errorsCoef;
                        worstIdx = i;
                    }
                    if(errorsCoef < min) {
                        min = errorsCoef;
                        bestIdx = i;
                    }

                    curr.removeRequest(0);
                } else {
                    if(curr != null) {
                        if(proCopy.get(worstIdx) != null && worstIdx != i)
                            proCopy.get(worstIdx).setFramesNum(proCopy.get(worstIdx).getFramesNum()+proCopy.get(i).getFramesNum());
                        else
                            freeFrames += proCopy.get(i).getFramesNum();
                    }
                    proCopy.set(i, null);
                    --proSize;
                }
            }
            if(proCopy.get(bestIdx) != null && proCopy.get(worstIdx) != null && proCopy.get(bestIdx).getFid() != 1 && max > errorMax) {
                if(proCopy.get(bestIdx).getFramesNum() > 3) {
                    proCopy.get(bestIdx).setFramesNum(proCopy.get(bestIdx).getFramesNum()-1);
                    proCopy.get(worstIdx).setFramesNum(proCopy.get(worstIdx).getFramesNum()+freeFrames+1);
                    freeFrames = 0;
                }
            }
        }

        return allErrors;
    }

    public int localityModel() {
        int allErrors = 0;
        int freeFrames = framesAmount;
        int finished = -1;

        for(int i = 0; i < pro.size(); i++) {
            int wSet = Math.floorDiv(pro.get(i).getRequests().size() * framesAmount, reqAmount);
            pro.get(i).setFramesNum(wSet);
        }
        if(finished != proAmount - 1) {
            for(int i = 0; i < pro.size(); i++) {
                if(freeFrames > pro.get(i).getFramesNum()) {
                    finished++;
                    freeFrames -= pro.get(i).getFramesNum();

                    if(pro.get(i).getFramesNum() != 0) {
                        allErrors += LRU(pro.get(i).getRequests(), pro.get(i).getFramesNum());
                    }
                }
            }
            freeFrames = framesAmount;
        }

        while(finished != proAmount - 1) {
            for(Process p : pro)
                p.setFramesNum(newFramesNum(p.getRequests()));

            for(int i = finished+1; i < pro.size(); i++) {
                if(freeFrames > pro.get(i).getFramesNum()) {
                    finished++;
                    freeFrames -= pro.get(i).getFramesNum();
                    if(freeFrames < 0) {
                        freeFrames += pro.get(i).getFramesNum();
                        continue;
                    }
                    if(pro.get(i).getFramesNum() != 0)
                        allErrors += LRU(pro.get(i).getRequests(), pro.get(i).getFramesNum());
                }
            }
            freeFrames = framesAmount;
        }
        return allErrors;
    }

    public int LRU(ArrayList<Page> pages, int frameSize) {
        int errors = 0;
        boolean done;

        for(Page temp : pages) {
            done = false;
            if(frames.size() < frameSize) {
                for(Page frame : frames) {
                    if(frame.getId() == temp.getId()) {
                        frame.setLastUsed(frame.getLastUsed() + 1);
                        done = true;
                        break;
                    }
                }
                if(!done) {
                    errors++;
                    frames.add(temp);
                }
            } else {
                for(Page frame : frames) {
                    if(frame.getId() == temp.getId()) {
                        frame.setLastUsed(frame.getLastUsed() + 1);
                        done = true;
                        break;
                    }
                }
                if(!done) {
                    frames.sort(Comparator.comparingInt(Page::getLastUsed));
                    frames.add(temp);
                    errors++;
                }
            }
        }

        frames = new ArrayList<>();

        return errors;
    }

    private int newFramesNum(ArrayList<Page> r) {
        if(zoneCoef > r.size()) return r.size();
        return zoneCoef;
    }

    public ArrayList<Page> generatorReq() {
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

    public ArrayList<Process> generatorPro() {
        ArrayList<Process> processes = new ArrayList<>();

        for(int i = 0; i < proAmount; i++) {
            processes.add(new Process(new ArrayList<>(), 0));
            for(Page r : req) {
                if(r.getPid() == i) {
                    processes.get(i).addRequest(r);
                }
            }
        }
        frames = new ArrayList<>();
        return processes;
    }
}
