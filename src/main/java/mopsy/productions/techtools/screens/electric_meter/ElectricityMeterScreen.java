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

import java.util.Arrays;

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
            drawCenteredText(matrices, textRenderer, Arrays.toString(entity.clientSortedValues), width / 2, height / 2, 0xFFFFFFFF);
        }

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        drawQuad(matrix4f,bufferBuilder,x+32,y+96,x+21,y+58,x+56,y+83,x+39,y+24,0.5f,0.3f,0.7f,0.3f);
        //drawLine(matrix4f,bufferBuilder,x,y,x+100,y,12);
        //drawLine(matrix4f,bufferBuilder,x+300,y,x+300,y+100,12);
        //drawLine(matrix4f,bufferBuilder,x+300,y+100,x+300,y,12);
        BufferRenderer.drawWithShader(bufferBuilder.end());
    }
    private void drawLine(Matrix4f matrix4f, BufferBuilder bufferBuilder, int x1, int y1, int x2, int y2, int thickness){

    }
    /*private void drawQuad(Matrix4f matrix4f, BufferBuilder bufferBuilder, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, float r, float g, float b, float a){
        bufferBuilder.vertex(matrix4f,x4,y4,0).color(r,g,b,a).next();
        bufferBuilder.vertex(matrix4f,x3,y3,0).color(r,g,b,a).next();
        bufferBuilder.vertex(matrix4f,x2,y2,0).color(r,g,b,a).next();
        bufferBuilder.vertex(matrix4f,x1,y1,0).color(r,g,b,a).next();
    }

     */
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
        drawQuad(matrix4f,bufferBuilder,new FloatVertex(x1,y1),new FloatVertex(x2,y2),new FloatVertex(x3,y3),new FloatVertex(x4,y4),r,g,b,a);
    }
    private void drawQuad(Matrix4f matrix4f, BufferBuilder bufferBuilder, FloatVertex v1, FloatVertex v2, FloatVertex v3, FloatVertex v4, float r, float g, float b, float a){
        /*List<FloatVertex> vertices = new ArrayList<>(4);
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);
        vertices.add(v4);

        vertices.sort(Comparator.comparingDouble((vertex -> vertex.x)));
        FloatVertex vertex1 = vertices.get(0);
        FloatVertex vertex2 = vertices.get(1);
        FloatVertex vertex3 = vertices.get(2);
        FloatVertex vertex4 = vertices.get(3);

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(vertex1.x+" "+vertex1.y);
        System.out.println(vertex2.x+" "+vertex2.y);
        System.out.println(vertex3.x+" "+vertex3.y);
        System.out.println(vertex4.x+" "+vertex4.y);
         */

        drawTriangle(matrix4f, bufferBuilder, v1.x,v1.y,v2.x,v2.y,v3.x,v3.y,r,g,b,a);
        drawTriangle(matrix4f, bufferBuilder, v4.x,v4.y,v2.x,v2.y,v3.x,v3.y,r,g,b,a);
    }
    private class FloatVertex{
        public float x;
        public float y;
        public float z;
        public FloatVertex(float x, float y, float z){
            this.x=x;
            this.y=y;
            this.z=z;
        }
        public FloatVertex(float x, float y){
            this.x=x;
            this.y=y;
            this.z=0.0f;
        }
        public FloatVertex min(FloatVertex floatVertex){
            return new FloatVertex(x-floatVertex.x,y- floatVertex.y,z- floatVertex.z);
        }
        public float getDirection(){
            return y/x;
        }
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)this.titleY, 4210752);
    }
}
