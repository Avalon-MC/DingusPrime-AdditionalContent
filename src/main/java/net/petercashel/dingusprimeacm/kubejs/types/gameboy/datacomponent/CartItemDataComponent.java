package net.petercashel.dingusprimeacm.kubejs.types.gameboy.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record CartItemDataComponent(String GameID, String UniqueID) {

    public static final Codec<CartItemDataComponent> CARTITEM_SAVE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("GameID").forGetter(CartItemDataComponent::GameID),
                    Codec.STRING.fieldOf("UniqueID").forGetter(CartItemDataComponent::UniqueID)
            ).apply(instance, CartItemDataComponent::new)
    );
    public static final StreamCodec<ByteBuf, CartItemDataComponent> CARTITEM_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CartItemDataComponent::GameID,
            ByteBufCodecs.STRING_UTF8, CartItemDataComponent::UniqueID,
            CartItemDataComponent::new
    );

    // Unit stream codec if nothing should be sent across the network
    public static final StreamCodec<ByteBuf, CartItemDataComponent> UNIT_STREAM_CODEC = StreamCodec.unit(new CartItemDataComponent("defaultrom", UUID.randomUUID().toString()));
}
