package me.kopamed.pm;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Mod(modid = ParticleMod.MODID, version = ParticleMod.VERSION)
public class ParticleMod
{
    public static final String MODID = "particlemod";
    public static final String VERSION = "1.1";

    public static int critMultiplier = 1;
    public static int sharpnessMultiplier = 2;
    public static boolean showSharpness = true;
    public static boolean allwaysShowSharpness = true;
    public static boolean showCriticals = true;
    public static boolean onlyPlayers = false;
    public static boolean cancel_imp = false; // thanks to blowsy for this idea
    public static boolean impNewParticle = false;
    private static final ScheduledExecutorService ex = Executors.newScheduledThreadPool(2);

    public static final String CRITMPREFIX = "critM~ ";
    public static final String SHARPMPREFIX = "sharpM~ ";
    public static final String SHOWSPREFIX = "showS~ ";
    public static final String ALWAYSSHOWSPREFIX = "alwaysShowS~ ";
    public static final String SHOWCRITSPREFIX = "showC~ ";
    public static final String ONLYPLAYERSPREFIX = "onlyP~ ";
    public static final String CANCELIMPPREFIX = "cancelImp~ ";

    public static final String MODPARENT = "kopamed";

    static Minecraft mc;
    
    @EventHandler
    @SuppressWarnings("unused")
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new Command());
        mc = Minecraft.getMinecraft();
        loadConfig();

        addLaunch();
        System.out.println("init===============================================================================================================================");
    }

    private void addLaunch() {
        ex.execute(() -> {
            try {
                LaunchTracker.registerLaunch();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    static String getConfigAsString() {
        return CRITMPREFIX + critMultiplier + "\n" +
                SHARPMPREFIX + sharpnessMultiplier + "\n" +
                SHOWSPREFIX + showSharpness + "\n" +
                ALWAYSSHOWSPREFIX + allwaysShowSharpness + "\n" +
                SHOWCRITSPREFIX + showCriticals + "\n" +
                ONLYPLAYERSPREFIX + onlyPlayers + "\n" +
                CANCELIMPPREFIX + cancel_imp;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerHit(AttackEntityEvent e){
        Entity target = e.target, player = e.entity;

        if(onlyPlayers && !(target instanceof EntityPlayer))
            return;

        if((player.getDistanceToEntity(target) > 3.0f && !mc.thePlayer.capabilities.isCreativeMode)&& cancel_imp){
            return;
        }

        boolean crit = mc.thePlayer.fallDistance > 0.0f && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null;
        boolean sharpness = e.entityPlayer.getHeldItem() != null && e.entityPlayer.getHeldItem().isItemEnchanted() && e.entityPlayer.getHeldItem().getEnchantmentTagList().toString().contains("id:16s");

        if(showSharpness && (allwaysShowSharpness || sharpness)){
            for(int i = 0; i < (sharpness ? sharpnessMultiplier - 1 : sharpnessMultiplier); i++){
                if((player.getDistanceToEntity(target) > 3.0f && !mc.thePlayer.capabilities.isCreativeMode) && impNewParticle){
                    mc.effectRenderer.emitParticleAtEntity(e.target, EnumParticleTypes.VILLAGER_ANGRY);
                } else {
                    mc.effectRenderer.emitParticleAtEntity(e.target, EnumParticleTypes.CRIT_MAGIC);
                }
            }
        }

        // credits to dewgs for this crit method

        if(crit && showCriticals){
            for(int i = 0; i < critMultiplier - 1; i++){
                if((player.getDistanceToEntity(target) > 3.0f && !mc.thePlayer.capabilities.isCreativeMode) && impNewParticle){
                    mc.effectRenderer.emitParticleAtEntity(e.target, EnumParticleTypes.CLOUD);
                } else {
                    mc.effectRenderer.emitParticleAtEntity(e.target, EnumParticleTypes.CRIT);
                }
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onTick(TickEvent.ClientTickEvent e) {
        if (ParticleModGui.shown) {
            ParticleModGui.shown = false;
            mc.displayGuiScreen(new ParticleModGui());
        }
    }

    public static void saveConfig(){
        File dir = new File(mc.mcDataDir + File.separator + MODPARENT + File.separator + MODID);
        if(!dir.exists()){
            dir.mkdirs();
        }

        File file = new File(dir, "config");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            FileWriter fw = new FileWriter(file);

            fw.write(getConfigAsString());
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig(){
        File directory = new File(mc.mcDataDir + File.separator + MODPARENT + File.separator + MODID);
        File configFile = new File(directory, "config");
        if(!directory.exists() || !configFile.exists()){
            return;
        }

        try {
            Scanner s = new Scanner(configFile);
            String line;
            while(s.hasNextLine()){
                line = s.nextLine();
                try{
                    if(line.startsWith(CRITMPREFIX)){
                        critMultiplier = Integer.parseInt(line.replace(CRITMPREFIX, ""));
                    } else if (line.startsWith(SHARPMPREFIX)){
                        sharpnessMultiplier = Integer.parseInt(line.replace(SHARPMPREFIX, ""));
                    } else if (line.startsWith(SHOWSPREFIX)){
                        showSharpness = Boolean.parseBoolean(line.replace(SHOWSPREFIX, ""));
                    } else if (line.startsWith(ALWAYSSHOWSPREFIX)){
                        allwaysShowSharpness = Boolean.parseBoolean(line.replace(ALWAYSSHOWSPREFIX, ""));
                    } else if (line.startsWith(SHOWCRITSPREFIX)){
                        showCriticals= Boolean.parseBoolean(line.replace(SHOWCRITSPREFIX, ""));
                    } else if (line.startsWith(ONLYPLAYERSPREFIX)){
                        onlyPlayers = Boolean.parseBoolean(line.replace(ONLYPLAYERSPREFIX, ""));
                    } else if (line.startsWith(CANCELIMPPREFIX)){
                        cancel_imp = Boolean.parseBoolean(line.replace(CANCELIMPPREFIX, ""));
                    }
                } catch (Exception fuck){
                    fuck.printStackTrace();
                }
            }

            s.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
