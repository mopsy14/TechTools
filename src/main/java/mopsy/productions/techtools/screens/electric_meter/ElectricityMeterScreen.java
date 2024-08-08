package mopsy.productions.techtools.screens.electric_meter;

import com.mojang.blaze3d.systems.RenderSystem;
import mopsy.productions.techtools.blocks.entity.ElectricityMeterEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

import java.awt.geom.Line2D;

import static mopsy.productions.techtools.TechTools.modid;

public class ElectricityMeterScreen extends HandledScreen<ElectricityMeterScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(modid, "textures/gui/electricity_meter.png");

    public ElectricityMeterScreen(ElectricityMeterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth-textRenderer.getWidth(title))/2;
    }
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        //Setup rendering
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        //Render background texture
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);

        //Render UI
        if (client == null || client.world == null) return;
        if (client.world.getBlockEntity(handler.blockPos) instanceof ElectricityMeterEntity entity) {
            entity.extraXOffset = Math.max(0, entity.extraXOffset-(System.nanoTime()-entity.clientLastUpdateTime)/200000000f);
            float[] pXArray = new float[entity.clientSortedValues.length];
            float[] pYArray = new float[entity.clientSortedValues.length];
            //drawCenteredText(matrices, textRenderer, Arrays.toString(entity.clientSortedValues), width / 2, height / 2, 0xFFFFFFFF);
            int width = 125;
            int height = 85;
            float distanceBetweenDots = (float) width / (entity.clientSortedValues.length-1);
            for (int i = 0; i < entity.clientSortedValues.length; i++) {
                if(entity.clientMaxValue!=0) {
                    pYArray[i] = y + 106 - ((float) height / entity.clientMaxValue) * entity.clientSortedValues[i];
                }else{
                    pYArray[i] = y + 106 - (float) height / 2;
                }
                pXArray[i] = x+20+distanceBetweenDots*i+entity.extraXOffset;
                drawDot(matrices, pXArray[i],pYArray[i]);
                if(i!=0)
                    drawLine(matrices,pXArray[i-1],pYArray[i-1],pXArray[i],pYArray[i],1);
            }
        }

        //drawLine(matrices,x+150,y+150,x+150+(int)(Math.sin((firstTime-System.nanoTime())/10000000000f)*100),y+150+(int)(Math.cos((firstTime-System.nanoTime())/10000000000f)*100),1);

        //drawDot(matrices,x+30,y+30);
    }
    private void drawDot(MatrixStack matrices, float x1, float y1){
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        drawDot(matrix4f,bufferBuilder,x1,y1);
        BufferRenderer.drawWithShader(bufferBuilder.end());
    }
    private void drawDot(Matrix4f matrix4f, BufferBuilder bufferBuilder, float x1, float y1){
        bufferBuilder.vertex(matrix4f,x1-1.3f,y1+1.3f,0).next();
        bufferBuilder.vertex(matrix4f,x1+1.3f,y1+1.3f,0).next();
        bufferBuilder.vertex(matrix4f,x1+1.3f,y1-1.3f,0).next();
        bufferBuilder.vertex(matrix4f,x1-1.3f,y1-1.3f,0).next();
    }
    private void drawLine(MatrixStack matrices, float x1, float y1, float x2, float y2, int thickness){
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        if(thickness == 0){
            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
            bufferBuilder.vertex(matrix4f, x1,y1,0).next();
            bufferBuilder.vertex(matrix4f, x2,y2,0).next();
            BufferRenderer.drawWithShader(bufferBuilder.end());
            return;
        }

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        float oneSideThickness = thickness/2f;
        float invSlope = -(x2-x1) / (y2-y1);

        float dX = (float) (Math.cos(Math.atan(invSlope))*oneSideThickness);
        float dY = (float) (Math.sin(Math.atan(invSlope))*oneSideThickness);

        drawQuad(matrix4f,bufferBuilder,x1+dX,y1+dY,x2+dX,y2+dY,x1-dX,y1-dY,x2-dX,y2-dY,1,1,1,1);

        BufferRenderer.drawWithShader(bufferBuilder.end());
    }
    private void drawLine(Matrix4f matrix4f, BufferBuilder bufferBuilder, float x1, float y1, float x2, float y2, int thickness){
        float oneSideThickness = thickness/2f;
        float invSlope = -(x2-x1) / (y2-y1);

        float dX = (float) (Math.cos(Math.atan(invSlope))*oneSideThickness);
        float dY = (float) (Math.sin(Math.atan(invSlope))*oneSideThickness);

        drawQuad(matrix4f,bufferBuilder,x1+dX,y1+dY,x2+dX,y2+dY,x1-dX,y1-dY,x2-dX,y2-dY,1,1,1,1);
    }
    private void drawTriangle(Matrix4f matrix4f, BufferBuilder bufferBuilder, float x1, float y1, float x2, float y2, float x3, float y3, float r, float g, float b, float a){
        float x1s,y1s,x2s,y2s,x3s,y3s;//sorted coordinates
        if(x1<=x2 && x1<=x3){
            if(x2<=x3){
                x1s=x1;y1s=y1;
                x2s=x2;y2s=y2;
                x3s=x3;y3s=y3;
            }else{
                x1s=x1;y1s=y1;
                x2s=x3;y2s=y3;
                x3s=x2;y3s=y2;
            }
        }else if(x2<=x1&&x2<=x3){
            if(x1<=x3){
                x1s=x2;y1s=y2;
                x2s=x1;y2s=y1;
                x3s=x3;y3s=y3;
            }else{
                x1s=x2;y1s=y2;
                x2s=x3;y2s=y3;
                x3s=x1;y3s=y1;
            }
        }else{
            if(x1<=x2){
                x1s=x3;y1s=y3;
                x2s=x1;y2s=y1;
                x3s=x2;y3s=y2;
            }else{
                x1s=x3;y1s=y3;
                x2s=x2;y2s=y2;
                x3s=x1;y3s=y1;
            }
        }

        float direction = (y3s-y1s)/(x3s-x1s);//a
        float height = -direction*x1s+y1s;     //b
        //y=ax+b --> y=direction*x+height
        if(direction*x2s+height>y2s){//Switch point 2 and 3 if point 2 lays below the line between point 1 and 3
            float temp = y2s;
            y2s        = y3s;
            y3s        = temp;
            temp = x2s;
            x2s  = x3s;
            x3s  = temp;
        }

        bufferBuilder.vertex(matrix4f,x1s,y1s,0).color(r,g,b,a).next();
        bufferBuilder.vertex(matrix4f,x2s,y2s,0).color(r,g,b,a).next();
        bufferBuilder.vertex(matrix4f,x3s,y3s,0).color(r,g,b,a).next();
        bufferBuilder.vertex(matrix4f,x3s,y3s,0).color(r,g,b,a).next();
    }
    private void drawQuad(Matrix4f matrix4f, BufferBuilder bufferBuilder, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, float r, float g, float b, float a){
        drawTriangle(matrix4f, bufferBuilder, x1,y1,x2,y2,x3,y3,r,g,b,a);
        if(Line2D.linesIntersect(x4,y4,x1,y1,x2,y2,x3,y3)){
            drawTriangle(matrix4f,bufferBuilder,x4,y4,x2,y2,x3,y3,r,g,b,a);
        } else if (Line2D.linesIntersect(x4,y4,x2,y2,x1,y1,x3,y3)) {
            drawTriangle(matrix4f,bufferBuilder,x4,y4,x1,y1,x3,y3,r,g,b,a);
        } else {
            drawTriangle(matrix4f,bufferBuilder,x4,y4,x1,y1,x2,y2,r,g,b,a);
        }
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)this.titleY, 4210752);
    }
}
