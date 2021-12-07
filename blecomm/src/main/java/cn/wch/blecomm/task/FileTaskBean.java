package cn.wch.blecomm.task;

import java.io.File;


public class FileTaskBean {

    private File file;

    public FileTaskBean( File file) {

        this.file = file;
    }


    public File getFile() {
        return file;
    }
}
