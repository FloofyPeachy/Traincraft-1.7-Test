package train.common.core.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import train.common.api.EntityRollingStock;
import train.common.api.Locomotive;
import train.common.entity.digger.EntityRotativeDigger;
import train.common.entity.rollingStock.EntityDieselEnergyGeneratorCar;
import train.common.entity.zeppelin.AbstractZeppelin;

public class PacketDEGActivate implements IMessage{
    //1 = on
    //0 = off
    int status;
    int id;
    public PacketDEGActivate() {}

    public PacketDEGActivate(int status, int id) {

        this.status = status;
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf bbuf) {
        this.status = bbuf.readInt();
        this.id = bbuf.readInt();
    }

    @Override
    public void toBytes(ByteBuf bbuf) {
        bbuf.writeInt(this.status);
        bbuf.writeInt(this.id);
    }

    public static class Handler implements IMessageHandler<PacketDEGActivate, IMessage> {

        @Override
        public IMessage onMessage(PacketDEGActivate message, MessageContext context) {

            Entity ridingEntity = context.getServerHandler().playerEntity.worldObj.getEntityByID(message.id);

            /* "instanceof" is null-safe, but we check to avoid four unnecessary instanceof checks for when the value is null anyways. */
            if (ridingEntity != null) {
                if (ridingEntity instanceof EntityDieselEnergyGeneratorCar) {
                    System.out.println(message.status);
                    if (message.status == 1) {
                        ((EntityDieselEnergyGeneratorCar)ridingEntity).isActivated = true;
                    } else {
                        ((EntityDieselEnergyGeneratorCar)ridingEntity).isActivated = false;
                    }

                }
            }

			/*if (message.key == 404){
				CommonProxy.debug = CommonProxy.debug;
				if (Minecraft.getMinecraft().theWorld != null) {
					System.out.println(Minecraft.getMinecraft().theWorld.isRemote);
				}
			}*/
            return null;
        }
    }
}
