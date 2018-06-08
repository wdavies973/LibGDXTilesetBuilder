package com.cpjd.main;

import java.io.File;

public class RenderMeta implements Comparable<RenderMeta> {

    private String tilesetName;
    private int width, height;
    private int buildOrder;

    // Set later
    private File renderedDirectory;

    private int numImages;

    public RenderMeta(String tilesetName, int width, int height, int buildOrder) {
        this.tilesetName = tilesetName;
        this.width = width;
        this.height = height;
        this.buildOrder = buildOrder;
    }

    public int getNumImages() {
        return numImages;
    }

    public void setNumImages(int numImages) {
        this.numImages = numImages;
    }

    public void setRenderedDirectory(File renderedDirectory) {
        this.renderedDirectory = renderedDirectory;
    }

    public File getRenderedDirectory() {
        return renderedDirectory;
    }

    public String getTilesetName() {
        return tilesetName;
    }

    public void setTilesetName(String tilesetName) {
        this.tilesetName = tilesetName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBuildOrder() {
        return buildOrder;
    }

    public void setBuildOrder(int buildOrder) {
        this.buildOrder = buildOrder;
    }

    @Override
    public String toString() {
        return "RenderMeta{" +
                "tilesetName='" + tilesetName + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", buildOrder=" + buildOrder +
                ", renderedDirectory=" + renderedDirectory +
                ", numImages=" + numImages +
                '}';
    }

    @Override
    public int compareTo(RenderMeta o) {
        return Integer.compare(buildOrder, o.getBuildOrder());
    }
}
