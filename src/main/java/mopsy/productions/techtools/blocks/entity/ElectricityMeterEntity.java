package mopsy.productions.techtools.blocks.entity;

import mopsy.productions.techtools.networking.TTNetwork;
import mopsy.productions.techtools.registry.TTBlockEntities;
import mopsy.productions.techtools.registry.TTBlocks;
import mopsy.productions.techtools.screens.electric_meter.ElectricityMeterScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class ElectricityMeterEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStorage {
    private final SimpleEnergyStorage energyMeterStorage = new SimpleEnergyStorage(Long.MAX_VALUE,Long.MAX_VALUE,0);
    private int tickCounter = 0;
    private long buffer = 0;
    public final long[] storedValues = new long[16];
    public int pointerToCurrentValue = 0;
    public final long[] clientSortedValues = new long[16];
    public long clientMaxValue = 0;
    public float extraXOffset = 0;
    public long clientLastUpdateTime = System.nanoTime();
    public ElectricityMeterEntity(BlockPos pos, BlockState state) {
        super(TTBlockEntities.electricityMeter, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Electricity Meter");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ElectricityMeterScreenHandler(syncId, inv, pos);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeLongArray(storedValues);
        buf.writeInt(pointerToCurrentValue);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, ElectricityMeterEntity entity) {
        if(world.isClient)return;


        entity.tickCounter++;//Update tickCounter
        entity.buffer+=entity.energyMeterStorage.amount;//Add the meterStorage to the buf
        entity.energyMeterStorage.amount=0;//Reset the meterStorage for the next tick
        if(entity.tickCounter==10){//When we reach 10 ticks store the buffer into the stored values array
            entity.tickCounter=0;//Reset tickCounter for next 10 ticks

            entity.storedValues[entity.pointerToCurrentValue]=entity.buffer;
            //Store the buffer into the storedValues at index pointerToCurrentValue
            if(entity.pointerToCurrentValue<entity.storedValues.length-1)//Increase pointerToCurrentValue within 0-15
                entity.pointerToCurrentValue++;
            else
                entity.pointerToCurrentValue=0;

            //Send packets to all close players containing the value in buffer.
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(blockPos);
            buf.writeLong(entity.buffer);
            PlayerLookup.tracking(entity).forEach((player)->{
                ServerPlayNetworking.send(player, TTNetwork.POWER_USAGE_UPDATE,buf);
            });

            entity.buffer=0;//Reset the buffer for the next tick
        }
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        BlockState state = world.getBlockState(pos);
        if(state!=null && state.isOf(TTBlocks.electricityMeter.getBlock())){
            Direction direction = state.get(HorizontalFacingBlock.FACING);
            EnergyStorage storage = EnergyStorage.SIDED.find(world,pos.add(direction.getVector()),direction.getOpposite());
            if(storage!=null){
                try(Transaction moveAndCountTransaction = transaction.openNested()) {
                    long inserted = storage.insert(maxAmount,moveAndCountTransaction);
                    energyMeterStorage.insert(inserted,moveAndCountTransaction);
                    moveAndCountTransaction.commit();
                    return inserted;
                }
            }
        }
        return 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long getAmount() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }
    public static EnergyStorage getEnergyStorageFromDirection(ElectricityMeterEntity entity, Direction direction){
        BlockState state = entity.world.getBlockState(entity.pos);
        if(state!=null && state.isOf(TTBlocks.electricityMeter.getBlock())) {
            Direction blockDirection = state.get(HorizontalFacingBlock.FACING);
            boolean straight = blockDirection==Direction.NORTH||blockDirection==Direction.SOUTH;
            if ((straight && (direction == Direction.NORTH || direction == Direction.SOUTH))
                || (!straight && (direction == Direction.WEST || direction == Direction.EAST)))
                return entity;
        }
        return null;
    }
}
