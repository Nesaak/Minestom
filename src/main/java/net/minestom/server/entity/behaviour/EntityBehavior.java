package net.minestom.server.entity.behaviour;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.behaviour.pathfinding.DefaultPathBehaviour;
import net.minestom.server.entity.behaviour.pathfinding.EntityPathBehaviour;
import net.minestom.server.entity.behaviour.target.DefaultTargetBehaviour;
import net.minestom.server.entity.behaviour.target.EntityTargetBehavior;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.thread.MinestomThread;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class EntityBehavior {
    private EntityPathBehaviour pathBehaviour = new DefaultPathBehaviour();
    private EntityTargetBehavior targetBehavior = new DefaultTargetBehaviour();

    private static ExecutorService pathfindingPool = new MinestomThread(MinecraftServer.THREAD_COUNT_ENTITIES_PATHFINDING, MinecraftServer.THREAD_NAME_ENTITIES_PATHFINDING);

    private Entity entity;

    public EntityBehavior(Entity entity) {
        this.entity = entity;
    }

    public void getPath( Consumer<LinkedList<BlockPosition>> consumer) {
        pathfindingPool.execute(() -> {
            consumer.accept(pathBehaviour.getPath(targetBehavior,entity));
        });
    }

    public void setPathBehaviour(EntityPathBehaviour pathBehaviour){
        this.pathBehaviour = pathBehaviour;
    }
    public void setTargetBehavior(EntityTargetBehavior targetBehavior){
        this.targetBehavior = targetBehavior;
    }

    public EntityPathBehaviour getPathBehaviour() {
        return pathBehaviour;
    }

    public EntityTargetBehavior getTargetBehavior() {
        return targetBehavior;
    }
}
