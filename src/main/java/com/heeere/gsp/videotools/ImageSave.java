/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heeere.gsp.videotools;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleEnablable;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author remonet
 */
public class ImageSave extends AbstractModuleEnablable {

    @ModuleParameter
    public String format = null;

    @Override
    protected void initModule() {
        if (format == null || format.isEmpty()) {
            System.err.println("WARNING: 'format' in ImageSave is not set... not writing images.");
        }
    }

    public void input(RenderedImage im) {
        if (!isEnabled()) {
            return;
        }
        saveImage(im);
    }
    private int saveImageIndex = 0;

    public void inputIndex(int index) {
        saveImageIndex = index;
    }

    private void outputIndex(int saveImageIndex) {
        emitEvent(saveImageIndex);
    }

    protected void saveImage(RenderedImage toDraw) {
        if (format != null) {
            outputIndex(saveImageIndex);
            File f = new File(String.format(format, saveImageIndex));
            try {
                ImageIO.write(toDraw, getExtension(f), f);
            } catch (IOException ex) {
                Logger.getLogger(ImageSave.class.getName()).log(Level.SEVERE, null, ex);
            }
            saveImageIndex++;
        }
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
