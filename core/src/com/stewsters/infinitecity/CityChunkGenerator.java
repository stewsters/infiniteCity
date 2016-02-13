package com.stewsters.infinitecity;

import com.stewsters.util.math.MatUtils;
import com.stewsters.util.math.geom.Rect;
import com.stewsters.util.math.geom.RectPrism;
import com.stewsters.util.math.geom.RectSubdivider;

import java.util.List;

/**
 * Adapted from <a href="http://devmag.org.za/2009/04/25/perlin-noise/">http://devmag.org.za/2009/04/25/perlin-noise/</a>
 *
 * @author badlogic
 */
public class CityChunkGenerator {


    public static final int groundHeight = 4;

    private int xSize;
    private int ySize;
    private int zSize;
    private byte[][][] tiles = null;

    public CityChunkGenerator(int xSize, int ySize, int zSize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.tiles = new byte[xSize][ySize][zSize];
    }


    public static void generateVoxels(VoxelWorld voxelWorld) {


        for (int xg = 0; xg < voxelWorld.chunksX; xg++) {
            for (int yg = 0; yg < voxelWorld.chunksY; yg++) {
                for (int zg = 0; zg < voxelWorld.chunksZ; zg++) {

                    CityChunkGenerator cityChunkGenerator = new CityChunkGenerator(voxelWorld.CHUNK_SIZE_X, voxelWorld.CHUNK_SIZE_Y, voxelWorld.CHUNK_SIZE_Z);

                    cityChunkGenerator.flattenWorld();
                    cityChunkGenerator.constructBuildings(8);

                    for (int x = 0; x < voxelWorld.CHUNK_SIZE_X; x++) {
                        for (int y = 0; y < voxelWorld.CHUNK_SIZE_Y; y++) {
                            for (int z = 0; z < voxelWorld.CHUNK_SIZE_Z; z++) {
                                voxelWorld.set(
                                        xg * VoxelWorld.CHUNK_SIZE_X + x,
                                        yg * VoxelWorld.CHUNK_SIZE_Y + y,
                                        zg * VoxelWorld.CHUNK_SIZE_Z + z,
                                        cityChunkGenerator.tiles[x][y][z]);
                            }
                        }
                    }

                }
            }
        }


    }

    private void flattenWorld() {
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                for (int z = 0; z < zSize; z++) {

                    tiles[x][y][z] = z <= groundHeight ? (byte) 1 : (byte) 0;
                }
            }
        }
    }


    public void constructBuildings(int minSize) {

        //check average ySize for this

        // make a lot representing the whole, except for the street
        Rect whole = new Rect(2, 2, xSize - 3, ySize - 3);


        List<Rect> lots = RectSubdivider.divide(whole, minSize);

        for (Rect lot : lots) {


            BuildingTypes buildingTypes = MatUtils.randVal(BuildingTypes.values());

            // ySize in floors
            int totalFloors = MatUtils.getIntInRange(1, 8);

            // This gives you the separation level around
            int extendedWalk = MatUtils.getIntInRange(0, 2);

            Rect foundation = new Rect(
                    lot.x1 + extendedWalk,
                    lot.y1 + extendedWalk,
                    lot.x2 - extendedWalk,
                    lot.y2 - extendedWalk);


            if (buildingTypes.cornerPillar) {

                int top = (totalFloors + 1) * 3;
                fillColumn(foundation.x1, foundation.y1, groundHeight, top);
                fillColumn(foundation.x2, foundation.y1, groundHeight, top);
                fillColumn(foundation.x1, foundation.y2, groundHeight, top);
                fillColumn(foundation.x2, foundation.y2, groundHeight, top);
            }

            for (int floor = 0; floor < totalFloors; floor++) {

                if (buildingTypes.wall != 0) {
                    wall(foundation, groundHeight + (floor * 3) + 1, buildingTypes.wall, (byte) 1);
                }
//                wall(foundation, groundHeight + (floor * 3) + 2, (byte) 1);
                solidLevel(foundation, groundHeight + (floor * 3) + 3, (byte) 1);
            }

        }

        // make foundations

    }

    private void fillColumn(int x, int y, int z, int height) {
        fillBlock(new RectPrism(x, y, z, x, y, height), (byte) 1);

    }


    private void fillBlock(RectPrism prism, byte tileType) {

        for (int x = prism.x1; x <= prism.x2; x++) {
            for (int y = prism.y1; y <= prism.y2; y++) {
                for (int z = prism.z1; z <= prism.z2; z++) {

                    tiles[x][y][z] = tileType;
                }
            }
        }
    }


    private void solidLevel(Rect prism, int z, byte tileType) {

        for (int x = prism.x1; x <= prism.x2; x++) {
            for (int y = prism.y1; y <= prism.y2; y++) {

                tiles[x][y][z] = tileType;
            }
        }
    }

    // Draws a wall around the area
    private void wall(Rect prism, int z, int wallHeight, byte tileType) {

        for (int x = prism.x1; x <= prism.x2; x++) {
            for (int y = prism.y1; y <= prism.y2; y++) {
                for (int zp = 0; zp < wallHeight; zp++) {

                    if ((x == prism.x1 || x == prism.x2) ||
                            (y == prism.y1 || y == prism.y2))
                        tiles[x][y][z + zp] = tileType;
                }
            }
        }
    }


}