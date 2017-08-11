package com.tencentpic.fhpic.model;

import java.io.File;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * @author gaokb
 * Date: 13-10-22
 * Time: 下午3:52
 * To change this template use File | Settings | File Templates.
 */
public class Image extends BaseModel implements Serializable {

    private String outputDir;//输出目录

    private String outputFileName;//输出文件名

    private int outputWidth=100;//输出宽度

    private int outputHeight=100;//输出高度

    private boolean proportion=true;//是否平滑输出

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public int getOutputWidth() {
        return outputWidth;
    }

    public void setOutputWidth(int outputWidth) {
        this.outputWidth = outputWidth;
    }

    public int getOutputHeight() {
        return outputHeight;
    }

    public void setOutputHeight(int outputHeight) {
        this.outputHeight = outputHeight;
    }

    public boolean isProportion() {
        return proportion;
    }

    public void setProportion(boolean proportion) {
        this.proportion = proportion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image)) return false;

        Image image = (Image) o;

        if (outputHeight != image.outputHeight) return false;
        if (outputWidth != image.outputWidth) return false;
        if (proportion != image.proportion) return false;
        if (!outputDir.equals(image.outputDir)) return false;
        if (!outputFileName.equals(image.outputFileName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = outputDir.hashCode();
        result = 31 * result + outputFileName.hashCode();
        result = 31 * result + outputWidth;
        result = 31 * result + outputHeight;
        result = 31 * result + (proportion ? 1 : 0);
        return result;
    }
}
