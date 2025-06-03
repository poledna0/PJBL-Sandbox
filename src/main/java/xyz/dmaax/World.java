package xyz.dmaax;

import java.util.ArrayList;

public class World {
    private ArrayList<Chunk> chunks = new ArrayList<>();

    public void addChunk(Chunk chunk) {
       chunks.add(chunk);
    }
}
