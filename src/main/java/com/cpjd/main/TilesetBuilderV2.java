package com.cpjd.main;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TilesetBuilderV2 {

    private static final String WELCOME_MESSAGE =
            "Welcome to the LibGDX tileset builder. This builder is fully automated, so refer to www.github.com/wdavies973/LibGDXTilesetBuilder for help.";

    static String DIR = "";
    private static String BACKUP = "";

    private ArrayList<File> imageDirectories;

    private TilesetBuilderV2() {
        File modelDirectory = new File(DIR + File.separator + "Models" + File.separator);
        File output = new File(DIR+File.separator+"Output"+ File.separator);

        if(!output.exists()) output.mkdir();

        imageDirectories = new ArrayList<>();

        // Hide the ".blend1" files because they're annoying
        System.out.println(hideBlend1Files(modelDirectory)+" .blend1 files were hidden.");

        // Next, look for folders named "Rendered", these folders will be added to the build queue
        addDirectories(modelDirectory);

        // Next, process each rendered folder
        // Each folder should contain a ".meta" file
        ArrayList<RenderMeta> toProcess = new ArrayList<>();

        main : for(File f : imageDirectories) {
            RenderMeta meta = null;

            File[] search = f.listFiles();

            if(search == null || search.length == 0) {
                System.err.println("No .meta file was found in "+f.getAbsolutePath()+". Skipping...");
                continue;
            }

            for(File s : search) {
                if(s.getName().contains(".meta")) {
                    try {
                        meta = Utils.loadMeta(s);
                        break;
                    } catch(Exception e) {
                        e.printStackTrace();
                        System.err.println("Error reading .meta file in "+f.getAbsolutePath()+". Skipping...");
                        continue main;
                    }
                }
            }

            if(meta == null) {
                System.err.println("Error reading .meta file in "+f.getAbsolutePath()+". Skipping...");
                continue;
            }

            meta.setRenderedDirectory(f);
            toProcess.add(meta);
        }

        System.out.println("Found "+toProcess.size()+" directories to process.");

        Collections.sort(toProcess);

        // Start processing the directory, steps:
        /*
         * (First, clear the output directory)
         *
         * 1) Move all files (images & NOT .meta) to the output folder
         * 2) Rename them according to "tilesetName_index"
         * 3) Generate a tileset image "tilesetName"
         */
        Utils.clearDirectory(output);


        // Process number of images
        for (RenderMeta meta : toProcess) {
            File[] images = meta.getRenderedDirectory().listFiles(pathname -> (pathname.getName().contains(".png")));

            if (images == null || images.length == 0) {
                System.err.println("No images found in directory " + meta.getRenderedDirectory().getAbsolutePath() + ". Skipping");
                continue;
            }

            /*
             * This will compute the number of images that will fill the sprite sheet,
             * including null tiles
             */
            int num = 5 * (int) Math.ceil(images.length / 5.0);
            meta.setNumImages(num);
        }

        for(int i = 0; i < toProcess.size(); i++) {
            RenderMeta meta = toProcess.get(i);

            File[] images = meta.getRenderedDirectory().listFiles(pathname -> (pathname.getName().contains(".png")));

            if(images == null || images.length == 0) {
                System.err.println("No images found in directory "+meta.getRenderedDirectory().getAbsolutePath()+". Skipping");
                continue;
            }


            /*
             * Now, sort the images by their names
             */
            Arrays.sort(images, (f1, f2) -> {
                try {
                    int i1 = Integer.parseInt(f1.getName().replaceAll("[^0-9]", ""));
                    int i2 = Integer.parseInt(f2.getName().replaceAll("[^0-9]", ""));
                    return i1 - i2;
                } catch(NumberFormatException e) {
                    e.printStackTrace();
                    System.err.println("Error occurred. All images must have a numerical filename.");
                    System.exit(0);
                    return 0;
                }
            });

            // Now, go through and rename them according to the indexes.
            // To find the starting index, find the sum of all "numImages" from
            // Previous RenderMeta
            int total = 0;
            for(int j = 0; j < i; j++) {
                total += toProcess.get(j).getNumImages();
            }

            for(int j = 0; j < images.length; j++) {
                File f = images[j];

                File dest = new File(output + File.separator + meta.getTilesetName()+"_"+f.getName());

                Utils.copy(f, dest);

                // Resize the file
                Utils.resizeImage(dest, meta.getWidth(), meta.getHeight());

                dest.renameTo(new File(dest.getParentFile()+File.separator+dest.getName().split("_")[0] + "_" + (total + j)+".png"));
            }

            // Alright, next just generate the tile-sheet for Tiled, the tile sheet won't actually be used for anything
            Utils.imagesToMap(output, meta.getTilesetName(), meta.getWidth(), meta.getHeight());

            // Rename the file by just prefixing the tileset name to its name
            System.out.println("Processing directory: "+(i + 1)+" / "+toProcess.size());
        }
        try {
            System.out.println("Backing up directory...");
            Utils.clearDirectory(new File(BACKUP));
            FileUtils.copyDirectory(new File(DIR), new File(BACKUP));
            System.out.println("Successfully backed up working directory.");
        } catch(IOException e) {
            System.err.println("Backup failed");
        }
    }

    private void addDirectories(File directory) {
        File[] files = directory.listFiles();

        if(files == null || files.length == 0) return;

        for(File f : files) {
            if(f.getName().split("\\.")[0].equals("Rendered")) {
                imageDirectories.add(f);
                continue;
            }

            if(f.isDirectory()) addDirectories(f);
        }
    }

    private int hideBlend1Files(File directory) {
        File[] files = directory.listFiles();

        if(files == null || files.length == 0) return 0;

        int hidden = 0;

        for(File f : files) {
            if(f.isDirectory()) {
                hidden += hideBlend1Files(f);
            }

            if(f.getName().contains("blend1")) {
                if(Utils.hideFile(f)) hidden++;
            }
        }

        return hidden;
    }

    public static void main(String[] args) {
        try {
            DIR = args[0];
            BACKUP = args[1];

            if(!new File(DIR).exists() || !new File(BACKUP).exists()) {
                System.out.println("Please specify a valid target directory and/or backup directory.");
                return;
            }

        } catch(Exception e) {
            System.err.println("ERROR: Please provide a target directory in command line preferences");
            return;
        }

        System.out.println(WELCOME_MESSAGE);
        System.out.println("Using working directory: "+DIR);
        System.out.println("Using backup directory: "+BACKUP);
        new TilesetBuilderV2();
    }
}
