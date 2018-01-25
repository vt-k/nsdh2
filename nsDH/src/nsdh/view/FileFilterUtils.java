/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.view;

import java.io.File;
import javax.swing.ImageIcon;


/**
 *
 * @author vtq
 */
public class FileFilterUtils {

    public final static String png = "png";
    public final static String xml = "xml";
    public final static String tcl = "tcl";

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

}
