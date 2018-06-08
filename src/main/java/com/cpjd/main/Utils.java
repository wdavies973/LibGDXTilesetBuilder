package com.cpjd.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;

class Utils {

    /**
     * Hides a file on the system
     * @param f the file to hide
     */
    static boolean hideFile(File f) {
        try {
            if(f.isHidden()) return false;

            Path path = f.toPath();
            Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
            return true;
        } catch(IOException e) {
            System.err.println("Failed to hide file: "+f.getPath());
            return false;
        }
    }

    static void resizeImage(File file, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(file);
            Image resized = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage bufferedImage = new BufferedImage(resized.getWidth(null), resized.getHeight(null), BufferedImage.TYPE_INT_ARGB);

            Graphics g = bufferedImage.createGraphics();
            g.drawImage(resized, 0, 0, null);
            g.dispose();

            ImageIO.write(bufferedImage, "png", file);
        } catch(IOException e) {
            System.err.println("Unable to load image "+file.getName());
        }
    }

    static void copy(File source, File destination) {
        InputStream is;
        OutputStream os;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            is.close();
            os.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static void clearDirectory(File f) {
        File[] files = f.listFiles();

        if(files == null || files.length == 0) return;

        for(File s : files) {
            if(s.isDirectory()) {
                clearDirectory(s);
            }

            s.delete();
        }
    }

    static RenderMeta loadMeta(File f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));

        RenderMeta meta = new RenderMeta(br.readLine(), Integer.parseInt(br.readLine().split("\\s+")[1]),
                Integer.parseInt(br.readLine().split("\\s+")[1]), Integer.parseInt(br.readLine()));

        br.close();

        return meta;
    }

    static void imagesToMap(File parentDir, String tilesetName, int width, int height) {
        File[] images = parentDir.listFiles((dir, name) -> name.endsWith(".png") && name.startsWith(tilesetName) && !name.equals(tilesetName) && name.contains("_"));

        if(images == null || images.length == 0) {
            System.out.println("That directory doesn't contain any png images. Aborting...");
            return;
        }

        Arrays.sort(images, (f1, f2) -> {
            try {
                int i1 = Integer.parseInt(f1.getName().replaceAll("[^0-9]", ""));
                int i2 = Integer.parseInt(f2.getName().replaceAll("[^0-9]", ""));
                return i1 - i2;
            } catch(NumberFormatException e) {
                System.err.println("Error occurred. All images must have a numerical filename.");

                System.exit(0);
                return 0;
            }
        });

        BufferedImage map = new BufferedImage(width * 5, (int)Math.ceil((double)images.length / 5.0) * height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = map.getGraphics();

        int x = 0, y = 0;
        for (File image1 : images) {


            try {
                BufferedImage image = ImageIO.read(image1);
                g.drawImage(image, x, y, null);

                x += width;
                if (x >= width * 5) {
                    x = 0;
                    y += height;
                }
            } catch (IOException e) {
                System.err.println("Failed to read image " + image1.getAbsolutePath() + ", error msg: " + e.getMessage());
            }
        }

        try {
            ImageIO.write(map, "png", new File(TilesetBuilderV2.DIR+File.separator+"Output"+File.separator+tilesetName+".png"));
        } catch(IOException e) {
            System.err.println("Failed to write output map. Error msg: "+e.getMessage());
        }
    }
}
