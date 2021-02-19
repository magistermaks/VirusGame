class Renderer {
  
    private float camX = 0;
    private float camY = 0;
    private float camS = 0;
    private int maxRight;
    
    // textures
    public final PImage spriteGear;
    
    public Renderer( Settings settings ) {
        camS = ((float) height) / settings.world_size;
        maxRight = settings.show_ui ? height : width;
        
        spriteGear = loadImage("gear.png");
    }
    
    public float trueXtoAppX(float x){
        return (x-camX)*camS;
    }
    
    public float trueYtoAppY(float y){
        return (y-camY)*camS;
    }
    
    public float appXtoTrueX(float x){
        return x/camS+camX;
    }
    
    public float appYtoTrueY(float y){
        return y/camS+camY;
    }
    
    public float trueStoAppS(float s){
        return s * camS; 
    }
    
    public void scaledLine(Vec2f a, Vec2f b){
        float x1 = trueXtoAppX(a.x);
        float y1 = trueYtoAppY(a.y);
        float x2 = trueXtoAppX(b.x);
        float y2 = trueYtoAppY(b.y);
        strokeWeight(0.03 * camS);
        line(x1, y1, x2, y2);
    }
    
    public void drawUI(){
        
        editor.drawSelection();
        
        if( settings.show_ui ) {
          
            pushMatrix();
            translate(height,0);
            fill(0);
            noStroke();
            rect(0,0,width-height,height);
            fill(255);
            textFont(font,40);
            textAlign(LEFT);
            text( "FPS: " + (int) Math.floor(frameRate), 25, 60);
            text( "Start: " + framesToTime(frameCount), 25, 100);
            text( "Edit: " + framesToTime(frameCount-world.lastEditFrame), 25, 140);
            textFont(font, 28);
            text("Initial: " + world.initialCount, 340, 50);
            text("Alive: " + world.aliveCount, 340, 75);
            text("Dead: " + world.deadCount, 340, 100);
            text("Shells: " + world.shellCount, 340, 125);
            text("Infected: " + world.infectedCount, 340, 150);
            if( editor.isOpened() ){
                editor.draw();
            }else{
                drawWorldStats();
            }
            popMatrix();
            drawUGObutton( !editor.isOpened() );
        }
          
        if( settings.show_debug ) {
          
            int c = 20;
            
            fill(0);
            textFont(font, 20);
            textAlign(LEFT);
            
            long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
            long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
            long used  = total - free;
            
            text( "FPS: " + (int) Math.floor(frameRate) + ", frame: " + frameCount, 20, c += 20 );
            text( "Graph high: " + graph.getHighest(false) + ", offset: " + graph.offset + " p: " + settings.graph_update_period, 20, c += 20 );
            text( "Selected: " + editor.isOpened() + ", at: " + editor.selx + ", " + editor.sely, 20, c += 20 );
            text( "CamS: " + String.format("%.2f", camS) + ", CamX: " + String.format("%.2f", camX ) + ", CamY: " + String.format("%.2f", camY ), 20, c += 20 );
            text( "Mutability: " + settings.mutability, 20, c += 20 );
            text( "Memory: " + used + "/" + total + " MB, free: " + (int) (((double) free / total) * 100) + "%", 20, c += 20 );
          
        }
        
        drawCredits();
        
    }
    
    public void drawCredits() {
        pushMatrix();
        translate(4, height - 6);
        fill( COLOR_COPYRIGHT_TEXT );
        noStroke();
        textFont(font, 18);
        textAlign(LEFT);
        text("Copyright (C) 2020 Cary Huang & magistermaks", 0, 0);
        popMatrix();
    }

    public void drawGenomeArrows(double dw, double dh){
        float w = (float)dw;
        float h = (float)dh;
        
        fill(255);
        beginShape();
        vertex(-5, 0);
        vertex(-45, -40);
        vertex(-45, 40);
        endShape(CLOSE);
        beginShape();
        vertex(w + 5, 0);
        vertex(w + 45, -40);
        vertex(w + 45, 40);
        endShape(CLOSE);
        noStroke();
        rect(0, -h / 2, w, h);
    }
    
    public void drawWorldStats() {
        fill(255);
        textAlign(LEFT);
        textFont(font, 30);
        text("Foods: " + world.pc.foods.size(), 25, 200);
        text("Wastes: " + world.pc.wastes.size(), 25, 230);
        text("UGOs: " + world.pc.ugos.size(), 25, 260);
        
        text("total: " + world.totalFoodCount, 200, 200);
        text("total: " + world.totalWasteCount, 200, 230);
        text("total: " + world.totalUGOCount, 200, 260);
        
        graph.draw( 10, height - 10 );
    }
    
    public void drawArrow(float dx1, float dx2, float dy1, float dy2){
        float x1 = trueXtoAppX(dx1);
        float y1 = trueYtoAppY(dx2);
        float x2 = trueXtoAppX(dy1);
        float y2 = trueYtoAppY(dy2);
        
        float angle = atan2(y2 - y1, x2 - x1);
        float head_size = 0.3 * camS;
        
        strokeWeight(0.03*camS);
        line(x1, y1, x2, y2);
        float x3 = x2 + head_size * cos(angle + PI * 0.8);
        float y3 = y2 + head_size * sin(angle + PI * 0.8);
        line(x2, y2, x3, y3);
        float x4 = x2 + head_size * cos(angle - PI * 0.8);
        float y4 = y2 + head_size * sin(angle - PI * 0.8);
        line(x2, y2, x4, y4);
    }
  
    public void drawUGObutton(boolean drawUGO){
        fill(80);
        noStroke();
        rect(width - 130, 10, 120, 140);
        fill(255);
        textAlign(CENTER);
        
        if(drawUGO){
            textFont(font, 48);
            text("MAKE", width - 70, 70);
            text("UGO", width - 70, 120);
        }else{
            textFont(font, 36);
            text("CANCEL", width - 70, 95);
        }
    }

}
