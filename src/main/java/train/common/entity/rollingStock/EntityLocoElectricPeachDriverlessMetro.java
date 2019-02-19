package train.common.entity.rollingStock;

import com.google.common.collect.Collections2;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import train.common.Traincraft;
import train.common.api.ElectricTrain;
import train.common.api.EntityRollingStock;
import train.common.library.GuiIDs;
import train.common.mtc.PDMMessage;
import train.common.mtc.RouteStation;
import train.common.mtc.TilePDMInstructionRadio;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;

public class EntityLocoElectricPeachDriverlessMetro extends ElectricTrain {
	public EntityLocoElectricPeachDriverlessMetro(World world) {
		super(world);
	}
	public String status = "inactive";
	public Boolean consistComplete = false;
	public  EntityLocoElectricPeachDriverlessMetro otherDrivingOne;
	public Boolean readyToGo = false;
	public Boolean depotStartup = false;
	public Boolean drivingStatus = false;
	public ArrayList<RouteStation> timetable = new ArrayList<RouteStation>();
	public int system = 0;
	public int trainID = 0;
	public String nextStation = "";
	public RouteStation currentStation;
	public String uniqueID = "";
	public String serverUUID = "";
	public EntityLocoElectricPeachDriverlessMetro(World world, double d, double d1, double d2) {
		this(world);
		setPosition(d, d1 + yOffset, d2);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = d;
		prevPosY = d1;
		prevPosZ = d2;
		uniqueID = UUID.randomUUID().toString();
		System.out.println(uniqueID);
	}

	@Override
	public void updateRiderPosition() {
		double pitchRads = this.anglePitchClient * Math.PI / 180.0D;
		double distance = 1.5;
		double yOffset = 0.2;
		float rotationCos1 = (float) Math.cos(Math.toRadians(this.renderYaw + 90));
		float rotationSin1 = (float) Math.sin(Math.toRadians((this.renderYaw + 90)));
		if(side.isServer()){
			rotationCos1 = (float) Math.cos(Math.toRadians(this.serverRealRotation + 90));
			rotationSin1 = (float) Math.sin(Math.toRadians((this.serverRealRotation + 90)));
			anglePitchClient = serverRealPitch*60;
		}
		float pitch = (float) (posY + ((Math.tan(pitchRads) * distance) + getMountedYOffset())
				+ riddenByEntity.getYOffset() + yOffset);
		float pitch1 = (float) (posY + getMountedYOffset() + riddenByEntity.getYOffset() + yOffset);
		double bogieX1 = (this.posX + (rotationCos1 * distance));
		double bogieZ1 = (this.posZ + (rotationSin1* distance));
		//System.out.println(rotationCos1+" "+rotationSin1);
		if(anglePitchClient>20 && rotationCos1 == 1){
			bogieX1 -= pitchRads * 2;
			pitch -= pitchRads * 1.2;
		}
		if (anglePitchClient > 20 && rotationSin1 == 1) {
			bogieZ1 -= pitchRads * 2;
			pitch -= pitchRads * 1.2;
		}
		if (pitchRads == 0.0) {
			riddenByEntity.setPosition(bogieX1, pitch1, bogieZ1);
		}
		if (pitchRads > -1.01 && pitchRads < 1.01) {
			riddenByEntity.setPosition(bogieX1, pitch, bogieZ1);
		}
	}

	@Override
	public void setDead() {
		super.setDead();
		isDead = true;
		if (this.serverUUID != "") {
			JsonObject sendingObj = new JsonObject();
			sendingObj.addProperty("funct", "trainDead");
			sendingObj.addProperty("uuid", this.uniqueID);
			sendMessage(new PDMMessage(this.uniqueID, serverUUID, sendingObj.toString(), this.system));
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		//Code for detecting the other locomotive, should be on the other side
		//if (cartLinked1 != null) {
		//if ((cartLinked1).train != null && (cartLinked1).train.getTrains().size() != 0) {
		//	for (int j1 = 0; j1 < (cartLinked1).train.getTrains().size(); j1++) {
		//		EntityRollingStock daRollingStock = (cartLinked1).train.getTrains().get(j1);


		//System.out.println(train.getTrains().get(train.getTrains().size() - 1));

		//	if (daRollingStock instanceof EntityLocoElectricPeachDriverlessMetro) {
		//	otherDrivingOne = ( EntityLocoElectricPeachDriverlessMetro) daRollingStock;
		//	otherDrivingOne.uniqueID = this.uniqueID;
		//	consistComplete = true;
		//		break;
		//} else {
		//otherDrivingOne = null;
		//}
		//}
		//}

		//}

		//Test if the last car is there

		if (!this.worldObj.isRemote) {
			try {
			if (cartLinked1 != null) {

					if (train.getTrains().get(train.getTrains().size() - 1) instanceof EntityLocoElectricPeachDriverlessMetro) {
						otherDrivingOne = (EntityLocoElectricPeachDriverlessMetro) train.getTrains().get(train.getTrains().size() - 1);
					}


			}

			if (otherDrivingOne != null) {
				consistComplete = true;
				//if (this.drivingStatus) {
			//		otherDrivingOne.uniqueID = this.uniqueID;
			//	}
			}

			//if (consistComplete) {
			//	sendMessage(new PDMMessage(this.uniqueID, "8aaa744b-3e78-4c46-bde4-1d38a1e08136", "System ready", 0));
		//	}
			//System.out.println(drivingStatus + " " + uniqueID);
				if (drivingStatus) {
					if (drivingStatus && mtcStatus == 1 | mtcStatus == 2 && !status.equals("stationStop")) {
						atoStatus = 1;
					}
					if (depotStartup && drivingStatus && !status.equals("stationStop")) {
						//accel(10);
						if (this.getSpeed() < 25) {
							accel(25);
						} else if (this.getSpeed() > 25) {
							brakePressed = true;
						}
						if (mtcStatus == 1 | mtcStatus == 2) {
							atoStatus = 1;
							depotStartup = false;
							drivingStatus = true;
						}

					}

					if (status.equals("stationstop") && currentStation != null && drivingStatus) {
						Integer tickWaitTime = this.ticksExisted % (currentStation.waitTime * 20);
						System.out.println(tickWaitTime);
						if (tickWaitTime == 0) {
							//Depart the train
							this.xFromStopPoint = 0.0;
							this.yFromStopPoint = 0.0;
							this.zFromStopPoint = 0.0;
							System.out.println("Departing from the station.");
							this.parkingBrake = false;
							atoStatus = 1;
							accel(speedLimit);
							status = "driving";
						} else {
							atoStatus = 0;
							this.motionX = 0.0;
							this.motionZ = 0.0;
							this.parkingBrake = true;
						}
					}
					if (this.parkingBrake && this.nextStation != "") {
						//Train has stopped, remove it from the timetable

						for (RouteStation station : timetable) {
							if (station.stationName.equals(nextStation)) {
								timetable.remove(station);
								System.out.println(nextStation + " removed from timetable.");
								status = "stationstop";
								currentStation = station;
								if (station.endOfLine) {
									//Train has officially, reached the end, reverse.
									System.out.println("Has complete journey. Reversing.");
									otherDrivingOne.status = "stationstop";
									otherDrivingOne.currentStation = this.currentStation;
									this.drivingStatus = false;
									otherDrivingOne.drivingStatus = true;
									otherDrivingOne.uniqueID = this.uniqueID;
									this.uniqueID = this.otherDrivingOne.uniqueID;

									this.canBePulled = true;
									this.setCanBeAdjusted(true);
									otherDrivingOne.canBePulled = false;
									otherDrivingOne.setCanBeAdjusted(false);
									depotStartup = true;
									System.out.println(otherDrivingOne.uniqueID);
									System.out.println(this.canBePulled);
									System.out.println(otherDrivingOne.status);
									JsonObject sendingObj = new JsonObject();
									sendingObj.addProperty("uuid", this.uniqueID);
									sendingObj.addProperty("funct", "newTimetable");
									sendingObj.addProperty("origin", currentStation.getStationID());
									sendMessage(new PDMMessage(otherDrivingOne.uniqueID, serverUUID, sendingObj.toString(), this.system));
									System.out.println(this.uniqueID + " is sending requests to reverse");
								}
								nextStation = "";
								break;
							}

						}


					}
					if (otherDrivingOne != null) {
						if (otherDrivingOne.status.equals("drivingNow")) {
							JsonObject sendingObj = new JsonObject();
							sendingObj.addProperty("uuid", this.uniqueID);
							sendingObj.addProperty("function", "newTimetable");
							sendingObj.addProperty("origin", currentStation.getStationID());
							sendMessage(new PDMMessage(this.uniqueID, serverUUID, sendingObj.toString(), this.system));
							System.out.println(this.uniqueID + " is sending requests to reverse");
						}
					}
					//Send a message to the server every 5 seconds
					if (this.ticksExisted % 100 == 0 && !serverUUID.equals("") && this.drivingStatus) {
						JsonObject sendingObj = new JsonObject();
						sendingObj.addProperty("uuid", this.uniqueID);
						sendingObj.addProperty("funct", "statusCheck");
						sendingObj.addProperty("drivingStatus", this.drivingStatus);
						sendingObj.addProperty("nextStation", this.nextStation);
						sendingObj.addProperty("x", this.posX);
						sendingObj.addProperty("y", this.posY);
						sendingObj.addProperty("z", this.posZ);
						sendMessage(new PDMMessage(this.uniqueID, serverUUID, sendingObj.toString(), this.system));
						System.out.println("Sending~");
					}
			/*System.out.println(timetable.size());
			if (timetable.size() == 0 && consistComplete && !depotStartup) {
				//Train has officially, reached the end, reverse.
				System.out.println("Has complete journey. Reversing.");
				this.drivingStatus = false;
				otherDrivingOne.drivingStatus = true;
				this.uniqueID = this.otherDrivingOne.uniqueID;
				otherDrivingOne.uniqueID = this.uniqueID;
			}*/
					//Allow for partial acceleration help
					if (mtcStatus == 1 | mtcStatus == 2 && !this.status.equals("stationstop") && drivingStatus) {
						if (this.getSpeed() < this.speedLimit) {
							accel(this.speedLimit);
						}

					}
				}
		} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
			}

	@Override
	public void pressKey(int i) {
		if (i == 7 && riddenByEntity != null && riddenByEntity instanceof EntityPlayer) {
			((EntityPlayer) riddenByEntity).openGui(Traincraft.instance, GuiIDs.LOCO, worldObj, (int) this.posX, (int) this.posY, (int) this.posZ);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);

		nbttagcompound.setShort("fuelTrain", (short) fuelTrain);
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < locoInvent.length; i++) {
			if (locoInvent[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				locoInvent[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("Items", nbttaglist);
		nbttagcompound.setString("uniqueID", uniqueID);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);

		fuelTrain = nbttagcompound.getShort("fuelTrain");
		NBTTagList nbttaglist = nbttagcompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		locoInvent = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 0xff;
			if (j >= 0 && j < locoInvent.length) {
				locoInvent[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
		uniqueID = nbttagcompound.getString("uniqueID");
	}

	@Override
	public int getSizeInventory() {
		return inventorySize;
	}
	@Override
	public String getInventoryName() {
		return "Peach Driverless Metro";
	}

	@Override
	public boolean interactFirst(EntityPlayer entityplayer) {
		playerEntity = entityplayer;
		if ((super.interactFirst(entityplayer))) {
			return false;
		}
		if (!worldObj.isRemote) {
			if (riddenByEntity != null && (riddenByEntity instanceof EntityPlayer) && riddenByEntity != entityplayer) {
				return true;
			}
			entityplayer.mountEntity(this);
			System.out.println("This train's unique ID is " + uniqueID + " and it's system is " + system + " driving status " + drivingStatus);
		}
		return true;
	}
	@Override
	public float getOptimalDistance(EntityMinecart cart) {
		return 0.7F;
	}

	@Override
	public boolean canBeAdjusted(EntityMinecart cart) {
		return canBeAdjusted;
	}
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	public void receiveMessage(PDMMessage message) {
		System.out.println("Got one!");

		if (message != null) {

			if (message.system == this.system) {
				System.out.println("Got one from teh server!!! " + message.message);
				JsonParser parser = new JsonParser();
				System.out.println(message.message);
				JsonObject thing = parser.parse(message.message.toString()).getAsJsonObject();

				if (thing.get("yardStart").getAsBoolean()) {
					System.out.println(consistComplete);
					if (consistComplete) {
						System.out.println("Instruction received to yard start..trains consist status is " + consistComplete);
						otherDrivingOne.drivingStatus = false;
						JsonArray stations = new JsonParser().parse(thing.get("timetable").toString()).getAsJsonArray();

						//for(int n = 0; n < stations.size(); n++)
						//{
							//JsonObject object = stations.get(n);
							//System.out.println("test "   + stations.get(n).getAsString());
							// do some stuff....
						//}
						for (JsonElement pa : stations) {
							JsonObject paymentObj = pa.getAsJsonObject();
							timetable.add(new RouteStation(paymentObj.get("stationID").getAsInt(), paymentObj.get("stationName").getAsString(), paymentObj.get("waitTime").getAsInt(), paymentObj.get("isEndOfLine").getAsBoolean()));
							if (paymentObj.get("isEndOfLine").getAsBoolean()) {
								this.destination = paymentObj.get("stationName").getAsString();
							}
						}
						System.out.println("Timetable received and created..moving train..");
						this.drivingStatus = true;
						this.drivingStatus = true;
						this.drivingStatus = true;
						this.drivingStatus = true;
						this.drivingStatus = true;
						this.drivingStatus = true;
						this.depotStartup = true;
						this.depotStartup = true;
						this.depotStartup = true;

					}
					//The depot startup is handled by the system itself, once it gets an MTC speed signal MTC takes over
				}

				if (thing.get("function").getAsString().equals("newTimetableAccepted")) {
					JsonArray stations = new JsonParser().parse(thing.get("timetable").toString()).getAsJsonArray();
					System.out.println(stations);
					for (JsonElement pa : stations) {
						JsonObject paymentObj = pa.getAsJsonObject();
						timetable.add(new RouteStation(paymentObj.get("stationID").getAsInt(), paymentObj.get("stationName").getAsString(), paymentObj.get("waitTime").getAsInt(), paymentObj.get("isEndOfLine").getAsBoolean()));
						if (paymentObj.get("isEndOfLine").getAsBoolean()) {
							this.destination = paymentObj.get("stationName").getAsString();
						}
					}
					System.out.println("Timetable received and created.");
				}
				if (thing.get("function").getAsString().equals("responseStatusCheck")) {
					//this.speedLimit = thing.get("speedLimit").getAsInt();
					//this.nextSpeedLimit = thing.get("nextSpeedLimit").getAsInt();

				}
			}
		}
	}

	public void sendMessage(PDMMessage message) {
		AxisAlignedBB targetBox = AxisAlignedBB.getBoundingBox(this.posX, this.posY, this.posZ, this.posX + 512, this.posY + 512, this.posZ + 512);
		List<TileEntity> allTEs = worldObj.loadedTileEntityList;
		for (TileEntity te : allTEs) {

			if (te instanceof TilePDMInstructionRadio) {

				TilePDMInstructionRadio teP = (TilePDMInstructionRadio)te;
				if (teP != null) {
					if (teP.uniqueID.equals(message.UUIDTo)) {
						teP.receiveMessage(message);
					}
				}
			}
		}


	}


	public void setDrivingStatus(Boolean status) {
		drivingStatus = status;
	}

	@Override
	public void accel(Integer desiredSpeed) {

		if (this.worldObj != null) {
			if (this.getSpeed() != desiredSpeed) {
				if ((int) this.getSpeed() <= desiredSpeed) {

					double rotation = this.serverRealRotation;
					if (rotation == 90.0) {

						this.motionX -= 0.0075 * this.accelerate;


					} else if (rotation == -90.0) {

						this.motionX += 0.0075 * this.accelerate;

					} else if (rotation == 0.0) {

						this.motionZ += 0.0075 * this.accelerate;

					} else if (rotation == -180.0) {

						this.motionZ -= 0.0075 * this.accelerate;

					} else {
						this.motionX = this.motionX;
						this.motionZ = this.motionZ;
					}

				}

			}
		}
	}


}