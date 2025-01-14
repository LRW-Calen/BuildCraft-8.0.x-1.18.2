/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.builders.snapshot;

import buildcraft.lib.misc.data.SingleCache;
import buildcraft.lib.nbt.NbtSquisher;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GlobalSavedDataSnapshots {
    private static final String SNAPSHOT_FILE_EXTENSION = ".bcnbt";
    private static final Map<Dist, GlobalSavedDataSnapshots> INSTANCES = new EnumMap<>(Dist.class);
    private final LoadingCache<Snapshot.Key, Optional<Snapshot>> snapshotsCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(CacheLoader.from(key -> Optional.ofNullable(readSnapshot(key)).map(Pair::getLeft)));
    private final SingleCache<List<Snapshot.Key>> listCache = new SingleCache<>(
            this::readList,
            1,
            TimeUnit.SECONDS
    );
    private final File snapshotsFile;

    private GlobalSavedDataSnapshots(Dist side) {
        snapshotsFile = new File(
//            FMLCommonHandler.instance().getSavesDirectory().getParentFile(),
                FMLPaths.GAMEDIR.get().toFile(),
                "snapshots-" + side.name().toLowerCase(Locale.ROOT)
        );
        if (!snapshotsFile.exists()) {
            if (!snapshotsFile.mkdirs()) {
                throw new RuntimeException("Failed to make the directories required for snapshots: " + snapshotsFile);
            }
        } else if (!snapshotsFile.isDirectory()) {
            throw new IllegalStateException("The snapshots directory was not a directory: " + snapshotsFile);
        }
    }

    public static void reInit(Dist side) {
        INSTANCES.put(side, new GlobalSavedDataSnapshots(side));
    }

    public static GlobalSavedDataSnapshots get(Dist side) {
        if (!INSTANCES.containsKey(side)) {
            INSTANCES.put(side, new GlobalSavedDataSnapshots(side));
        }
        return INSTANCES.get(side);
    }

    public static GlobalSavedDataSnapshots get(Level world) {
        return get(world.isClientSide ? Dist.CLIENT : Dist.DEDICATED_SERVER);
    }

    private Pair<Snapshot, File> readSnapshot(Snapshot.Key key) {
        File[] files = snapshotsFile.listFiles();
        if (files != null) {
            for (File snapshotFile : files) {
                if (snapshotFile.getName().startsWith(key.toString()) &&
                        snapshotFile.getName().endsWith(SNAPSHOT_FILE_EXTENSION))
                {
                    try (FileInputStream fileInputStream = new FileInputStream(snapshotFile)) {
                        Snapshot snapshot = Snapshot.readFromNBT(NbtSquisher.expand(fileInputStream));
                        if (Objects.equals(snapshot.key, key)) {
                            return Pair.of(snapshot, snapshotFile);
                        }
                    } catch (IOException e) {
                        new IOException("Failed to read the snapshot " + snapshotFile, e).printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    private List<Snapshot.Key> readList() {
        ImmutableList.Builder<Snapshot.Key> listBuilder = ImmutableList.builder();
        File[] files = snapshotsFile.listFiles();
        if (files != null) {
            for (File snapshotFile : files) {
                if (snapshotFile.getName().endsWith(SNAPSHOT_FILE_EXTENSION)) {
                    try (FileInputStream fileInputStream = new FileInputStream(snapshotFile)) {
                        Snapshot snapshot = Snapshot.readFromNBT(NbtSquisher.expand(fileInputStream));
                        if (snapshotFile.getName().startsWith(snapshot.key.toString())) {
                            listBuilder.add(snapshot.key);
                        }
                    } catch (IOException io) {
                        new IOException("Failed to read the snapshot " + snapshotFile, io).printStackTrace();
                    }
                }
            }
        }
        return listBuilder.build();
    }

    public void addSnapshot(Snapshot snapshot) {
        File snapshotFile = new File(
                snapshotsFile,
                snapshot.key.toString() + SNAPSHOT_FILE_EXTENSION
        );
        if (!snapshotFile.exists()) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(snapshotFile)) {
                NbtSquisher.squishVanilla(Snapshot.writeToNBT(snapshot), fileOutputStream);
            } catch (IOException e) {
                new IOException("Failed to write the snapshot file: " + snapshotFile, e).printStackTrace();
            }
        }
        snapshotsCache.invalidate(snapshot.key);
        listCache.clear();
    }

    public void removeSnapshot(Snapshot.Key key) {
        Optional.ofNullable(readSnapshot(key)).map(Pair::getRight).ifPresent(snapshotFile ->
        {
            if (!snapshotFile.delete()) {
                new IOException("Failed to read the snapshot file: " + snapshotFile).printStackTrace();
            }
            snapshotsCache.invalidate(key);
        });
        listCache.clear();
    }

    @Nullable
    public Snapshot getSnapshot(@Nullable Snapshot.Key key) {
        if (key == null) return null;
        return snapshotsCache.getUnchecked(key).orElse(null);
    }

    public List<Snapshot.Key> getList() {
        return listCache.get();
    }
}
