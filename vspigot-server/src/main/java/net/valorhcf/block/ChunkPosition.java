package net.valorhcf.block;

public class ChunkPosition
{
    private final int x;
    private final int z;
    
    public ChunkPosition(final int x, final int z) {
        this.x = x;
        this.z = z;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getZ() {
        return this.z;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ChunkPosition that = (ChunkPosition)o;
        return this.x == that.x && this.z == that.z;
    }
    
    @Override
    public int hashCode() {
        int result = this.x;
        result = 31 * result + this.z;
        return result;
    }
}
