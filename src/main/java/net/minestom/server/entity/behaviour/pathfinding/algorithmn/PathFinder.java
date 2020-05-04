package net.minestom.server.entity.behaviour.pathfinding.algorithmn;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.behaviour.target.EntityTargetBehavior;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.BlockPosition;

import java.util.LinkedList;

public interface PathFinder {
    LinkedList<BlockPosition> getPath(BlockPosition start, BlockPosition end, Instance instance);
}
