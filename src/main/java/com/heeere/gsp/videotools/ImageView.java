/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heeere.gsp.videotools;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleEnablable;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author remonet
 */
public class ImageView extends AbstractModuleEnablable {

    @ModuleParameter
    public String title = "ImageView Module";
    @ModuleParameter
    public boolean lazy = true;
    @ModuleParameter
    public boolean exitOnClose = false;
    @ModuleParameter
    public String bg = null;
    @ModuleParameter
    public float darkenBg = 0.f;
    @ModuleParameter
    public int scale = 1;
    //
    protected BufferedImage bgImage = null;
    protected BufferedImage toDraw = null;
    protected JFrame frame;
    protected JLabel content;
    protected boolean inited = false;
    //
    private int widthForInit = -1;
    private int heightForInit = -1;

    @Override
    protected void initModule() {
        if (!isEnabled()) {
            return;
        }
        if (lazy) {
            lazy = false;
            return;
        }
        if (inited) {
            return;
        }
        inited = true;
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        if (exitOnClose) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        content = new JLabel() {

            {
                addNotify();
            }

            @Override
            protected void paintComponent(Graphics g) {
                paintContent((Graphics2D) g);
            }
        };
        if (bg != null) {
            if (!interceptBgUrl()) {
                try {
                    bgImage = ImageIO.read(new URL(bg));
                } catch (Exception ex) {
                    Logger.getLogger(ImageView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        int width = widthForInit;
        int height = heightForInit;
        if (bgImage != null) {
            width = bgImage.getWidth();
            height = bgImage.getHeight();
        }
        content.setPreferredSize(new Dimension(width, height));
        content.setMinimumSize(new Dimension(width, height));
        frame.getContentPane().add(content);
        if (bgImage == null && content.getGraphicsConfiguration() != null) {
            bgImage = content.getGraphicsConfiguration().createCompatibleImage(width, height);
        }
        if (bgImage == null) {
            bgImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        }
        if (content.getGraphicsConfiguration() != null) {
            toDraw = content.getGraphicsConfiguration().createCompatibleImage(width, height);
        } else {
            toDraw = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        }
        if (bgImage != null) {
            Graphics2D g = toDraw.createGraphics();
            g.drawImage(bgImage, 0, 0, null);
            if (darkenBg != 0) {
                g.setColor(Color.BLACK);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, darkenBg));
                g.fillRect(0, 0, width, height);
            }
            g.dispose();
        }
        frame.pack();
        frame.setVisible(true);
    }

    public void input(BufferedImage image) {
        if (!isEnabled()) {
            return;
        }
        if (!inited) {
            widthForInit = image.getWidth();
            heightForInit = image.getHeight();
        }
        initModule();
        Graphics2D g = resizeAndFillToDraw(scale);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        content.repaint();
        output(image);
    }

    private synchronized void paintContent(Graphics2D g) {
        g.drawImage(toDraw, 0, 0, null);
    }

    protected Graphics2D resizeAndFillToDraw(int scale) {
        int w = bgImage.getWidth() * scale;
        int h = bgImage.getHeight() * scale;
        if (toDraw.getWidth() != w || toDraw.getHeight() != h) {
            toDraw = content.getGraphicsConfiguration().createCompatibleImage(w, h);
            content.setPreferredSize(new Dimension(w, h));
            content.setMinimumSize(new Dimension(w, h));
            content.invalidate();
            frame.pack();
        }
        Graphics2D g = toDraw.createGraphics();
        g.drawImage(bgImage, 0, 0, w, h, null);
        if (darkenBg != 0) {
            g.setColor(Color.BLACK);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, darkenBg));
            g.fillRect(0, 0, w, h);
        }
        g.scale(scale, scale);
        return g;
    }

    private boolean interceptBgUrl() {
        assert bg != null;
        String scheme = bg.replaceFirst(":.*$", "");
        String what = bg.substring(scheme.length() + 1);
        if ("color".equals(scheme)) {
            String[] e = what.split("x");
            int w = Integer.parseInt(e[0]);
            int h = Integer.parseInt(e[1]);
            bgImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);
            // defaults to black
            if (e.length > 2) {
                try {
                    Color c = Color.decode(e[2]);
                    Graphics2D g = bgImage.createGraphics();
                    g.setColor(c);
                    g.fillRect(0, 0, w, h);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return bgImage != null;
    }

    private void output(BufferedImage image) {
        emitEvent(image);
    }
}
