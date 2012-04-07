/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heeere.gsp.videotools;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author remonet
 */
public class Montage4x extends AbstractModule {

    @ModuleParameter
    public int mode = 1;
    //
    @ModuleParameter
    public int inputCount = 4;
    //
    BufferedImage[] inputs;
    BufferedImage content = null;

    public Montage4x() {
        this.allowOpenEvents = true;
    }

    public void i0(BufferedImage image) {
        iN(0, image);
    }

    public void i1(BufferedImage image) {
        iN(1, image);
    }

    public void i2(BufferedImage image) {
        iN(2, image);
    }

    public void i3(BufferedImage image) {
        iN(3, image);
    }

    // i(n)
    private void iN(int i, BufferedImage im) {
        if (inputs == null) {
            inputs = new BufferedImage[inputCount];
        }
        inputs[i] = im;
        // checkIfAllReceivedAndProcessIfNecessary
        for (BufferedImage bufferedImage : inputs) {
            if (bufferedImage == null) {
                return;
            }
        }
        // if we reach here, then we have to process the inputs
        doProcess(inputs);
        inputs = null;
    }

    private void doProcess(BufferedImage[] inputs) {
        if (mode == 1) {
            int w0 = inputs[0].getWidth();
            int w1 = inputs[1].getWidth();
            int w2 = inputs[2].getWidth();
            int w3 = inputs[3].getWidth();
            int h0 = inputs[0].getHeight();
            int h1 = inputs[1].getHeight();
            int h2 = inputs[2].getHeight();
            int h3 = inputs[3].getHeight();
            int w = Math.max(w0 + w1, w2 + w3);
            int h = Math.max(h0 + h2, h1 + h3);
            int wmid = Math.max(w0, w2);
            int hmid = Math.max(h0, h1);
            BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);
            Graphics g = res.createGraphics();
            g.drawImage(inputs[0], 0, 0, null);
            g.drawImage(inputs[1], wmid, 0, null);
            g.drawImage(inputs[2], 0, hmid, null);
            g.drawImage(inputs[3], wmid, hmid, null);
            output(res);
        } else {
            System.err.println("Mode unsupported: " + mode);
        }
    }

    private void output(BufferedImage c) {
        emitEvent(c);
    }
}
