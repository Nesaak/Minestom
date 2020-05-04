package net.minestom.server.entity.behaviour.pathfinding.algorithmn.astar;

import net.minestom.server.entity.behaviour.pathfinding.algorithmn.PathFinder;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;

import java.util.*;
import java.util.function.Predicate;

public class AStarPathFinder implements PathFinder {
    private final int MAX_CHECK = 1000;
    @Override
    public LinkedList<BlockPosition> getPath(BlockPosition start, BlockPosition end,Instance instance) {
       List<AStarNode> open = new ArrayList<>();
       List<AStarNode> closed = new ArrayList<>();

       AStarNode startNode = new AStarNode(start);
       AStarNode endNode = new AStarNode(end);

       open.add(startNode);
        int check = 0;
       AStarNode current = startNode;
       while(!open.isEmpty()) {
           if (check > MAX_CHECK) {
               return build(current,instance);
           }
           check++;
           Optional<AStarNode> currentOptional = getLeastCostNode(open, end);
           if (!currentOptional.isPresent()) {
               return build(current,instance);
           }
           current = currentOptional.get();
           open.remove(current);
           closed.add(current);

           if (current.equals(endNode)) {
               return build(current,instance);
           }

           for (AStarNode c : current.getNearbyNodes(instance)) {
               if (closed.contains(c)) continue;
               if (open.contains(c)) {
                   AStarNode old = null;
                   for (AStarNode o : open) {
                       if (c.equals(o)) old = c;
                   }
                   if (c.getGCost() < old.getGCost()) {
                       open.remove(old);
                       open.add(c);
                   }
               } else {
                   open.add(c);
               }
           }

       }

       return build(startNode,instance);
    }
    private LinkedList<BlockPosition> build(AStarNode finalNode,Instance instance){
        LinkedList<BlockPosition> path = new LinkedList<>();
        AStarNode current = finalNode;
        while(current.getParent()!=current){
            BlockPosition pos = current.getBlockPosition();
            //instance.setBlock(pos.toPosition(),Block.REDSTONE_BLOCK);
            path.push(pos);
            current = current.getParent();
        }
        path.push(current.getBlockPosition());


        return path;
    }

    public Optional<AStarNode> getLeastCostNode(List<AStarNode> nodes, BlockPosition target){
        if(nodes==null)return Optional.empty();
        AStarNode least = null;
        for(AStarNode node:nodes){
            node.calculateHCost(target);
            if(least==null){
                least=node;
                continue;
            }
            if(node.getFCost()<least.getFCost()){
                least = node;
            }
        }
        return Optional.ofNullable(least);
    }
    static<T> Predicate<T> not(Predicate<T> p) {
        return t -> !p.test(t);
    }
}
