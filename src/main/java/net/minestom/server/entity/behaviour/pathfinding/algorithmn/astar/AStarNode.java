package net.minestom.server.entity.behaviour.pathfinding.algorithmn.astar;

import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AStarNode {
    private int x,y,z;
    private int g, h;
    private AStarNode parent;


    public AStarNode(BlockPosition position){
        this(position.getX(),position.getY(),position.getZ());
    }
    public AStarNode(int x, int y, int z){
        this(x,y,z,null);
    }
    public AStarNode(int x, int y, int z, AStarNode parentNode){
        this.x = x;
        this.y = y;
        this.z = z;
        if(parentNode==null){
            this.parent = this;
            g = 0;
        }
        else{
            this.parent = parentNode;
            g = parent.g+distance(parent);
        }
    }

    public int getFCost(){
        return g+h;
    }
    public int getGCost(){
        return g;
    }
    public void calculateHCost(BlockPosition targetPosition){
        int dx = (targetPosition.getX()-x);
        int dy = (targetPosition.getY()-y);
        int dz = (targetPosition.getZ()-z);
        h = dx*dx+dy*dy+dz*dz;
    }
    public BlockPosition getBlockPosition(){
        return new BlockPosition(x,y,z);
    }

    public List<AStarNode> getNearbyNodes(Instance instance){
        List<AStarNode> result = new ArrayList<>();
        for(int x = -1;x<=1;x++){
            for(int y = -1;y<=1;y++){
                for(int z = -1;z<=1;z++){
                    AStarNode nextNode = new AStarNode(this.x+x,this.y+y,this.z+z,this);
                    if(nextNode.isAccessible(instance))result.add(nextNode);
                }
            }
        }
        return result;
    }

    private boolean isAccessible(Instance instance){
        BlockPosition target = getBlockPosition();
        if (instance.getChunkAt(target) == null) return false;
        Block targetBlock = Block.fromId(instance.getBlockId(target));
        Block belowBlock = Block.fromId(instance.getBlockId(target.clone().add(0, -1, 0)));
        boolean jumpNode = false;
        /*if(!belowBlock.isSolid()){
            List<Block> sur = getSurroundingBlocksXZPlane(target.clone().add(0, -1, 0),instance);
            for(Block b:sur){
                if(b.isSolid())jumpNode=true;
            }
        }*/


        Block tbelowBlock = Block.fromId(instance.getBlockId(target.clone().add(0, -2, 0)));
        return !targetBlock.isSolid() && belowBlock.isSolid() || !targetBlock.isSolid() && tbelowBlock.isSolid() && jumpNode;
    }

    public List<Block> getSurroundingBlocksXZPlane(BlockPosition a,Instance instance){
        List<Block> result = new ArrayList<>();
        for(int x = -1; x<=1;x++){
            for(int z = -1; z<=1;z++){
                result.add(Block.fromId(instance.getBlockId(a.clone().add(x,0,z))));
            }
        }
        return result;
    }

    public AStarNode getParent(){
        return parent;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AStarNode aStarNode = (AStarNode) o;
        return x == aStarNode.x &&
                y == aStarNode.y &&
                z == aStarNode.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    public int distance(AStarNode to){
        int dx = to.x-x;
        int dy = to.y-y;
        int dz = to.z-z;
        return dx*dx + dy*dy + dz*dz;
    }
}
