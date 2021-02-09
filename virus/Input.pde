
boolean isPressed = false;
boolean doubleClick = false; // not realy double click - find better name
boolean wasMouseDown = false;
float clickWorldX = -1;
float clickWorldY = -1;
int windowSizeX = 0; // used for resize detection
int windowSizeY = 0;

void keyPressed() {
  
    // disable/enble GUI
    if( key == 'x' || key == 'X' ) {
        settings.show_ui = !settings.show_ui;
        renderer.maxRight = settings.show_ui ? height : width;
    }
    
    // disable/enble tampered cell highlighting
    if( key == 'z' || key == 'Z' ) {
        settings.show_tampered = !settings.show_tampered;
    }
    
    // disable/enble debug screen
    if( key == '\t' ) {
        settings.show_debug = !settings.show_debug;
    }
    
    if( key == ' ' ) {
        renderer.camX = 0;
        renderer.camY = 0;
        renderer.camS = ((float) height) / settings.world_size; 
    }
    
    // make ESC key close the editor, and not the entire game
    if( key == ESC ) {
        editor.close();
        key = 0;
    }
  
}

void mouseWheel(MouseEvent event) {
    if( !editor.isOpened() || !editor.handleScroll( event ) ) { 
        float thisZoomF = event.getCount() == 1 ? 1/1.05 : 1.05;
        float worldX = mouseX/renderer.camS+renderer.camX;
        float worldY = mouseY/renderer.camS+renderer.camY;
        renderer.camX = (renderer.camX-worldX)/thisZoomF+worldX;
        renderer.camY = (renderer.camY-worldY)/thisZoomF+worldY;
        renderer.camS *= thisZoomF;
    }
}

void windowResized() {
    graph.resize( width - height - 20, height - 300 );
}

void inputCheck(){
    if( width != windowSizeX || height != windowSizeY ) {
         windowSizeX = width;
         windowSizeY = height;
         windowResized();
    }
  
    if (mousePressed) {
        editor.arrow = null;
        if(!wasMouseDown) {
            if(mouseX < renderer.maxRight){
                editor.selectedCodon = -1;
                clickWorldX = renderer.appXtoTrueX(mouseX);
                clickWorldY = renderer.appYtoTrueY(mouseY);
                isPressed = true;
            }else{
                editor.checkInput();
                isPressed = false;
            }
            doubleClick = true;
        }else if(isPressed){
          
            float newCX = renderer.appXtoTrueX(mouseX);
            float newCY = renderer.appYtoTrueY(mouseY);
            
            if(newCX != clickWorldX || newCY != clickWorldY){
                doubleClick = false;
            }
            if(editor.selected == editor.ugo){
                stroke(0, 0, 0);
                editor.arrow = new float[]{clickWorldX,clickWorldY,newCX,newCY};
            }else{
                renderer.camX -= (newCX-clickWorldX);
                renderer.camY -= (newCY-clickWorldY);
            }
        }
        
    }else{
        if(wasMouseDown) {
            if(editor.selected == editor.ugo && editor.arrow != null){
                if(euclidLength(editor.arrow) > settings.min_length_to_produce){
                    editor.produce();
                }
            }
            if(doubleClick && isPressed){
                if(editor.selected != editor.ugo){
                    editor.close();
                }
                if( world.isCellValid( clickWorldX, clickWorldY ) ) {
                    editor.select( (int) clickWorldX, (int) clickWorldY );
                }
            }
        }
        clickWorldX = -1;
        clickWorldY = -1;
        editor.arrow = null;
    }
    wasMouseDown = mousePressed;
}
