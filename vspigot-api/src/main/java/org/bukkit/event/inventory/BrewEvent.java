package org.bukkit.event.inventory;

import org.bukkit.event.block.*;
import org.bukkit.event.*;
import org.bukkit.inventory.*;
import org.bukkit.block.*;

public class BrewEvent extends BlockEvent implements Cancellable
{
    private static final HandlerList handlers;
    private BrewerInventory contents;
    private final ItemStack[] results;
    private boolean cancelled;

    public BrewEvent(final Block brewer, final BrewerInventory contents, final ItemStack[] results) {
        super(brewer);
        this.contents = contents;
        this.results = results;
    }

    public BrewerInventory getContents() {
        return this.contents;
    }

    public ItemStack[] getResults() {
        return this.results;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return BrewEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return BrewEvent.handlers;
    }

    static {
        handlers = new HandlerList();
    }
}
