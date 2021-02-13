package net.darktree.virus.gui.editor;

import net.darktree.virus.Main;
import net.darktree.virus.cell.Cell;
import net.darktree.virus.cell.CellType;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.codon.CodonBases;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.codon.arg.ComplexCodonArg;
import net.darktree.virus.genome.CellGenome;
import net.darktree.virus.particle.ParticleType;
import net.darktree.virus.particle.VirusParticle;
import net.darktree.virus.util.DrawContext;
import net.darktree.virus.util.Helpers;
import processing.event.MouseEvent;

public class Editor implements DrawContext {

    private boolean open = false;
    public Cell ugo;
    public Cell selected;
    public int selx = 0;
    public int sely = 0;
    public float[] arrow = null;
    private EditType type = EditType.DIVINE;
    public int selectedCodon = -1;
    private float offset = 0;

    public Editor( Main.Settings settings ) {
        ugo = new Cell(-1, -1, CellType.Normal, 0, 1, settings.editor_default);
    }

    public void select( int x, int y ) {
        open = true;
        selected = Main.applet.world.getCellAt( x, y );
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
        rect(10, 160, 530, Main.applet.height - 10);
        rect(540, 160, 200, 270);

        fill(255);
        textSize(96);
        textAlign(LEFT);

        if( selected != null ) {

            text(selected.getCellName(), 25, 255);

            if(isNotUGO && (selected.type != CellType.Locked)){

                int c = 200;
                textSize(22);
                text("This cell is " + (selected.tampered ? "TAMPERED" : "NATURAL"), 555, c);
                text("Contents:", 555, c += 22);
                text("    total: " + selected.getParticleCount(null), 555, c += 44);
                text("    food: " + selected.getParticleCount(ParticleType.FOOD), 555, c += 22);
                text("    waste: " + selected.getParticleCount(ParticleType.WASTE), 555, c += 22);
                text("    UGOs: " + selected.getParticleCount(ParticleType.UGO), 555, c + 22);

                drawBar(Main.applet.COLOR_ENERGY, selected.energy, "Energy", 290);
                drawBar(Main.applet.COLOR_WALL, selected.wall, "Wall health", 360);

            }

            if( selected.type == CellType.Normal ) {
                drawGenomeAsList(selected.genome);

                if(isNotUGO){
                    fill(255);
                    textSize(32);
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
        float x = Main.EDIT_LIST_DIMS[0];
        float y = Main.EDIT_LIST_DIMS[1];
        float w = Main.EDIT_LIST_DIMS[2];
        float h = Main.EDIT_LIST_DIMS[3];

        push();
        textSize(30);
        textAlign(CENTER);
        translate(x, y);

        float buttonWidth = w - Main.MARGIN * 2;

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
                if( selectedCodon != -1 ) {
                    CodonArg[] args = selected.genome.codons.get( selectedCodon ).getArgs();
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
                float buttonHeight = h / Main.max( Main.DIVINE_CONTROLS.length, 8 );

                for(int i = 0; i < Main.DIVINE_CONTROLS.length; i++){
                    drawButton(
                            isDivineControlAvailable(i) ? Main.applet.COLOR_DIVINE_CONTROL : Main.applet.COLOR_DIVINE_DISABLED,
                            buttonHeight * i,
                            buttonWidth,
                            buttonHeight,
                            Main.DIVINE_CONTROLS[i]
                    );
                }
            } break;

            case MODIFY: {
                if( selectedCodon != -1 ) {
                    CodonArg arg = selected.genome.codons.get( selectedCodon ).getArg();
                    if( arg instanceof ComplexCodonArg) {

                        ComplexCodonArg complexArg = (ComplexCodonArg) arg;

                        String[] options = complexArg.getOptions();
                        float buttonHeight = h / Main.max( options.length, 8 );

                        for(int i = 0; i < options.length; i++){
                            float pos = buttonHeight * i;

                            drawButton(
                                    Main.applet.COLOR_CODON_OPTION,
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
        rect( Main.MARGIN, x + Main.MARGIN, w, h - Main.MARGIN * 2);
        fill( 255 );
        text( t, w * 0.5f + Main.MARGIN * 2, x + h * 0.5f + 11 );
    }

    private void drawGenomeAsList(CellGenome genome){
        float x = Main.GENOME_LIST_DIMS[0];
        float y = Main.GENOME_LIST_DIMS[1];
        float w = Main.GENOME_LIST_DIMS[2];
        float h = Main.GENOME_LIST_DIMS[3];

        int codonsCount = genome.codons.size();
        float buttonWidth = w * 0.5f - Main.MARGIN;

        textSize(28);
        textAlign(CENTER);
        push();
        translate(x, y + 40 + offset);

        push();
        float transY = Main.GENOM_LIST_ENTRY_HEIGHT * (genome.appRO + 0.5f);
        translate(0, transY);

        if( transY + offset > -20 && transY + offset < h ) {
            if(selected != ugo && genome.selected >= 0 && genome.selected < codonsCount){
                Main.applet.renderer.drawGenomeArrows(w, Main.GENOM_LIST_ENTRY_HEIGHT);
            }
        }
        pop();

        for(int i = 0; i < codonsCount; i++){
            float buttonPos = Main.GENOM_LIST_ENTRY_HEIGHT * i;
            Codon codon = genome.codons.get(i);

            float offsetPos = buttonPos + offset;
            if( offsetPos < -40 || offsetPos > h ) {
                continue;
            }

            drawCodon( buttonPos, buttonWidth, i == selectedCodon, EditType.CODON, codon );
            drawCodon( buttonPos, buttonWidth, i == selectedCodon, EditType.CODON_ARGS, codon );

            // draw settings gear
            if( codon.isComplex() ) {
                if( i == selectedCodon && type == EditType.MODIFY ) {
                    Main.applet.tint(255, (0.5f + 0.5f * Main.sin(getFrameCount() * 0.25f)) * 140 + 100);
                }

                image(Main.applet.renderer.spriteGear, buttonWidth * 2 - Main.GENOM_LIST_ENTRY_HEIGHT, buttonPos, Main.GENOM_LIST_ENTRY_HEIGHT, Main.GENOM_LIST_ENTRY_HEIGHT);
                noTint();
            }

        }
        pop();

        fill(80);
        rect(x, y, w, 40);
        rect(x, y + h + 40, w, 40);

        if(selected == ugo){
            fill(255);
            textSize(60);
            textAlign(LEFT);
            text("-", x + Main.MARGIN * 8, y + h + 80);
            textAlign(RIGHT);
            text("+", x + w - Main.MARGIN * 8, y + h + 80);
        }

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
            x = w - Main.MARGIN;
        }

        fill(0);
        rect(x + Main.MARGIN, y + Main.MARGIN, w, Main.GENOM_LIST_ENTRY_HEIGHT - Main.MARGIN * 2);

        if( codon.hasSubstance() ) {
            fill(c);
            float trueW = w * codon.health;
            float trueX = (( type == EditType.CODON ) ? w * (1 - codon.health) : 0) + x + Main.MARGIN;
            rect (trueX, y + Main.MARGIN, trueW, Main.GENOM_LIST_ENTRY_HEIGHT - Main.MARGIN * 2 );
        }

        fill(255);
        text(t, x + w * 0.5f, y + Main.GENOM_LIST_ENTRY_HEIGHT / 2 + 11);

        if( selected && type == this.type ){
            fill(255, 255, 255, (0.5f + 0.5f * Main.sin(getFrameCount() * 0.25f)) * 140);
            rect(x + Main.MARGIN, y + Main.MARGIN, w, Main.GENOM_LIST_ENTRY_HEIGHT - Main.MARGIN * 2);
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

    public void drawSelection() {
        if(arrow != null){
            if(Helpers.euclidLength(arrow) > Main.applet.settings.min_length_to_produce){
                stroke(0);
            }else{
                stroke(150);
            }
            Main.applet.renderer.drawArrow(arrow[0], arrow[1], arrow[2], arrow[3]);
        }

        if( open && selected != ugo ) {
            push();
            translate(Main.applet.renderer.trueXtoAppX(selx), Main.applet.renderer.trueYtoAppY(sely));
            scale(Main.applet.renderer.camS / Main.BIG_FACTOR);
            noFill();
            stroke(0, 255, 255, 155 + (int) (100 * Math.sin(getFrameCount() / 10.f)));
            strokeWeight(4);
            rect(0, 0, Main.BIG_FACTOR, Main.BIG_FACTOR);
            pop();
        }
    }

    public void checkInput() {
        if(open) {
            checkEditListClick();
            if( selected != null && selected.hasGenome() ) checkGenomeListClick();
            if( Main.applet.mouseX > Main.applet.width - 160 && Main.applet.mouseY < 160 ) close();
        }else{
            if( Main.applet.mouseX > Main.applet.width - 160 && Main.applet.mouseY < 160 ) openUGO();
        }
    }

    public void checkGenomeListClick() {
        double rmx = ((Main.applet.mouseX - Main.applet.height) - Main.GENOME_LIST_DIMS[0]) / Main.GENOME_LIST_DIMS[2];
        double rmy = (Main.applet.mouseY - offset - Main.GENOME_LIST_DIMS[1] - 40) / Main.GENOME_LIST_DIMS[3];

        if(rmx >= 0 && rmx < 1 && rmy >= 0){
            if( rmy < 1 ) {
                int choice = (int) (rmy * (Main.GENOME_LIST_DIMS[3] / Main.GENOM_LIST_ENTRY_HEIGHT));

                // check if the choice is valid
                if( choice < selected.genome.codons.size() ) {

                    // check if clicked on the settings gear
                    if( rmx > 0.88f && selected.genome.codons.get(choice).isComplex() ) {
                        type = EditType.MODIFY;
                    }else{
                        type = rmx < 0.5f ? EditType.CODON : EditType.CODON_ARGS;
                    }

                    selectedCodon = choice;
                }
            }else if( selected == ugo && Main.applet.mouseY > Main.GENOME_LIST_DIMS[1] + 40 + Main.GENOME_LIST_DIMS[3] ){
                if( rmx < 0.5f ) {
                    selected.genome.shorten();
                }else{
                    selected.genome.lengthen();
                }
            }
        }
    }

    public void checkEditListClick() {
        double rmx = ((Main.applet.mouseX - Main.applet.height) - Main.EDIT_LIST_DIMS[0]) / Main.EDIT_LIST_DIMS[2];
        double rmy = (Main.applet.mouseY - Main.EDIT_LIST_DIMS[1]) / Main.EDIT_LIST_DIMS[3];

        if(rmx >= 0 && rmx < 1 && rmy >= 0 && rmy < 1) {

            int count = getOptionCount();
            int choice = (int) (rmy * Main.max( count, 8 ));

            if( choice >= count ) {
                return;
            }

            switch( type ) {

                case DIVINE:
                    divineIntervention( choice );
                    break;

                case CODON:
                    if( selectedCodon != -1 ) {
                        selected.genome.codons.get(selectedCodon).setBase( CodonBases.get(choice) );
                    }
                    break;

                case CODON_ARGS:
                    if( selectedCodon != -1 ) {
                        Codon c = selected.genome.codons.get(selectedCodon);
                        c.setArg( c.getArgs()[choice].clone() );
                    }
                    break;

                case MODIFY:
                    if( selectedCodon != -1 ) {
                        CodonArg arg = selected.genome.codons.get(selectedCodon).getArg();
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

            if( selected != null && selected != ugo ) {
                Main.applet.world.lastEditFrame = getFrameCount();
                selected.tamper();
            }

        }else{
            type = EditType.DIVINE;
            selectedCodon = -1;
        }
    }

    private int getOptionCount() {
        if( type == EditType.DIVINE ) return Main.DIVINE_CONTROLS.length;
        if( type == EditType.CODON ) return CodonBases.size();
        if( type == EditType.CODON_ARGS ) return selectedCodon == -1 ? 0 : selected.genome.codons.get(selectedCodon).getArgs().length;

        if( type == EditType.MODIFY ) {
            CodonArg arg = selected.genome.codons.get(selectedCodon).getArg();
            if( arg instanceof ComplexCodonArg) {
                ComplexCodonArg complexArg = (ComplexCodonArg) arg;
                return complexArg.getOptions().length;
            }
        }

        return 0;
    }

    public boolean handleScroll( MouseEvent event ) {
        if( selected != null && selected.type == CellType.Normal ) {
            double rmx = ((Main.applet.mouseX - Main.applet.height) - Main.GENOME_LIST_DIMS[0]) / Main.GENOME_LIST_DIMS[2];
            double rmy = (Main.applet.mouseY - Main.GENOME_LIST_DIMS[1] - 40) / Main.GENOME_LIST_DIMS[3];

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
                Main.applet.world.setCellAt( selx, sely, null );
                break;

            case 1: // Revive
                Main.applet.world.aliveCount ++;
                Main.applet.world.setCellAt( selx, sely, new Cell( selx, sely, CellType.Normal, 0, 1, Main.applet.settings.genome ) );
                break;

            case 2: // Heal
                selected.healWall();
                break;

            case 3: // Energize
                selected.giveEnergy();
                break;

            case 4: // Make Wall
                Main.applet.world.setCellAt( selx, sely, new Cell( selx, sely, CellType.Locked, 0, 1, Main.applet.settings.genome ) );
                break;

            case 5: // Make Shell
                Main.applet.world.shellCount ++;
                Main.applet.world.setCellAt( selx, sely, new Cell( selx, sely, CellType.Shell, 0, 1, Main.applet.settings.genome ) );
                break;
        }

        select( selx, sely );
        Main.applet.world.lastEditFrame = getFrameCount();

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

    public void produce(){
        if(Main.applet.world.getCellAtUnscaled(arrow[0], arrow[1]) == null){
            VirusParticle u = new VirusParticle(arrow, ugo.genome.asDNA());
            u.markDivine();
            Main.applet.world.addParticle(u);
            Main.applet.world.lastEditFrame = getFrameCount();
        }
    }

}
