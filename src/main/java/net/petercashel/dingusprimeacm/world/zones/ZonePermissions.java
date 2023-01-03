package net.petercashel.dingusprimeacm.world.zones;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ZonePermissions implements INBTSerializable<CompoundTag> {
    public EnumSet<ZonePermissionsEnum> permissionSet = EnumSet.noneOf(ZonePermissionsEnum.class);

    public boolean hasPermissionFlag(ZonePermissionsEnum flag) {
        return permissionSet.contains(flag);
    }
    public boolean addPermissionFlag(ZonePermissionsEnum flag) {
        if (!hasPermissionFlag(flag)) {
            return permissionSet.add(flag);
        }
        return false;
    }
    public boolean removePermissionFlag(ZonePermissionsEnum flag) {
        if (!hasPermissionFlag(flag)) {
            return permissionSet.remove(flag);
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("flags", ZonePermissionsEnum.encode(permissionSet));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("flags")) {
            permissionSet = ZonePermissionsEnum.decode(nbt.getInt("flags"), ZonePermissionsEnum.class);
        } else {
            permissionSet = EnumSet.noneOf(ZonePermissionsEnum.class);
        }
    }

    public void SetPermissionState(ZonePermissionsEnum permission, Boolean state) {
        if (state) {
            this.addPermissionFlag(permission);
        } else {
            this.removePermissionFlag(permission);
        }
    }

    public enum ZonePermissionPlayerType {
        Member,
        Ally,
        Public
    }

    public enum ZonePermissionsEnum implements FlagEnum {
        Enter(1),
        Build(2),
        Destroy(4),
        Interact(8),
        ItemUse(16),

        ;
        private int flag;
        ZonePermissionsEnum(int flagVal) {
            this.flag = flagVal;
        }

        @Override
        public int getFlag() {
            return flag;
        }

        public static <E extends Enum<E> & FlagEnum > int encode(EnumSet<E> set) {
            int ret = 0;

            for (E val : set) {
                ret |= (1 << val.getFlag());
            }

            return ret;
        }

        public static <E extends Enum<E> & FlagEnum> EnumSet<E> decode(int encoded, Class<E> enumClass) {
            // First populate a look-up map of ordinal to Enum flag value.
            Map<Integer, E> ordinalMap = new HashMap<Integer, E>();
            for (E val : EnumSet.allOf(enumClass)) {
                ordinalMap.put(val.getFlag(), val);
            }

            EnumSet<E> ret= EnumSet.noneOf(enumClass);
            int ordinal = 0;

            // Now loop over encoded value by analysing each bit independently.
            // If the bit is set, determine which ordinal that corresponds to
            // (by also maintaining an ordinal counter) and use this to retrieve
            // the correct value from the look-up map.
            for (int i=1; i!=0; i <<= 1) {
                if ((i & encoded) != 0) {
                    ret.add(ordinalMap.get(ordinal));
                }

                ++ordinal;
            }

            return ret;
        }
    }

    interface FlagEnum{
        int getFlag();
    }
}
