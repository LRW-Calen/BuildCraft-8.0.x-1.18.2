/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.lib.misc;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RotationUtil
{
    public static AABB rotateAABB(AABB aabb, Direction facing) {
        if (facing == Direction.DOWN) {
            return new AABB(aabb.minX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ);
        } else if (facing == Direction.UP) {
            return new AABB(aabb.minX, 1 - aabb.maxY, aabb.minZ, aabb.maxX, 1 - aabb.minY, aabb.maxZ);
        } else if (facing == Direction.NORTH) {
            return new AABB(aabb.minX, aabb.minZ, aabb.minY, aabb.maxX, aabb.maxZ, aabb.maxY);
        } else if (facing == Direction.SOUTH) {
            return new AABB(aabb.minX, aabb.minZ, 1 - aabb.maxY, aabb.maxX, aabb.maxZ, 1 - aabb.minY);
        } else if (facing == Direction.WEST) {
            return new AABB(aabb.minY, aabb.minZ, aabb.minX, aabb.maxY, aabb.maxZ, aabb.maxX);
        } else if (facing == Direction.EAST) {
            return new AABB(1 - aabb.maxY, aabb.minZ, aabb.minX, 1 - aabb.minY, aabb.maxZ, aabb.maxX);
        }
        return aabb;
    }

    public static Vec3 rotateVec3d(Vec3 vec, Rotation rotation) {
        switch (rotation) {
            case NONE:
            default:
                return vec;
            case CLOCKWISE_90:
                return new Vec3(1 - vec.z, vec.y, vec.x);
            case CLOCKWISE_180:
                return new Vec3(1 - vec.x, vec.y, 1 - vec.z);
            case COUNTERCLOCKWISE_90:
                return new Vec3(vec.z, vec.y, 1 - vec.x);
        }
    }

    public static Direction rotateAll(Direction facing) {
        switch (facing) {
            case NORTH:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.WEST;
            case WEST:
                return Direction.UP;
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.NORTH;
        }
        throw new IllegalArgumentException();
    }

    public static Rotation invert(Rotation rotation) {
        switch (rotation) {
            case NONE:
                return Rotation.NONE;
            case CLOCKWISE_90:
                return Rotation.COUNTERCLOCKWISE_90;
            case CLOCKWISE_180:
                return Rotation.CLOCKWISE_180;
            case COUNTERCLOCKWISE_90:
                return Rotation.CLOCKWISE_90;
        }
        throw new IllegalArgumentException();
    }
}