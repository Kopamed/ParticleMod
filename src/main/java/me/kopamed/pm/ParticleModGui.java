package me.kopamed.pm;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.awt.*;

public class ParticleModGui extends GuiScreen {
    public static boolean shown = false;
    private double textScale = 1.6;

    private final int marginMiddle = 10;
    private final int marginY = 16;
    private final int textY = marginY;
    private int startY;
    private final int checkwidth = 10;


    private GuiSlider sharpness_bar;
    private GuiSlider critical_bar;
    private GuiCheckBox sharp;
    private GuiCheckBox alwaysSharp;
    private GuiCheckBox crits;
    private GuiCheckBox onlyPlayers;
    private GuiCheckBox cancelimp;
    private GuiCheckBox newParticlesForImp;

    private Color bgColor = new Color(0x102d2d2e);

    @Override
    public void initGui(){
        ScaledResolution sr = new ScaledResolution(mc);

        this.startY = this.textY + (int)(fontRendererObj.FONT_HEIGHT / textScale) + this.marginY;

        //     ParticleMod by Kopamed
        //  []crits      |----criticals----|
        //  []sharp      |----sharpness----|
        //  []alwayssarp`   []onplyPlayers
        //  []cancelimp  []newparticlesforimp

        int screenhalf = this.width/2;
        int startLeft = screenhalf - marginMiddle - 200, startRight = screenhalf + marginMiddle;
        int currentY = startY;

        this.crits = new GuiCheckBox(1, startLeft, currentY, "Crits", ParticleMod.showCriticals);
        crits.setWidth(checkwidth);
        this.critical_bar = new GuiSlider(2, startRight, currentY, 200, 20, "Intensity: ", "x", 1, 20, ParticleMod.critMultiplier, false, true);

        currentY += 20 + marginY;

        this.sharp = new GuiCheckBox(3, startLeft, currentY, "Sharpness", ParticleMod.showSharpness);
        this.sharpness_bar = new GuiSlider(4, startRight, currentY, 200, 20, "Intensity: ", "x", 1, 20, ParticleMod.sharpnessMultiplier, false, true);

        currentY += 20 + marginY;

        this.alwaysSharp = new GuiCheckBox(5, startLeft, currentY, "Always affect sharp", ParticleMod.allwaysShowSharpness);
        this.onlyPlayers = new GuiCheckBox(6, startRight, currentY, "Only players", ParticleMod.onlyPlayers);

        currentY += 20 + marginY;

        this.cancelimp = new GuiCheckBox(7, startLeft, currentY, "Cancel Impossible", ParticleMod.cancel_imp);
        this.newParticlesForImp = new GuiCheckBox(8, startRight, currentY, "New Particles for impossibles", ParticleMod.impNewParticle);

        this.buttonList.add(this.crits);
        this.buttonList.add(this.critical_bar);
        this.buttonList.add(this.sharp);
        this.buttonList.add(this.sharpness_bar);
        this.buttonList.add(alwaysSharp);
        this.buttonList.add(onlyPlayers);
        this.buttonList.add(cancelimp);
        this.buttonList.add(newParticlesForImp);


        //GL11.glDrawPixels(20, 20, 20, 6, 6L);
    }

    @Override
    public void actionPerformed(GuiButton button){
        switch (button.id){
            case 1:
                ParticleMod.showCriticals = ((GuiCheckBox) button).isChecked();
                break;

            case 3:
                ParticleMod.showSharpness = ((GuiCheckBox) button).isChecked();
                break;

            case 5:
                ParticleMod.allwaysShowSharpness = ((GuiCheckBox) button).isChecked();
                break;

            case 6:
                ParticleMod.onlyPlayers = ((GuiCheckBox) button).isChecked();
                break;

            case 7:
                ParticleMod.cancel_imp = ((GuiCheckBox) button).isChecked();
                break;

            case 8:
                ParticleMod.impNewParticle = ((GuiCheckBox) button).isChecked();
                break;
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        Gui.drawRect(0, 0, this.width, this.height, 0x992d2d2e);
        this.drawCenteredString(fontRendererObj, "ParticleMod by Kopamed", this.width/2, textY,0xff81D8D0);

        ParticleMod.critMultiplier = this.critical_bar.getValueInt();
        ParticleMod.sharpnessMultiplier = this.sharpness_bar.getValueInt();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        ParticleMod.saveConfig();
    }
}
