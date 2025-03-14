package net.petercashel.dingusprimeacm.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.INBTSerializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkUtils {

    public static class Serialization {
        public static CompoundTag SerializeAABB(AABB aabb) {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("AABB_min_X", aabb.minX);
            tag.putDouble("AABB_min_Y", aabb.minY);
            tag.putDouble("AABB_min_Z", aabb.minZ);
            tag.putDouble("AABB_max_X", aabb.maxX);
            tag.putDouble("AABB_max_Y", aabb.maxY);
            tag.putDouble("AABB_max_Z", aabb.maxZ);
            return tag;
        }

        public static AABB DeserializeAABB(CompoundTag tag) {
            AABB aabb = new AABB(
                tag.getDouble("AABB_min_X"),
                tag.getDouble("AABB_min_Y"),
                tag.getDouble("AABB_min_Z"),
                tag.getDouble("AABB_max_X"),
                tag.getDouble("AABB_max_Y"),
                tag.getDouble("AABB_max_Z")
            );
            return aabb;
        }

        public static CompoundTag SerializeBlockPos(BlockPos pos) {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("X", pos.getX());
            tag.putDouble("Y", pos.getY());
            tag.putDouble("Z", pos.getZ());
            return tag;
        }

        public static BlockPos DeserializeBlockPos(CompoundTag tag) {
            BlockPos pos = new BlockPos(
                    tag.getDouble("X"),
                    tag.getDouble("Y"),
                    tag.getDouble("Z")
            );
            return pos;
        }


    }

}
