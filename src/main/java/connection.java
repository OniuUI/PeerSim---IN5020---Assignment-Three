package main.java;

import peersim.Simulator;

public class connection {


    public static void main(String[] args) {

        Simulator sim = new Simulator();

        String[] sim_args = {"src/main/java/gossip/scripts/ShuffleExample.txt"};

        sim.main(sim_args);
    }
}

