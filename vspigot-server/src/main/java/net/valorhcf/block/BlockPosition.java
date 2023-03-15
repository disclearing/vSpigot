package net.valorhcf.block;

import org.bukkit.block.Block;

public class BlockPosition {
    public final int x;
    public final int y;    
    public final int z;

    public BlockPosition fromBlock(Block block) {
        return new BlockPosition(block.getX(), block.getY(), block.getZ());
    }

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlockPosition that = (BlockPosition)o;
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        return this.z == that.z;
    }

    public int hashCode() {
        int result = this.x;
        result = 31 * result + this.y;
        result = 31 * result + this.z;
        return result;
    }
}
 