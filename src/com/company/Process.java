package com.company;

import java.util.ArrayList;
import java.util.Comparator;

public class Process {
    private int fid;
    private int errors;
    private int framesNum;
    private ArrayList<Page> frames;
    private ArrayList<Page> requests;

    public Process(ArrayList<Page> requests, int fid) {
        this.requests = requests;
        this.fid = fid;
        errors = 0;
        framesNum = 0;
        frames = new ArrayList<>();
    }

    public String toString() {
        return "ilosc ramek: " + framesNum + "\tilosc stron: " + requests.size();
    }

    public void addRequest(Page req) {
        requests.add(req);
    }

    public void removeRequest(int i) { requests.remove(i); }

    public int getFid() {
        return fid;
    }

    public int getErrors() {
        return errors;
    }

    public int getFramesNum() {
        return framesNum;
    }

    public ArrayList<Page> getFrames() {
        return frames;
    }

    public ArrayList<Page> getRequests() {
        return requests;
    }

    public void setFramesNum(int framesNum) {
        this.framesNum = framesNum;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public int LRU(int frameSize) {
        int errors = 0;
        Page temp = requests.get(0);
        boolean done = false;

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
                    frame.setLastUsed(frame.getLastUsed()+1);
                    done = true;
                    break;
                }
            }
            if(!done) {
                frames.sort(Comparator.comparingInt(Page::getLastUsed));
                frames.remove(0);
                frames.add(temp);
                errors++;
            }
        }
        this.errors = errors + this.errors;
        return errors;
    }
}
