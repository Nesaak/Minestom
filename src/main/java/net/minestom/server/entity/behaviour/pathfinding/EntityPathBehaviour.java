package net.minestom.server.entity.behaviour.pathfinding;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.behaviour.pathfinding.algorithmn.astar.AStarPathFinder;
import net.minestom.server.entity.behaviour.pathfinding.algorithmn.PathFinder;
import net.minestom.server.entity.behaviour.target.EntityTargetBehavior;
import net.minestom.server.utils.BlockPosition;

import java.util.LinkedList;

public abstract class EntityPathBehaviour {
    PathFinder pathfinder = new AStarPathFinder();
    public abstract LinkedList<BlockPosition> getPath(EntityTargetBehavior target, Entity entity);
}
