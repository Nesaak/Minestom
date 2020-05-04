package net.minestom.server.entity.behaviour.pathfinding;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.behaviour.target.EntityTargetBehavior;
import net.minestom.server.utils.BlockPosition;

import java.util.LinkedList;

public class DefaultPathBehaviour extends EntityPathBehaviour {
    @Override
    public LinkedList<BlockPosition> getPath(EntityTargetBehavior target, Entity entity) {
        BlockPosition targetPos = target.getTarget(entity);
        if(targetPos==null)targetPos=entity.getPosition().toBlockPosition();
        return pathfinder.getPath(entity.getPosition().toBlockPosition(),targetPos,entity.getInstance());
    }
}
