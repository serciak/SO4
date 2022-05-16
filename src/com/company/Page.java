package com.company;

public class Page {
    private int id;
    private int pid;
    private int lastUsed;

    public Page(int id, int pid, int lastUsed) {
        this.id = id;
        this.pid = pid;
        this.lastUsed = lastUsed;
    }

    public int getId() {
        return id;
    }

    public int getPid() {
        return pid;
    }

    public int getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(int lastUsed) {
        this.lastUsed = lastUsed;
    }
}
