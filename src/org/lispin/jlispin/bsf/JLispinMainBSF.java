package org.lispin.jlispin.bsf;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;
import org.genyris.bsf.GenyrisEngine;

public class JLispinMainBSF {


    public static void main(String[] args) throws IOException {
        String language = "JLispin";
        String inFileName = args[0];
        try {
            Reader in;
            if (inFileName != null) {
                in = new FileReader(inFileName);
            } else {
                in = new InputStreamReader(System.in);
                inFileName = "stdin";
            }
            BSFManager mgr = new BSFManager();
            String[] extensions = {"lin"};
            BSFManager.registerScriptingEngine("JLispin", GenyrisEngine.class.getName(), extensions);
            
            Object obj = mgr.eval(language, inFileName, 0, 0, IOUtils.getStringFromReader(in));
            System.out.println(obj);

        } catch (BSFException e) {
            e.printStackTrace();
        }
    }

}