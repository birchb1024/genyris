// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.sound;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.*;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;

import static java.lang.Thread.sleep;

public class Sound {
    private static final int EXTERNAL_BUFFER_SIZE = 128000;


    public static Long play(String fileName) throws GenyrisException {

        Clip clip;
        long retval;
        try {
            File file = new File(fileName);
            if (file.exists()) {

                AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = inputStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                clip = (Clip)AudioSystem.getLine(info);
                clip.open(inputStream);
                clip.start();
                retval = Long.valueOf(clip.getMicrosecondLength());
            } else {
                throw new GenyrisException("Sound: file not found: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new GenyrisException("Sound: Malformed URL: " + e);
        } catch (UnsupportedAudioFileException e) {
            throw new GenyrisException("Sound: Unsupported Audio File: " + e);
        } catch (IOException e) {
            throw new GenyrisException("Sound: Input/Output Error: " + e);
        } catch (LineUnavailableException e) {
            throw new GenyrisException("Sound: Line Unavailable Exception Error: " + e);
        }

        return retval;
    }
}
