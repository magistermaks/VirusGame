class Editor {
  
    private boolean open = false;
    public Cell ugo;
    public Cell selected;
    public int selx = 0;
    public int sely = 0;
    public float[] arrow = null;
    private EditType type = EditType.DIVINE;
    private int selectedCodon = -1;
    private float offset = 0;
    
    Editor( Settings settings ) {
        ugo = new Cell(-1, -1, CellType.Normal, 0, 1, settings.editor_default);
    }
    
    public void select( int x, int y ) {
        open = true;
        selected = world.getCellAt( x, y );
        selx = x;
        sely = y;
        type = EditType.DIVINE;
        offset = 0;
    }
    
    public void openUGO() {
        open = true;
        selected = ugo;
        offset = 0;
    }
    
    public void close() {
        open = false;
        selected = null;
    }
    
    public boolean isOpened() {
        return open; 
    }
 
    public void draw() {
        boolean isNotUGO = (selected != ugo);
                
        fill(80);
        noStroke();
        rect(10, 160, 530, height - 10);
        rect(540, 160, 200, 270);
        
        fill(255);
        textFont(font, 96);
        textAlign(LEFT);
        
        if( selected != null ) {
          
            text(selected.getCellName(), 25, 255);
        
            if(isNotUGO && (selected.type != CellType.Locked)){
          
                int c = 200;
                textFont(font, 22);
                text("This cell is " + (selected.tampered ? "TAMPERED" : "NATURAL"), 555, c);
                text("Contents:", 555, c += 22);
                text("    total: " + selected.getParticleCount(null), 555, c += 44);
                text("    food: " + selected.getParticleCount(ParticleType.Food), 555, c += 22);
                text("    waste: " + selected.getParticleCount(ParticleType.Waste), 555, c += 22);
                text("    UGOs: " + selected.getParticleCount(ParticleType.UGO), 555, c += 22);
                
                drawBar(COLOR_ENERGY, (float) selected.energy, "Energy", 290);
                drawBar(COLOR_WALL, (float) selected.wall, "Wall health", 360);
                
            }
            
            if( selected.type == CellType.Normal ) {
                drawGenomeAsList(selected.genome);
                
                if(isNotUGO){
                    fill(255);
                    textFont(font, 32);
                    textAlign(LEFT);
                    text("Memory: " + selected.getMemory(), 25, 952);
                }
            }
                        
        }else{
            text("Empty Cell", 25, 255);
        }
        
        drawEditTable();

    }
    
    private void drawEditTable() {
        float x = EDIT_LIST_DIMS[0];
        float y = EDIT_LIST_DIMS[1];
        float w = EDIT_LIST_DIMS[2];
        float h = EDIT_LIST_DIMS[3];
        
        pushMatrix();
        textFont(font, 30);
        textAlign(CENTER);
        translate(x, y);
      
        float buttonWidth = w - MARGIN * 2;
        
        switch( type ) {
        
            case CODON: {
                int buttonCount = Codons.size();
                float buttonHeight = h / max( buttonCount, 8 );
            
                for( int i = 0; i < buttonCount; i ++ ) {
                    drawButton( 
                        Codons.get(i).getColor(), 
                        buttonHeight * i, 
                        buttonWidth, 
                        buttonHeight, 
                        Codons.get( i ).getText()
                    );
                }
            } break;
            
            case CODON_ARGS: {
                if( selectedCodon != -1 ) {
                    CodonArg[] args = selected.genome.codons.get( selectedCodon ).getArgs();
                    float buttonHeight = h / max( args.length, 8 );
            
                    for(int i = 0; i < args.length; i++){
                        drawButton( 
                            args[i].getColor(), 
                            buttonHeight * i, 
                            buttonWidth, 
                            buttonHeight, 
                            args[i].getText() 
                        );
                    }
                }
            } break;
            
            case DIVINE: {
                float buttonHeight = h / max( DIVINE_CONTROLS.length, 8 );
            
                for(int i = 0; i < DIVINE_CONTROLS.length; i++){
                    drawButton( 
                        isDivineControlAvailable(i) ? COLOR_DIVINE_CONTROL : COLOR_DIVINE_DISABLED, 
                        buttonHeight * i,
                        buttonWidth, 
                        buttonHeight, 
                        DIVINE_CONTROLS[i] 
                    );
                }
            } break;

        }
        
        popMatrix();
    }
    
    private void drawButton( color c, float x, float w, float h, String t ) {
        fill( c );
        rect( MARGIN, x + MARGIN, w, h - MARGIN * 2);
        fill( 255 );
        text( t, w * 0.5 + MARGIN * 2, x + h * 0.5f + 11 );
    }
    
    private void drawGenomeAsList(Genome genome){
        float x = GENOME_LIST_DIMS[0];
        float y = GENOME_LIST_DIMS[1];
        float w = GENOME_LIST_DIMS[2];
        float h = GENOME_LIST_DIMS[3];
        
        int codonsCount = genome.codons.size();
        float buttonWidth = w * 0.5 - MARGIN;
        
        textFont(font, 28);
        textAlign(CENTER);
        pushMatrix();
        translate(x, y + 40 + offset);
                
        pushMatrix();
        float transY = GENOM_LIST_ENTRY_HEIGHT * ((float) genome.appRO + 0.5f);
        translate(0, transY);
        
        if( transY + offset > -20 && transY + offset < h ) {
            if(editor.selected != editor.ugo && genome.rotateOn >= 0 && genome.rotateOn < codonsCount){
                renderer.drawGenomeArrows(w, GENOM_LIST_ENTRY_HEIGHT);
            }
        }
        popMatrix();
        
        for(int i = 0; i < codonsCount; i++){
            float buttonPos = GENOM_LIST_ENTRY_HEIGHT * i;
            Codon codon = genome.codons.get(i);
            
            float offsetPos = buttonPos + offset;
            if( offsetPos < -40 || offsetPos > h ) {
                continue; 
            }

            drawCodon( buttonPos, buttonWidth, i == selectedCodon, EditType.CODON, codon );
            drawCodon( buttonPos, buttonWidth, i == selectedCodon, EditType.CODON_ARGS, codon );
            
            // draw settings gear
            if( codon.isComplex() ) {
                if( i == selectedCodon && this.type == EditType.MODIFY ) {
                    tint(255, (0.5 + 0.5 * sin(frameCount * 0.25)) * 140 + 100); 
                }
              
                image(renderer.spriteGear, buttonWidth * 2 - GENOM_LIST_ENTRY_HEIGHT, buttonPos, GENOM_LIST_ENTRY_HEIGHT, GENOM_LIST_ENTRY_HEIGHT); 
                noTint();
            }
            
        }
        popMatrix();
        
        fill(80);
        rect(x, y, w, 40);
        rect(x, y + h + 40, w, 40);
        
        if(selected == ugo){
            fill(255);
            textFont(font, 60);
            textAlign(LEFT);
            text("-", x + MARGIN * 8, y + h + 80);
            textAlign(RIGHT);
            text("+", x + w - MARGIN * 8, y + h + 80);
        }
        
    }
    
    private void drawCodon( float y, float w, boolean selected, EditType type, Codon codon ) {
        float x;
        color c;
        String t;
        
        if( type == EditType.CODON ) {
            c = codon.getBaseColor();
            t = codon.getBaseText();
            x = 0;
        }else{
            c = codon.getArgColor();
            t = codon.getArgText();
            x = w - MARGIN;
        }
        
        fill(0);
        rect(x + MARGIN, y + MARGIN, w, GENOM_LIST_ENTRY_HEIGHT - MARGIN * 2);
        
        if( codon.hasSubstance() ) {
            fill(c);
            float trueW = w * codon.health;
            float trueX = (( type == EditType.CODON ) ? w * (1 - codon.health) : 0) + x + MARGIN;
            rect (trueX, y + MARGIN, trueW, GENOM_LIST_ENTRY_HEIGHT - MARGIN * 2 );
        }
        
        fill(255);
        text(t, x + w * 0.5, y + GENOM_LIST_ENTRY_HEIGHT / 2 + 11);
       
        if( selected && type == this.type ){
            fill(255, 255, 255, (0.5 + 0.5 * sin(frameCount * 0.25)) * 140);
            rect(x + MARGIN, y + MARGIN, w, GENOM_LIST_ENTRY_HEIGHT - MARGIN * 2);
        }
    }
    
    private void drawBar(color col, float value, String s, float y){
        fill(150);
        rect(25, y, 500, 60);
        fill(col);
        rect(25, y, value * 500, 60);
        fill(0);
        textFont(font ,48);
        textAlign(LEFT);
        text(s + ": " + nf(value*100, 0, 1) + "%", 35, y + 47);
    }
    
    private void drawSelection() {
        if(editor.arrow != null){
            if(euclidLength(editor.arrow) > settings.min_length_to_produce){
                stroke(0);
            }else{
                stroke(150);
            }
            renderer.drawArrow(editor.arrow[0], editor.arrow[1], editor.arrow[2], editor.arrow[3]);
        }
      
        if( open && selected != ugo ) {
            pushMatrix();
            translate( (float) renderer.trueXtoAppX(selx), (float) renderer.trueYtoAppY(sely) );
            scale( (float) (renderer.camS / BIG_FACTOR) );
            noFill();
            stroke(0,255,255,155 + (int) (100 * Math.sin(frameCount / 10.f)));
            strokeWeight(4);
            rect(0, 0, BIG_FACTOR, BIG_FACTOR);
            popMatrix();
        }
    }
  
    public void checkInput() {
        if(open) {
            checkEditListClick();
            if( selected != null && selected.hasGenome() ) checkGenomeListClick();
            if( mouseX > width - 160 && mouseY < 160 ) close();
        }else{
            if( mouseX > width - 160 && mouseY < 160 ) openUGO();
        }
    }
    
    void checkGenomeListClick() {
        double rmx = ((mouseX - height) - GENOME_LIST_DIMS[0]) / GENOME_LIST_DIMS[2];
        double rmy = (mouseY - offset - GENOME_LIST_DIMS[1] - 40) / GENOME_LIST_DIMS[3];
    
        if(rmx >= 0 && rmx < 1 && rmy >= 0){
            if( rmy < 1 ) {
                int choice = (int) (rmy * (GENOME_LIST_DIMS[3] / GENOM_LIST_ENTRY_HEIGHT));
                
                // check if the choice is valid
                if( choice < selected.genome.codons.size() ) {
                  
                    // check if clicked on the settings gear
                    if( rmx > 0.88 && selected.genome.codons.get(choice).isComplex() ) {
                        type = EditType.MODIFY;
                    }else{
                        type = rmx < 0.5f ? EditType.CODON : EditType.CODON_ARGS; 
                    }
                  
                    selectedCodon = choice;
                }
            }else if( selected == ugo && mouseY > GENOME_LIST_DIMS[1] + 40 + GENOME_LIST_DIMS[3] ){
                if( rmx < 0.5 ) {
                    selected.genome.shorten();
                }else{
                    selected.genome.lengthen();
                } //<>//
            }
        }
    }

    void checkEditListClick() {
        double rmx = ((mouseX - height) - EDIT_LIST_DIMS[0]) / EDIT_LIST_DIMS[2];
        double rmy = (mouseY - EDIT_LIST_DIMS[1]) / EDIT_LIST_DIMS[3];
    
        if(rmx >= 0 && rmx < 1 && rmy >= 0 && rmy < 1) {
          
            int count = getOptionCount();
            int choice = (int) (rmy * max( count, 8 ));
            
            if( choice >= count ) {
                return; 
            }
            
            switch( type ) {
              
                case DIVINE:
                    divineIntervention( choice );
                    break;
                  
                case CODON:
                    if( selectedCodon != -1 ) {
                        selected.genome.codons.get(selectedCodon).setBase( Codons.get(choice) );
                    }
                    break;
                    
                case CODON_ARGS:
                    if( selectedCodon != -1 ) {
                        Codon c = selected.genome.codons.get(selectedCodon);
                        c.setArg( c.getArgs()[choice] );
                    }
                    break;
                    
                case MODIFY:
                    // TODO: modify gene settings
                    break;
            }
           
            if( selected != null && selected != ugo ) {
                world.lastEditFrame = frameCount;
                selected.tamper();
            }   
            
        }else{
            type = EditType.DIVINE;
            selectedCodon = -1;
        }
    }
    
    private int getOptionCount() {
        if( type == EditType.DIVINE ) return DIVINE_CONTROLS.length;
        if( type == EditType.CODON ) return Codons.size();
        if( type == EditType.CODON_ARGS ) return selectedCodon == -1 ? 0 : selected.genome.codons.get(selectedCodon).getArgs().length;
        return 0;
    }
    
    public boolean handleScroll( MouseEvent event ) {
        if( selected.type == CellType.Normal ) {
            double rmx = ((mouseX - height) - GENOME_LIST_DIMS[0]) / GENOME_LIST_DIMS[2];
            double rmy = (mouseY - GENOME_LIST_DIMS[1] - 40) / GENOME_LIST_DIMS[3];
    
            if(rmx >= 0 && rmx < 1 && rmy >= 0 && rmy <= 1) {
                offset -= event.getCount() * 24;
                return true;
            }
        }
        
        return false;
    }
    
    public void divineIntervention( int id ) {
      
        if( !isDivineControlAvailable(id) ) return;
        
        switch( id ) {
            case 0: // Remove
                world.setCellAt( selx, sely, null );
                break;
            
            case 1: // Revive
                world.aliveCount ++;
                world.setCellAt( selx, sely, new Cell( selx, sely, CellType.Normal, 0, 1, settings.genome ) );
                break;
            
            case 2: // Heal
                selected.healWall();
                break;
            
            case 3: // Energize
                selected.giveEnergy();
                break;
            
            case 4: // Make Wall
                world.setCellAt( selx, sely, new Cell( selx, sely, CellType.Locked, 0, 1, settings.genome ) );
                break;
            
            case 5: // Make Shell
                world.shellCount ++;
                world.setCellAt( selx, sely, new Cell( selx, sely, CellType.Shell, 0, 1, settings.genome ) );
                break;
        }
        
        editor.select( selx, sely );
        world.lastEditFrame = frameCount;
      
    }
    
    public boolean isDivineControlAvailable( int id ) {
        // For meaning of the specific id see 'DIVINE_CONTROLS' defined in 'Virus',
        // where id is the offset into that array.
        
        if( selected == ugo || !open ) return false;
        if( id == 0 ) return (selected != null);
        if( id == 2 || id == 3 ) return (selected != null && selected.type != CellType.Locked);
        if( id == 1 ) return (selected == null || selected.type != CellType.Normal);
        if( id == 4 ) return (selected == null || selected.type != CellType.Locked);
        if( id == 5 ) return (selected == null || selected.type != CellType.Shell);
        return true;
    }
    
    void produce(){
        if(world.getCellAtUnscaled(arrow[0], arrow[1]) == null){
            UGO u = new UGO(arrow, ugo.genome.asDNA());
            u.markDivine();
            world.addParticle(u);
            world.lastEditFrame = frameCount;
        }
    }
  
  
}

public enum EditType {
    CODON,
    CODON_ARGS,
    DIVINE,
    MODIFY;
}
