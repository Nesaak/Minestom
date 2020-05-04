package net.minestom.server.entity.behaviour.target;

import net.minestom.server.entity.Entity;
import net.minestom.server.utils.BlockPosition;

public abstract class EntityTargetBehavior {
    BlockPosition target;
    public BlockPosition getTarget(Entity source){
        if(target==null)findTarget(source);
        return target;
    }

    abstract void findTarget(Entity source);

    public void setTarget(BlockPosition position){
        target = position;
    }
}
