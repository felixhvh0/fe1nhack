package me.alpha432.oyvey.util;

import me.alpha432.oyvey.util.traits.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;

import java.util.ArrayList;

public class WorldUtil implements Util {
    public static boolean canTarget(Entity entity, ArrayList<EntityType> toTarget) {
        if (toTarget.contains(EntityType.ZOMBIE) && entity instanceof HostileEntity) {
            return true;
        } else if (toTarget.contains(EntityType.PIG) && entity instanceof AnimalEntity) {
            return true;
        } else {
            return (entity instanceof BoatEntity || entity instanceof MinecartEntity) && toTarget.contains(EntityType.BOAT) ? true : toTarget.contains(entity.getType());
        }
    }
}
