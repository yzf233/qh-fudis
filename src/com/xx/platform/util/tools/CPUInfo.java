package com.xx.platform.util.tools;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 *
 * @version 1.0
 */
/**
 WindowsCmd ="cmd.exe /c echo %NUMBER_OF_PROCESSORS%";//windowsµƒÃÿ ‚
 SolarisCmd = {"/bin/sh", "-c", "/usr/sbin/psrinfo | wc -l"};
 AIXCmd = {"/bin/sh", "-c", "/usr/sbin/lsdev -Cc processor | wc -l"};
 HPUXCmd = {"/bin/sh", "-c", "echo \"map\" | /usr/sbin/cstm | grep CPU | wc -l "};
 LinuxCmd = {"/bin/sh", "-c", "cat /proc/cpuinfo | grep ^process | wc -l"};
 *
 * */
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class CPUInfo {
    public String getMACAddress(String ipAddress) {
        String str = "", strMAC = "", macAddress = "";
        try {
            Process pp = Runtime.getRuntime().exec("nbtstat -a " + ipAddress);
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    if (str.indexOf("MAC Address") > 1) {
                        strMAC = str.substring(str.indexOf("MAC Address") + 14,
                                               str.length());
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            return "Can't Get MAC Address!";
        }
        //
        if (strMAC.length() < 17) {
            return "Error!";
        }
        macAddress = strMAC.substring(0, 2) + ":"
                     + strMAC.substring(3, 5) + ":"
                     + strMAC.substring(6, 8) + ":"
                     + strMAC.substring(9, 11) + ":"
                     + strMAC.substring(12, 14) + ":"
                     + strMAC.substring(15, 17);
        //
        return macAddress;
    }

    public static void main(String[] args) {
        try {
            java.lang.Process proc = Runtime.getRuntime().exec("ipconfig /all");
            InputStream istr = proc.getInputStream();
            byte[] data = new byte[1024];
            istr.read(data);
            String netdata = new String(data);
            System.out.println("Your Mac Address=" + procAll(netdata));
        } catch (IOException e) {
            System.out.println("error=" + e);
            e.printStackTrace();
        }
    }

    public static String procAll(String str) {
        return procStringEnd(procFirstMac(procAddress(str)));
    }

    public static String procAddress(String str) {
        int indexof = str.indexOf("Physical Address");
        if (indexof > 0) {
            return str.substring(indexof, str.length());
        }
        return str;
    }

    public static String procFirstMac(String str) {
        int indexof = str.indexOf(":");
        if (indexof > 0) {
            return str.substring(indexof + 1, str.length()).trim();
        }
        return str;
    }

    public static String procStringEnd(String str) {
        int indexof = str.indexOf("\r");
        if (indexof > 0) {
            return str.substring(0, indexof).trim();
        }
        return str;
    }
}
