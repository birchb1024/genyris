// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.genyris.core.Exp;
import org.genyris.core.Lstring;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class Sound {
    private static final int EXTERNAL_BUFFER_SIZE = 128000;

    public static void play(Lstring strFilename) throws GenyrisException {
        /*
         * Copyright (c) 1999 - 2001 by Matthias Pfisterer All rights reserved.
         * 
         * Redistribution and use in source and binary forms, with or without modification, are
         * permitted provided that the following conditions are met:
         *  - Redistributions of source code must retain the above copyright notice, this list of
         * conditions and the following disclaimer. - Redistributions in binary form must reproduce
         * the above copyright notice, this list of conditions and the following disclaimer in the
         * documentation and/or other materials provided with the distribution.
         * 
         * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
         * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
         * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
         * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
         * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
         * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
         * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
         * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
         * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
         */
        File soundFile = new File(strFilename.toString());
        /*
         * We have to read in the sound file.
         */
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        }
        catch (Exception e) {
            throw new GenyrisException(e.getMessage());
        }
        /*
         * From the AudioInputStream, i.e. from the sound file, we fetch information about the
         * format of the audio data. These information include the sampling frequency, the number of
         * channels and the size of the samples. These information are needed to ask Java Sound for
         * a suitable output line for this audio file.
         */
        AudioFormat audioFormat = audioInputStream.getFormat();
        /*
         * Asking for a line is a rather tricky thing. We have to construct an Info object that
         * specifies the desired properties for the line. First, we have to say which kind of line
         * we want. The possibilities are: SourceDataLine (for playback), Clip (for repeated
         * playback) and TargetDataLine (for recording). Here, we want to do normal playback, so we
         * ask for a SourceDataLine. Then, we have to pass an AudioFormat object, so that the Line
         * knows which format the data passed to it will have. Furthermore, we can give Java Sound a
         * hint about how big the internal buffer for the line should be. This isn't used here,
         * signaling that we don't care about the exact size. Java Sound will use some default value
         * for the buffer size.
         */
        SourceDataLine line = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            line = (SourceDataLine)AudioSystem.getLine(info);
            /*
             * The line is there, but it is not yet ready to receive audio data. We have to open the
             * line.
             */
            line.open(audioFormat);
        }
        catch (LineUnavailableException e) {
            throw new GenyrisException(e.getMessage());
        }
        catch (Exception e) {
            throw new GenyrisException(e.getMessage());
        }
        /*
         * Still not enough. The line now can receive data, but will not pass them on to the audio
         * output device (which means to your sound card). This has to be activated.
         */
        line.start();
        /*
         * Ok, finally the line is prepared. Now comes the real job: we have to write data to the
         * line. We do this in a loop. First, we read data from the AudioInputStream to a buffer.
         * Then, we write from this buffer to the Line. This is done until the end of the file is
         * reached, which is detected by a return value of -1 from the read method of the
         * AudioInputStream.
         */
        int nBytesRead = 0;
        byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
            }
            catch (IOException e) {
                throw new GenyrisException(e.getMessage());
            }
            if (nBytesRead >= 0) {
                // int nBytesWritten =
                line.write(abData, 0, nBytesRead);
            }
        }
        /*
         * Wait until all data are played. This is only necessary because of the bug noted below.
         * (If we do not wait, we would interrupt the playback by prematurely closing the line and
         * exiting the VM.)
         * 
         * Thanks to Margie Fitch for bringing me on the right path to this solution.
         */
        line.drain();
        /*
         * All data are played. We can close the shop.
         */
        line.close();
    }
    public static class PlayMethod extends AbstractMethod {
    	public static String getStaticName() {return "play";};
        public PlayMethod(Interpreter interp) {
        	super(interp, getStaticName());
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            if (arguments.length == 1) {
                if (!(arguments[0] instanceof Lstring)) {
                    throw new GenyrisException("Non-string filename passed to Sound!Play");
                }
                play((Lstring)arguments[0]);
                return NIL;
            } else {
                throw new GenyrisException("Missing argument to Sound!play");
            }
        }
    }
}
