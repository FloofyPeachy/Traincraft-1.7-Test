package train.common.mtc;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import train.common.entity.rollingStock.EntityLocoElectricPeachDriverlessMetro;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TilePDMInstructionRadio extends TileEntity implements IPeripheral {
    public Boolean isActivated = false;
    public ArrayList<IComputerAccess> computers = new ArrayList<IComputerAccess>();
    public int system = 0;
    public String uniqueID;
    public AxisAlignedBB boundingBox = null;

    public TilePDMInstructionRadio() {



    }
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.isActivated = nbttagcompound.getBoolean("activated");
        this.uniqueID =  nbttagcompound.getString("uniqueID");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("activated", this.isActivated);
        nbttagcompound.setString("uniqueID", this.uniqueID);
    }
    @Override
    public String getType() {
        return "pdmInstructionRadio";
    }

    @Override
    public String[] getMethodNames() {
        return new String[] {"activate", "deactivate", "sendMessage", "getSelfUUID"};
    }

    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
       System.out.println("method hasd been called");
        switch(method) {
            case 0: {
                isActivated = true;
                uniqueID = UUID.randomUUID().toString();
                System.out.println(uniqueID);
                return new Object[] {true};

            } case 1: {
                isActivated = false;
                return new Object[] {true};

            } case 2: {
                sendMessage((String) arguments[0], arguments[1]);
                System.out.println("work");
                return new Object[]{true};
            } case 3 : {

                System.out.println(uniqueID);
                return new Object[]{uniqueID};
            } default:
                return new Object[] {"nil"};
        }
    }

    @Override
    public void attach(IComputerAccess computer) {
        System.out.println(computer==null);
        computers.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        computers.remove(computer);
    }

    @Override
    public boolean equals(IPeripheral other) {
        return false;
    }

    public void sendMessage(String UUIDTo, Object message) {
            for (EntityLocoElectricPeachDriverlessMetro pdm : getTrainsInBoundingBox()) {
                System.out.println("Found one");
                System.out.println("Train's UUID: " + pdm.uniqueID);
                System.out.println("Finding UUID: " + UUIDTo);

                if (pdm.uniqueID.equals(UUIDTo)) {
                    System.out.println("Both of those match! Sending message..");
                    pdm.receiveMessage(new PDMMessage(this.uniqueID, UUIDTo, message, system));
                }
            }
    }

    public List<EntityLocoElectricPeachDriverlessMetro> getTrainsInBoundingBox() {
        List<Object> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, this.getRenderBoundingBox());
        ArrayList<EntityLocoElectricPeachDriverlessMetro> returnList = new ArrayList<EntityLocoElectricPeachDriverlessMetro>();
        if (list != null) {
            for (Object obj : list) {
                System.out.println(obj.getClass().getName());
                if (obj instanceof EntityLocoElectricPeachDriverlessMetro) {
                    returnList.add((EntityLocoElectricPeachDriverlessMetro)obj);
                }
            }
        }
        return returnList;

    }

    public void receiveMessage(PDMMessage message) {
        try {
            if (computers != null && computers.size() > 0) {
                for (IComputerAccess c : computers) {
                    c.queueEvent("pdm_message", PDMMessage.getEventData(c));
                    System.out.println(message.message);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (boundingBox == null) {
            System.out.println("new");
            boundingBox = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 500, yCoord +500, zCoord + 500);
        }
        return boundingBox;
    }
}
