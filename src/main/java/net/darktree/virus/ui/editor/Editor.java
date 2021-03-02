package net.darktree.virus.ui.editor;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.cell.*;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.codon.CodonBases;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.codon.arg.ComplexCodonArg;
import net.darktree.virus.genome.CellGenome;
import net.darktree.virus.genome.GenomeBase;
import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.ParticleType;
import net.darktree.virus.particle.VirusParticle;
import net.darktree.virus.ui.Screen;
import net.darktree.virus.ui.sound.Sounds;
import net.darktree.virus.util.DrawContext;
import net.darktree.virus.world.World;
import processing.event.MouseEvent;

public class Editor implements DrawContext {

    private boolean open = false;
    public EditorCell virus;
    public Cell selected;
    public int selX = 0;
    public int selY = 0;
    public Arrow arrow = null;
    private EditType type = EditType.DIVINE;
    public int selectedCodon = -1;
    private float offset = 0;

    public Editor() {
        virus = new EditorCell(null);
    }

    public void select( float x, float y ) {

        Particle particle = Main.applet.world.getParticleAround( x, y, 0.2f, ParticleType.VIRUS );
        open = true;
        type = EditType.DIVINE;

        if( particle == null ) {
            selected = Main.applet.world.getCellAt(x, y);
            selX = (int) x;
            selY = (int) y;
            offset = 0;
        }else{
            selected = new EditorCell(particle);
        }

    }

    public void openUGO() {
        open = true;
        selected = virus;
        offset = 0;
    }

    public void close() {
        open = false;
        selected = null;
    }

    public boolean isOpened() {
        return open;
    }

    public String getDebugString() {
        return "Selected: " + open + " (c: " + selectedCodon + "), at: " + selX + ", " + selY + ", gs: " + offset;
    }

    public void draw() {
        World world = Main.applet.world;
        int width = Main.applet.width;
        int height = Main.applet.height;

        push();
        translate(height, 0);
        fill(0);
        noStroke();
        rect(0,0,width - height, height);
        fill(255);
        textSize(40);
        textAlign(LEFT);

        text( "FPS: " + (int) Math.floor(Main.applet.frameRate), 25, 60 );
        text( "Start: " + Main.applet.world.getTickCount() / 60 + "s", 25, 100 );
        text( "Edit: " + (Main.applet.world.getTickCount() - Main.applet.world.lastEditTick) / 60 + "s", 25, 140 );
        textSize(28);
        text( "Initial: " + world.initialCount, 340, 50 );
        text( "Alive: " + world.aliveCount, 340, 75 );
        text( "Dead: " + world.deadCount, 340, 100 );
        text( "Shells: " + world.shellCount, 340, 125 );
        text( "Infected: " + world.infectedCount, 340, 150 );

        if( open ){
            drawCellPanel();
        }else{
            drawWorldStats(world);
        }

        pop();
        drawMainButton();

    }

    private void drawWorldStats(World world) {
        fill(255);
        textAlign(LEFT);
        textSize(30);
        text("Foods: " + world.pc.foods.size(), 25, 200);
        text("Wastes: " + world.pc.wastes.size(), 25, 230);
        text("UGOs: " + world.pc.viruses.size(), 25, 260);

        text("total: " + world.totalFoodCount, 200, 200);
        text("total: " + world.totalWasteCount, 200, 230);
        text("total: " + world.totalVirusCount, 200, 260);

        Main.applet.graph.draw( 10, Main.applet.height - 10 );
    }

    public void drawMainButton(){
        int width = Main.applet.width;

        fill(80);
        noStroke();
        rect(width - 130, 10, 120, 140);
        fill(255);
        textAlign(CENTER);

        if(!open){
            textSize(48);
            text("MAKE", width - 70, 70);
            text("UGO", width - 70, 120);
        }else{
            textSize(36);
            text("CANCEL", width - 70, 95);
        }
    }

    private void drawCellPanel() {
        boolean isNotUGO = (selected != virus);

        fill(80);
        noStroke();
        rect(10, 160, 530, Main.applet.height - 10);
        rect(540, 160, 200, 270);

        fill(255);
        textSize(96);
        textAlign(LEFT);

        if( selected != null ) {

            text(selected.getCellName(), 25, 255);

            if(isNotUGO && selected instanceof ContainerCell){

                ContainerCell cell = (ContainerCell) selected;

                int c = 200;
                textSize(22);
                text("This cell is " + (selected instanceof NormalCell && ((NormalCell) selected).tampered ? "TAMPERED" : "NATURAL"), 555, c);
                text("Contents:", 555, c += 22);
                text("    total: " + cell.getParticleCount(null), 555, c += 44);
                text("    food: " + cell.getParticleCount(ParticleType.FOOD), 555, c += 22);
                text("    waste: " + cell.getParticleCount(ParticleType.WASTE), 555, c += 22);
                text("    UGOs: " + cell.getParticleCount(ParticleType.VIRUS), 555, c + 22);

                if( selected instanceof ShellCell ) {
                    ShellCell shellCell = (ShellCell) selected;

                    drawBar(Const.COLOR_ENERGY, shellCell.energy, "Energy", 290);
                    drawBar(Const.COLOR_WALL, shellCell.wall, "Wall health", 360);
                }
            }

            if( selected instanceof GenomeCell ) {
                drawGenomeAsList( ((GenomeCell) selected).getGenome() );

                if(isNotUGO && selected instanceof NormalCell ){
                    NormalCell normalCell = (NormalCell) selected;

                    fill(255);
                    textSize(32);
                    textAlign(LEFT);
                    text("Memory: " + normalCell.getMemory(), 25, 952);
                }
            }

        }else{
            text("Empty Cell", 25, 255);
        }

        drawEditTable();

    }

    private void drawEditTable() {
        // TODO: remove this
        float x = Const.EDIT_LIST_DIMS[0];
        float y = Const.EDIT_LIST_DIMS[1];
        float w = Const.EDIT_LIST_DIMS[2];
        float h = Const.EDIT_LIST_DIMS[3];

        push();
        textSize(30);
        textAlign(CENTER);
        translate(x, y);

        float buttonWidth = w - Const.MARGIN * 2;

        switch( type ) {

            case CODON: {
                int buttonCount = CodonBases.size();
                float buttonHeight = h / Main.max( buttonCount, 8 );

                for( int i = 0; i < buttonCount; i ++ ) {
                    drawButton(
                            CodonBases.get(i).getColor(),
                            buttonHeight * i,
                            buttonWidth,
                            buttonHeight,
                            CodonBases.get( i ).getText()
                    );
                }
            } break;

            case CODON_ARGS: {
                if( selectedCodon != -1 && selected instanceof GenomeCell ) {
                    GenomeCell cell = (GenomeCell) selected;
                    CodonArg[] args = cell.getGenome().codons.get( selectedCodon ).getArgs();
                    float buttonHeight = h / Main.max( args.length, 8 );

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
                if( selected instanceof EditorCell && ((EditorCell) selected).getParticle() != null ) break;
                float buttonHeight = h / Main.max( Const.DIVINE_CONTROLS.length, 8 );

                for(int i = 0; i < Const.DIVINE_CONTROLS.length; i++){
                    drawButton(
                            isDivineControlAvailable(i) ? Const.COLOR_DIVINE_CONTROL : Const.COLOR_DIVINE_DISABLED,
                            buttonHeight * i,
                            buttonWidth,
                            buttonHeight,
                            Const.DIVINE_CONTROLS[i]
                    );
                }
            } break;

            case MODIFY: {
                if( selectedCodon != -1 && selected instanceof GenomeCell ) {
                    GenomeCell cell = (GenomeCell) selected;
                    CodonArg arg = cell.getGenome().codons.get( selectedCodon ).getArg();
                    if( arg instanceof ComplexCodonArg) {

                        ComplexCodonArg complexArg = (ComplexCodonArg) arg;

                        String[] options = complexArg.getOptions();
                        float buttonHeight = h / Main.max( options.length, 8 );

                        for(int i = 0; i < options.length; i++){
                            float pos = buttonHeight * i;

                            drawButton(
                                    Const.COLOR_CODON_OPTION,
                                    pos,
                                    buttonWidth,
                                    buttonHeight,
                                    options[i] + ": " + complexArg.get(i)
                            );

                            textAlign(CENTER, CENTER);
                            float offset = buttonHeight / 2;
                            text( "-", offset / 2, pos + offset );
                            text( "+", buttonWidth - offset / 2, pos + offset );
                            textAlign(CENTER);
                        }
                    }
                }
            }
        }

        pop();
    }

    private void drawButton( int c, float x, float w, float h, String t ) {
        fill( c );
        rect( Const.MARGIN, x + Const.MARGIN, w, h - Const.MARGIN * 2);
        fill( 255 );
        text( t, w * 0.5f + Const.MARGIN * 2, x + h * 0.5f + 11 );
    }

    private void drawGenomeAsList( GenomeBase genome ) {

        // TODO: remove this
        float x = Const.GENOME_LIST_DIMS[0];
        float y = Const.GENOME_LIST_DIMS[1];
        float w = Const.GENOME_LIST_DIMS[2];
        float h = Const.GENOME_LIST_DIMS[3];

        int codonsCount = genome.codons.size();
        float buttonWidth = w * 0.5f - Const.MARGIN;

        textSize(28);
        textAlign(CENTER);
        push();
        translate(x, y + 40 + offset);

        if( genome instanceof CellGenome ) {
            CellGenome cellGenome = (CellGenome) genome;
            float transY = Const.GENOME_LIST_ENTRY_HEIGHT * cellGenome.getInterpolatedOffset();

            if (transY + offset > -20 && transY + offset < h) {
                if (selected != virus && cellGenome.selected >= 0 && cellGenome.selected < codonsCount) {
                    push();
                    translate(0, transY);
                    drawGenomeArrows(w, Const.GENOME_LIST_ENTRY_HEIGHT);
                    pop();
                }
            }
        }

        for(int i = 0; i < codonsCount; i++){
            float buttonPos = Const.GENOME_LIST_ENTRY_HEIGHT * i;
            Codon codon = genome.codons.get(i);

            float offsetPos = buttonPos + offset;
            if( offsetPos < -40 || offsetPos > h ) {
                continue;
            }

            drawCodon( buttonPos, buttonWidth, i == selectedCodon, EditType.CODON, codon );
            drawCodon( buttonPos, buttonWidth, i == selectedCodon, EditType.CODON_ARGS, codon );

            // draw settings arrow
            if( codon.isComplex() ) {
                fill(255, 255, 255, i == selectedCodon && type == EditType.MODIFY ? 255 : 100);
                float rowWidth = 2 * buttonWidth, bh = Const.GENOME_LIST_ENTRY_HEIGHT;

                triangle(
                        rowWidth - bh / 1.5f,
                        buttonPos + Const.MARGIN * 2,
                        rowWidth - Const.MARGIN,
                        buttonPos + bh / 2,
                        rowWidth - bh / 1.5f,
                        buttonPos + bh - Const.MARGIN * 2
                );
            }

        }
        pop();

        fill(80);
        rect(x, y, w, 40);
        rect(x, y + h + 40, w, 40);

        if(selected == virus){
            fill(255);
            textSize(60);
            textAlign(LEFT);
            text("-", x + Const.MARGIN * 8, y + h + 80);
            textAlign(RIGHT);
            text("+", x + w - Const.MARGIN * 8, y + h + 80);
        }

    }

    private void drawGenomeArrows(double dw, double dh){
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

    private void drawCodon(float y, float w, boolean selected, EditType type, Codon codon ) {
        float x;
        int c;
        String t;

        if( type == EditType.CODON ) {
            c = codon.getBaseColor();
            t = codon.getBaseText();
            x = 0;
        }else{
            c = codon.getArgColor();
            t = codon.getArgText();
            x = w - Const.MARGIN;
        }

        fill(0);
        rect(x + Const.MARGIN, y + Const.MARGIN, w, Const.GENOME_LIST_ENTRY_HEIGHT - Const.MARGIN * 2);

        if( codon.hasSubstance() ) {
            fill(c);
            float trueW = w * codon.health;
            float trueX = (( type == EditType.CODON ) ? w * (1 - codon.health) : 0) + x + Const.MARGIN;
            rect (trueX, y + Const.MARGIN, trueW, Const.GENOME_LIST_ENTRY_HEIGHT - Const.MARGIN * 2 );
        }

        fill(255);
        text(t, x + w * 0.5f, y + Const.GENOME_LIST_ENTRY_HEIGHT / 2 + 11);

        if( selected && type == this.type ){
            fill(255, 255, 255, (0.5f + 0.5f * Main.sin(getFrameCount() * 0.25f)) * 140);
            rect(x + Const.MARGIN, y + Const.MARGIN, w, Const.GENOME_LIST_ENTRY_HEIGHT - Const.MARGIN * 2);
        }
    }

    private void drawBar(int col, float value, String s, float y){
        fill(150);
        rect(25, y, 500, 60);
        fill(col);
        rect(25, y, value * 500, 60);
        fill(0);
        textSize(48);
        textAlign(LEFT);
        text(s + ": " + Main.nf(value*100, 0, 1) + "%", 35, y + 47);
    }

    public void drawSelection(Screen screen) {
        if(arrow != null){
            arrow.draw(screen);
        }

        if( open && selected instanceof EditorCell ) {
            push();
            Particle particle = ((EditorCell) selected).getParticle();
            if( particle != null ) {
                float x = particle.pos.x, y = particle.pos.y;
                translate(screen.trueXtoAppX(x), screen.trueYtoAppY(y));
                scale(screen.camS / Const.BIG_FACTOR);
                noFill();
                stroke(0, 255, 255, 155 + (int) (100 * Math.sin(getFrameCount() / 10.f)));
                strokeWeight(4);
                ellipse(0f, 0f, Const.CODON_DIST_UGO * 3.5f, Const.CODON_DIST_UGO * 3.5f);
            }
            pop();
        }else {
            if (open && selected != virus) {
                push();
                translate(screen.trueXtoAppX(selX), screen.trueYtoAppY(selY));
                scale(screen.camS / Const.BIG_FACTOR);
                noFill();
                stroke(0, 255, 255, 155 + (int) (100 * Math.sin(getFrameCount() / 10.f)));
                strokeWeight(4);
                rect(0, 0, Const.BIG_FACTOR, Const.BIG_FACTOR);
                pop();
            }
        }
    }

    public void checkInput() {
        if(open) {
            checkEditListClick();
            if( selected != null && selected instanceof GenomeCell ) checkGenomeListClick();
        }

        if( Main.applet.mouseX > Main.applet.width - 160 && Main.applet.mouseY < 160 ) {
            if( open ) close(); else openUGO();
            Sounds.CLICK.play();
        }
    }

    public void checkGenomeListClick() {
        double rmx = ((Main.applet.mouseX - Main.applet.height) - Const.GENOME_LIST_DIMS[0]) / Const.GENOME_LIST_DIMS[2];
        double rmy = (Main.applet.mouseY - Const.GENOME_LIST_DIMS[1] - 40) / Const.GENOME_LIST_DIMS[3];

        if(rmx >= 0 && rmx < 1 && rmy >= 0 && selected instanceof GenomeCell ) {
            GenomeCell cell = (GenomeCell) selected;

            if( rmy < 1 ) {
                int choice = (int) ((Main.applet.mouseY - offset - Const.GENOME_LIST_DIMS[1] - 40) / Const.GENOME_LIST_ENTRY_HEIGHT);

                // check if the choice is valid
                if( choice < cell.getGenome().codons.size() ) {

                    // check if clicked on the settings arrow
                    if( rmx > 0.88f && cell.getGenome().codons.get(choice).isComplex() ) {
                        type = EditType.MODIFY;
                    }else{
                        type = rmx < 0.5f ? EditType.CODON : EditType.CODON_ARGS;
                    }

                    Sounds.CLICK.play();
                    selectedCodon = choice;
                }
            }else if( selected == virus && Main.applet.mouseY > Const.GENOME_LIST_DIMS[1] + 40 + Const.GENOME_LIST_DIMS[3] ){
                if( rmx < 0.5f ) {
                    cell.getGenome().shorten();
                }else{
                    cell.getGenome().lengthen();
                }

                Sounds.CLICK.play();
            }
        }
    }

    public void checkEditListClick() {
        double rmx = ((Main.applet.mouseX - Main.applet.height) - Const.EDIT_LIST_DIMS[0]) / Const.EDIT_LIST_DIMS[2];
        double rmy = (Main.applet.mouseY - Const.EDIT_LIST_DIMS[1]) / Const.EDIT_LIST_DIMS[3];

        if(rmx >= 0 && rmx < 1 && rmy >= 0 && rmy < 1) {

            int count = getOptionCount();
            int choice = (int) (rmy * Main.max( count, 8 ));

            if( choice >= count ) {
                return;
            }

            switch( type ) {

                case DIVINE:
                    if( selected instanceof EditorCell && ((EditorCell) selected).getParticle() != null ) break;
                    if( !divineIntervention( choice ) ) return;
                    break;

                case CODON:
                    if( selectedCodon != -1 && selected instanceof GenomeCell ) {
                        ((GenomeCell) selected).getGenome().codons.get(selectedCodon).setBase( CodonBases.get(choice) );
                    }
                    break;

                case CODON_ARGS:
                    if( selectedCodon != -1 && selected instanceof GenomeCell ) {
                        Codon c = ((GenomeCell) selected).getGenome().codons.get(selectedCodon);
                        c.setArg( c.getArgs()[choice].clone() );
                    }
                    break;

                case MODIFY:
                    if( selectedCodon != -1 && selected instanceof GenomeCell ) {
                        CodonArg arg = ((GenomeCell) selected).getGenome().codons.get(selectedCodon).getArg();
                        if( arg instanceof ComplexCodonArg) {
                            ComplexCodonArg complexArg = (ComplexCodonArg) arg;
                            if( rmx < 0.5f ) {
                                complexArg.decrement(choice);
                            }else{
                                complexArg.increment(choice);
                            }
                        }
                    }
                    break;
            }

            if( selected != null && selected != virus && selected instanceof NormalCell ) {
                Main.applet.world.updateLastEdit();
                ((NormalCell) selected).tamper();
            }

            Sounds.CLICK.play();

        }else{
            type = EditType.DIVINE;
            selectedCodon = -1;
        }
    }

    private int getOptionCount() {
        if( type == EditType.DIVINE ) return Const.DIVINE_CONTROLS.length;
        if( type == EditType.CODON ) return CodonBases.size();

        if( selected instanceof GenomeCell ) {
            GenomeCell cell = (GenomeCell) selected;

            if( type == EditType.CODON_ARGS ) return selectedCodon == -1 ? 0 : cell.getGenome().codons.get(selectedCodon).getArgs().length;

            if( type == EditType.MODIFY ) {
                CodonArg arg = cell.getGenome().codons.get(selectedCodon).getArg();
                if( arg instanceof ComplexCodonArg) {
                    ComplexCodonArg complexArg = (ComplexCodonArg) arg;
                    return complexArg.getOptions().length;
                }
            }
        }

        return 0;
    }

    public boolean handleScroll( MouseEvent event ) {
        if( selected instanceof GenomeCell ) {
            double rmx = ((Main.applet.mouseX - Main.applet.height) - Const.GENOME_LIST_DIMS[0]) / Const.GENOME_LIST_DIMS[2];
            double rmy = (Main.applet.mouseY - Const.GENOME_LIST_DIMS[1] - 40) / Const.GENOME_LIST_DIMS[3];

            if(rmx >= 0 && rmx < 1 && rmy >= 0 && rmy <= 1) {
                offset -= event.getCount() * 24;
                return true;
            }
        }

        return false;
    }

    public boolean divineIntervention( int id ) {

        if( !isDivineControlAvailable(id) ) return false;

        switch( id ) {
            case 0: // Remove
                Main.applet.world.setCellAt(selX, selY, null );
                break;

            case 1: // Revive
                Main.applet.world.aliveCount ++;
                Main.applet.world.setCellAt(selX, selY, new NormalCell(selX, selY, Const.DEFAULT_CELL_GENOME ) );
                break;

            case 2: // Heal
                if( selected instanceof ShellCell ) {
                    ShellCell cell = (ShellCell) selected;
                    cell.healWall();
                }
                break;

            case 3: // Energize
                if( selected instanceof ShellCell ) {
                    ShellCell cell = (ShellCell) selected;
                    cell.giveEnergy();
                }
                break;

            case 4: // Make Wall
                Main.applet.world.setCellAt(selX, selY, new WallCell(selX, selY) );
                break;

            case 5: // Make Shell
                Main.applet.world.shellCount ++;
                Main.applet.world.setCellAt(selX, selY, new ShellCell(selX, selY) );
                break;
        }

        select(selX, selY);
        Main.applet.world.updateLastEdit();
        return true;

    }

    public boolean isDivineControlAvailable( int id ) {
        // For meaning of the specific id see 'DIVINE_CONTROLS' defined in 'Const.java',
        // where id is the offset into that array.

        if( selected == virus || !open ) return false;
        if( id == 0 ) return (selected != null);
        if( id == 2 || id == 3 ) return (selected != null && selected.getType() != CellType.Locked);
        if( id == 1 ) return (selected == null || selected.getType() != CellType.Normal);
        if( id == 4 ) return (selected == null || selected.getType() != CellType.Locked);
        if( id == 5 ) return (selected == null || selected.getType() != CellType.Shell);
        return true;
    }

    public void produce(){
        if(Main.applet.world.getCellAt(arrow.getX(), arrow.getY()) == null){
            VirusParticle u = new VirusParticle(arrow, virus.getGenome().asDNA());
            u.markDivine();
            Main.applet.world.addParticle(u);
            Main.applet.world.updateLastEdit();
        }
    }

}
