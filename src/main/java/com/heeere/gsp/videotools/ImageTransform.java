/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heeere.gsp.videotools;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleEnablable;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

/**
 *
 * @author remonet
 */
public class ImageTransform extends AbstractModuleEnablable {

    private static String UNSET = new String("UNSET");
    @ModuleParameter
    public String transform = UNSET;

    @Override
    protected void initModule() {
        if (UNSET == transform) {
            System.err.println("'transform' parameter of " + this.getClass().getSimpleName() + " is mandatory");
        }
    }

    public void input(BufferedImage in) {
        if (!isEnabled()) {
            return;
        }
        String[] sequence = transform.split("\\s*\\|\\s*");
        BufferedImage out = null;
        for (String seq : sequence) {
            // only handle a single operation per instance
            String[] parts = seq.split("\\s+");
            if (false) {
            } else if (parts[0].equals("crop")) {
                int x = intof(parts[1]);
                int y = intof(parts[2]);
                int w = intof(parts[3]);
                int h = intof(parts[4]);
                out = im(w, h, in);
                Graphics2D g = out.createGraphics();
                g.translate(-x, -y);
                g.drawImage(in, 0, 0, null);
                g.dispose();
            } else if (parts[0].equals("scale")) {
                double sx = doubleof(parts[1]);
                double sy = doubleof(parts[2]);
                int w = (int) (in.getWidth() * sx);
                int h = (int) (in.getHeight() * sy);
                out = im(w, h, in);
                Graphics2D g = out.createGraphics();
                g.scale(sx, sy);
                g.drawImage(in, 0, 0, null);
                g.dispose();
            } else if (parts[0].equals("invert")) {
                int w = in.getWidth();
                int h = in.getHeight();
                out = im(w, h, in);
                Graphics2D g = out.createGraphics();
                g.drawImage(in, 0, 0, null);
                for (int y = 0; y < h; y++) { // highly suboptimal
                    for (int x = 0; x < w; x++) {
                        int rgb = out.getRGB(x, y);
                        rgb = rgb ^ 0xFFFFFF;
                        out.setRGB(x, y, rgb);
                    }
                }
            } else if (parts[0].equals("threshold")) {
                int mask = intInDecimalOrHexadecimal(parts[1]);
                int threshold = intInDecimalOrHexadecimal(parts[2]);
                int w = in.getWidth();
                int h = in.getHeight();
                out = im(w, h, in);
                Graphics2D g = out.createGraphics();
                g.drawImage(in, 0, 0, null);
                for (int y = 0; y < h; y++) { // highly suboptimal
                    for (int x = 0; x < w; x++) {
                        int rgb = out.getRGB(x, y);
                        rgb = rgb & mask;
                        rgb = rgb < threshold ? 0xFF000000 : 0xFFFFFFFF;
                        out.setRGB(x, y, rgb);
                    }
                }
            } else if (parts[0].equals("identity")) {
                out = in;
            }
            in = out; // for next iteration, this output becomes the input
        }
        if (out != null) {
            output(out);
        }
    }

    private void output(RenderedImage im) {
        emitEvent(im);
    }

    private BufferedImage im(int w, int h, BufferedImage in) {
        if (in != null && in.getType() != 0) {
            return new BufferedImage(w, h, in.getType());
        } else {
            return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        }
    }

    private int intof(String string) {
        return Integer.parseInt(string);
    }

    private double doubleof(String string) {
        return Double.parseDouble(string);
    }

    private int intInDecimalOrHexadecimal(String string) {
        if (string.matches("^(0[xX]|#).*")) {
            return Integer.parseInt(string.replaceAll("^(0[xX]|#)", ""), 16);
        } else {
            return Integer.parseInt(string);
        }
    }
}
