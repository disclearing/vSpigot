package net.minecraft.server;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.player.PlayerPearlRefundEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.Gate;
import org.bukkit.material.Openable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.spigotmc.SpigotConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

// CraftBukkit start

public class EntityEnderPearl extends EntityProjectile {

    private Location lastValidTeleport;
    private Item toRefundPearl = null;
    private EntityLiving c;

    private static Set<Block> PROHIBITED_PEARL_BLOCKS = Sets.newHashSet(Block.getById(85), Block.getById(107));
    public static List<String> pearlAbleType = Arrays.asList("STEP", "STAIR");
    public static List<Material> forwardTypes = Collections.singletonList(Material.ENDER_PORTAL_FRAME);

    public EntityEnderPearl(World world) {
        super(world);
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
    }

    public EntityEnderPearl(World world, EntityLiving entityliving) {
        super(world, entityliving);
        this.c = entityliving;
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (SpigotConfig.pearlThroughGatesAndTripwire) {
            Block block = this.world.getType(movingobjectposition.b, movingobjectposition.c, movingobjectposition.d);

            if (block == Blocks.TRIPWIRE) {
                return;
            } else if (block == Blocks.FENCE_GATE) {
                BlockIterator bi = null;

                try {
                    Vector l = new Vector(this.locX, this.locY, this.locZ);
                    Vector l2 = new Vector(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
                    Vector dir = new Vector(l2.getX() - l.getX(), l2.getY() - l.getY(), l2.getZ() - l.getZ()).normalize();
                    bi = new BlockIterator(this.world.getWorld(), l, dir, 0, 1);
                } catch (IllegalStateException ex) {
                    // ignore
                }

                if (bi != null) {
                    boolean open = true;
                    boolean hasSolidBlock = false;

                    while (bi.hasNext()) {
                        org.bukkit.block.Block b = bi.next();

                        if (b.getType().isSolid() && b.getType().isOccluding()) {
                            hasSolidBlock = true;
                        }

                        if (b.getState().getData() instanceof Gate && !((Gate) b.getState().getData()).isOpen()) {
                            open = false;
                            break;
                        }
                    }

                    if (open && !hasSolidBlock) {
                        return;
                    }
                }
            }
        }


        // PaperSpigot start - Remove entities in unloaded chunks
        if (inUnloadedChunk && world.paperSpigotConfig.removeUnloadedEnderPearls) {
            die();
        }
        // PaperSpigot end

        for (int i = 0; i < 32; ++i) {
            this.world.addParticle("portal", this.locX, this.locY + this.random.nextDouble() * 2.0D, this.locZ, this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
        }

        if (!this.world.isStatic) {

            if (this.getShooter() != null && this.getShooter() instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) this.getShooter();

                if (entityplayer.playerConnection.b().isConnected() && entityplayer.world == this.world) { // MineHQ
                    // CraftBukkit start - Fire PlayerTeleportEvent

                    if (this.lastValidTeleport != null) {

                        org.bukkit.craftbukkit.entity.CraftPlayer player = entityplayer.getBukkitEntity();
                        org.bukkit.Location location = this.lastValidTeleport;
                        location.setPitch(player.getLocation().getPitch());
                        location.setYaw(player.getLocation().getYaw());

                        PlayerTeleportEvent teleEvent = new PlayerTeleportEvent(player, player.getLocation(), location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                        Bukkit.getPluginManager().callEvent(teleEvent);

                        if (!teleEvent.isCancelled() && !entityplayer.playerConnection.isDisconnected()) {
                            if (this.getShooter().am()) {
                                this.getShooter().mount(null);
                            }

                            entityplayer.playerConnection.teleport(teleEvent.getTo());
                            this.getShooter().fallDistance = 0.0F;
                            CraftEventFactory.entityDamage = this;
                            this.getShooter().damageEntity(DamageSource.FALL, 5.0F);
                            CraftEventFactory.entityDamage = null;
                        }
                        // CraftBukkit end
                    } else {
                        Bukkit.getPluginManager().callEvent(new PlayerPearlRefundEvent(entityplayer.getBukkitEntity()));
                    }
                }
            }

            this.die();
        }
    }

    @Override
    public void h() {
        EntityLiving shooter = this.getShooter();

        if (shooter != null && !shooter.isAlive()) {
            this.die();
        } else {
            AxisAlignedBB newBoundingBox = AxisAlignedBB.a(this.locX - 0.3D, this.locY - 0.05D, this.locZ - 0.3D, this.locX + 0.3D, this.locY + 0.5D, this.locZ + 0.3D);

            if (!this.world.boundingBoxContainsMaterials(this.boundingBox.grow(0.25D, 0D, 0.25D), PROHIBITED_PEARL_BLOCKS) && this.world.getCubes(this, newBoundingBox).isEmpty()) {
                this.lastValidTeleport = getBukkitEntity().getLocation();
            }
            org.bukkit.block.Block block = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
            Material typeHere = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)).getType();

            if (pearlAbleType.stream().anyMatch(it -> typeHere.name().contains(it))) {
                this.lastValidTeleport = getBukkitEntity().getLocation();
            }

            if (shooter != null && forwardTypes.stream().anyMatch(it -> block.getRelative(getDirection((EntityPlayer)shooter)).getType() == it)) {
                this.lastValidTeleport = getBukkitEntity().getLocation();
            }

            if (typeHere == Material.FENCE_GATE) {
                if (((Openable) block.getState().getData()).isOpen()) {
                    this.lastValidTeleport = getBukkitEntity().getLocation();
                }
            }

            if (shooter != null) {
                final org.bukkit.block.Block newTrap = block.getRelative(getDirection((EntityPlayer)shooter)).getRelative(BlockFace.DOWN);

                if (newTrap.getType() == Material.COBBLE_WALL || newTrap.getType() == Material.FENCE) {
                    this.lastValidTeleport = newTrap.getLocation();
                }
            }

            super.h();
        }
    }

    public static BlockFace getDirection(EntityPlayer entityPlayer) {
        float yaw = entityPlayer.getBukkitEntity().getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH;
        } else if (yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw < 225) {
            return BlockFace.NORTH;
        } else if (yaw < 315) {
            return BlockFace.EAST;
        }
        return BlockFace.NORTH;
    }

    public Item getToRefundPearl() {
        return this.toRefundPearl;
    }

    public void setToRefundPearl(Item pearl) {
        this.toRefundPearl = pearl;
    }
}
