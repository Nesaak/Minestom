package net.minestom.server.entity.behaviour.target;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

import java.util.Set;

public class DefaultTargetBehaviour extends EntityTargetBehavior {
    @Override
    void findTarget(Entity source) {
        Instance instance = source.getInstance();
        Set<Player> targets = instance.getPlayers();
        if(targets.isEmpty())return;
        Player near = null;
        float dNear = 0;
        for(Player t:targets){
            if(near==null){
                near = t;
                dNear = t.getPosition().getDistance(source.getPosition());
                continue;
            }
            float d = t.getPosition().getDistance(source.getPosition());
            if(d<dNear){
                near = t;
                dNear = d;
            }
        }

        target = near.getPosition().toBlockPosition();
    }
}
