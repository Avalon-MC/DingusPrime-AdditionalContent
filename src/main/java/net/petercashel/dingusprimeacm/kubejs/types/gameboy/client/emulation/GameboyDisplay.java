package net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation;

import com.mojang.blaze3d.platform.NativeImage;
import eu.rekawek.coffeegb.gpu.Display;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyScreen;

public class GameboyDisplay implements Display, Runnable {

    public static final int DISPLAY_WIDTH = 160;
    public static final int DISPLAY_HEIGHT = 144;
    private boolean doStop;
    private boolean doRefresh;
    public static final int[] COLORS = new int[]{0xe6f8da, 0x99c886, 0x437969, 0x051f2a};
    private final int[] rgb;
    private boolean enabled;
    private int i;

    private long start;
    private long finish;
    private double nextWait = 1;

    public GameboyDisplay() {
        rgb = new int[DISPLAY_WIDTH * DISPLAY_HEIGHT];
    }

    @Override
    public void run() {
        doStop = false;
        doRefresh = false;
        enabled = true;
        while (!doStop) {
            synchronized (this) {
                try {
                    wait((long) nextWait);
                } catch (InterruptedException e) {
                    break;
                }
            }
            start = System.currentTimeMillis();

            if (doRefresh) {
                GameboyScreen.GBImageBuffer.setRGB(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT, rgb, 0, DISPLAY_WIDTH);
                for (int x = 0; x < 160; x++) {
                    for (int y = 0; y < 144; y++)
                    {
                        int rgb = GameboyScreen.GBImageBuffer.getRGB(x,y);
                        GameboyScreen.imageBuffer.getPixels().setPixelRGBA(x,y,NativeImage.combine(255, (rgb) & 0x000000FF, (rgb >>8 ) & 0x000000FF, (rgb >> 16) & 0x000000FF));
                    }
                }

                synchronized (this) {
                    i = 0;
                    doRefresh = false;
                    notifyAll();
                }
            }
            GameboyScreen.imageBuffer.upload();
            finish = System.currentTimeMillis();

            nextWait = (1000.0 / 59.7) - (double)(finish - start);
            if (nextWait < 1) nextWait = 1;
        }
    }

    public void stop() {
        doStop = true;
    }

    @Override
    public void putDmgPixel(int color) {
        rgb[i++] = COLORS[color];
        i = i % rgb.length;
    }

    @Override
    public void putColorPixel(int gbcRgb) {
        rgb[i++] = translateGbcRgb(gbcRgb);
    }
    public static int translateGbcRgb(int gbcRgb) {
        int r = (gbcRgb >> 0) & 0x1f;
        int g = (gbcRgb >> 5) & 0x1f;
        int b = (gbcRgb >> 10) & 0x1f;
        int result = (r * 8) << 16;
        result |= (g * 8) << 8;
        result |= (b * 8) << 0;
        return result;
    }

    @Override
    public synchronized void requestRefresh() {
        doRefresh = true;
        notifyAll();
    }


    @Override
    public synchronized void waitForRefresh() {
        while (doRefresh) {
            try {
                wait(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @Override
    public void enableLcd() {
        enabled = true;
    }

    @Override
    public void disableLcd() {
        enabled = false;
    }

    public void reset() {
        i = 0;
    }
}
