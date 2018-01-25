/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.view;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;


/**
 *
 * @author vtq
 */
public class TclFileFilter extends FileFilter {

    //akceptuj tylko pliki tcl
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = FileFilterUtils.getExtension(f);
        if (extension != null) {
            if (extension.equals(FileFilterUtils.tcl)){
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //opis filtra
    public String getDescription() {
        return "*.tcl";
    }


}
