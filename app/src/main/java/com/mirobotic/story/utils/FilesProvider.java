package com.mirobotic.story.utils;

import android.os.Environment;

import com.mirobotic.story.data.AudioModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FilesProvider {

    public ArrayList<AudioModel> getAudioFiles(String path){
        File dir = new File(path);
        ArrayList<AudioModel> list = new ArrayList<>();
        if (dir.exists()) {

            if (dir.listFiles() != null) {
                for (File f : dir.listFiles()) {
                    if (f.isFile())
                        path = f.getName();

                    if (path.contains(".mp3")) {

                        AudioModel audioModel = new AudioModel(
                                path,
                                "",
                                "",
                                ""
                        );

                        list.add(audioModel);
                    }
                }
            }
        }

        return  list;
    }

    public ArrayList<AudioModel> getPlayList(String rootPath) {

        ArrayList<AudioModel> fileList = new ArrayList<>();

        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {

                    fileList.add(new AudioModel(
                            file.getAbsolutePath(),
                            file.getName(),
                            "",
                            ""
                    ));

                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }
}
