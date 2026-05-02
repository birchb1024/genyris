// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.sound

import org.genyris.exception.GenyrisException
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import javax.sound.sampled.*

object Sound {
    private const val EXTERNAL_BUFFER_SIZE = 128000


    @kotlin.Throws(GenyrisException::class)
    fun play(fileName: String): Long {
        val clip: Clip
        val retval: Long
        try {
            val file = File(fileName)
            if (file.exists()) {
                val inputStream = AudioSystem.getAudioInputStream(file)
                val format = inputStream.getFormat()
                val info = DataLine.Info(Clip::class.java, format)
                clip = AudioSystem.getLine(info) as Clip
                clip.open(inputStream)
                clip.start()
                retval = java.lang.Long.valueOf(clip.getMicrosecondLength())
            } else {
                throw GenyrisException("Sound: file not found: " + fileName)
            }
        } catch (e: MalformedURLException) {
            throw GenyrisException("Sound: Malformed URL: " + e)
        } catch (e: UnsupportedAudioFileException) {
            throw GenyrisException("Sound: Unsupported Audio File: " + e)
        } catch (e: IOException) {
            throw GenyrisException("Sound: Input/Output Error: " + e)
        } catch (e: LineUnavailableException) {
            throw GenyrisException("Sound: Line Unavailable Exception Error: " + e)
        }

        return retval
    }
}
