package com.wasteland;

import java.util.List;
import java.util.Map;

/**
 * Data structure for DCSS room JSON files
 */
public class RoomData {
    public String name;
    public String version;
    public String description;
    public int width;
    public int depth;
    public int height;
    public List<String> map;
    public Map<String, TileType> legend;
    public List<Feature> features;
    public SpawnPoint spawn_point;

    public static class TileType {
        public String type;
        public String block;
        public String description;
    }

    public static class Feature {
        public String type;
        public int x;
        public int y;
        public int z;
        public String block;
        public String monster_tier;  // DCSS monster tier (1-9) for monster_spawn features
        public String portal_type;   // Portal type for portal features
    }

    public static class SpawnPoint {
        public int x;
        public int y;
        public int z;
    }
}
