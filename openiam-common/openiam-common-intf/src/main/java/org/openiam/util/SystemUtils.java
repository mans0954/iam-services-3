package org.openiam.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.servlet.ServletContext;

public class SystemUtils {
    private static Runtime runtime = Runtime.getRuntime();
    private static DecimalFormat df = new DecimalFormat("#.#");
    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a");

    public static String getWarManifestInfo(ServletContext context, String attrName) {
        if (context != null) {
            return getManifestInfoInternal(context, null, attrName, null);
        } else {
            return "unknown";
        }
    }

    public static String getJarManifestInfo(String resName, String attrName) {
        if (StringUtils.isNotEmpty(resName)) {
            return getManifestInfoInternal(null, attrName, attrName, null);
        } else {
            return "unknown";
        }
    }
    public static String getManifestInfo(Class clazz, String attrName) {
        return getManifestInfoInternal(null, null, attrName, clazz);
    }

    private static String getManifestInfoInternal(ServletContext context, String resName, String attrName, Class clazz) {
        InputStream is = null;
        if (StringUtils.isEmpty(resName) && context != null) {
            is = context.getResourceAsStream("/" + JarFile.MANIFEST_NAME);

        } else if (StringUtils.isNotEmpty(resName)) {
            try {
                Enumeration resEnum;
                resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
                while (resEnum.hasMoreElements()) {
                    URL url = (URL)resEnum.nextElement();
                    if (url.getPath().contains(resName)) {
                        is = url.openStream();
                    }
                }
            } catch (IOException e) {
                return "";
            }
        } else if(clazz!=null){
            String className = clazz.getSimpleName() + ".class";
            String classPath = clazz.getResource(className).toString();
            String manifestPath = JarFile.MANIFEST_NAME;
            if (!classPath.startsWith("jar")) {
                manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                        "/META-INF/MANIFEST.MF";
            }
            Manifest manifest = null;
            try {
               is=new URL(manifestPath).openStream();
            } catch (IOException e) {
                return "unknown";
            }
        }
        if (is != null) {
            try {
                Attributes attrs = new Manifest(is).getMainAttributes();
                String attrValue = attrs.getValue(attrName);
                if(attrValue != null) {
                    return attrValue;
                }
            } catch (IOException e) {
                return "";
            }
        }
        return "unknown";
    }


    public static String getOsInfo(String param) {
        if (param.equalsIgnoreCase("name")) return org.apache.commons.lang.SystemUtils.OS_NAME;
        if (param.equalsIgnoreCase("version")) return org.apache.commons.lang.SystemUtils.OS_VERSION;
        if (param.equalsIgnoreCase("arch")) return org.apache.commons.lang.SystemUtils.OS_ARCH;

        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();

        if (param.equalsIgnoreCase("systemLoadAverage")) return addValueIfPossible( os, "systemLoadAverage");
        if (param.equalsIgnoreCase("openFileDescriptorCount")) return addValueIfPossible( os, "openFileDescriptorCount");
        if (param.equalsIgnoreCase("maxFileDescriptorCount")) return addValueIfPossible( os, "maxFileDescriptorCount");
        if (param.equalsIgnoreCase("committedVirtualMemorySize")) return addValueIfPossible( os, "committedVirtualMemorySize");
        if (param.equalsIgnoreCase("totalPhysicalMemorySize")) return addValueIfPossible( os, "totalPhysicalMemorySize");
        if (param.equalsIgnoreCase("totalSwapSpaceSize")) return addValueIfPossible( os, "totalSwapSpaceSize");
        if (param.equalsIgnoreCase("processCpuTime")) return addValueIfPossible( os, "processCpuTime");

        try {
            if( !os.getName().toLowerCase(Locale.ENGLISH).startsWith( "windows" ) ) {
                // Try some command line things
                if (param.equalsIgnoreCase("uname")) return execute( "uname -a" );
                if (param.equalsIgnoreCase("ulimit")) return execute( "ulimit -n" );
                if (param.equalsIgnoreCase("uptime")) return execute( "uptime" );
            } else {
                return "";
            }
        }
        catch( Throwable ex ) {} // ignore
        return "unknown";
    }

    public static boolean isWindows() {
        return org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;
    }

    public static boolean isLinux() {
        return org.apache.commons.lang.SystemUtils.IS_OS_LINUX;
    }

    public static String getJavaInfo(String param) {
        if (param.equalsIgnoreCase("name")) return org.apache.commons.lang.SystemUtils.JAVA_VM_NAME;
        if (param.equalsIgnoreCase("version")) return org.apache.commons.lang.SystemUtils.JAVA_VERSION_TRIMMED;
        if (param.equalsIgnoreCase("classpath")) return org.apache.commons.lang.SystemUtils.JAVA_CLASS_PATH;

        try {
            RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
            if (param.equalsIgnoreCase("bootclasspath")) return mx.getBootClassPath();
            if (param.equalsIgnoreCase("commandLineArgs")) return ArrayUtils.toString(mx.getInputArguments());
            if (param.equalsIgnoreCase("startTime")) return sdf.format(new Date(mx.getStartTime()));
            if (param.equalsIgnoreCase("upTime")) return convertTime(mx.getUptime());
        } catch (Exception e) {
            return "error";
        }
        return "unknown";
    }

    public static String getMemInfo(String param) {
        if (param.equalsIgnoreCase("free")) return humanReadableUnits(runtime.freeMemory(), df);
        if (param.equalsIgnoreCase("total")) return humanReadableUnits(runtime.totalMemory(), df);
        if (param.equalsIgnoreCase("max")) return humanReadableUnits(runtime.maxMemory(), df);
        if (param.equalsIgnoreCase("used")) {
            long used = runtime.totalMemory() - runtime.freeMemory();
            double percentUsed = ((double)(used)/(double)runtime.maxMemory())*100;
            return humanReadableUnits(used, df) + " (" + df.format(percentUsed) + "%)";
        }
        return "unknown";
    }

    /**
     * Utility function to execute a function
     */
            private static String execute( String cmd )
    {
        DataInputStream in = null;
        BufferedReader reader = null;

        try {
            Process process = Runtime.getRuntime().exec(cmd);
            in = new DataInputStream( process.getInputStream() );
            // use default charset from locale here, because the command invoked also uses the default locale:
            return IOUtils.toString(in);
        }
        catch( Exception ex ) {}
        finally {
            IOUtils.closeQuietly( reader );
            IOUtils.closeQuietly( in );
        }
        return "";
    }

    private static String addValueIfPossible( OperatingSystemMXBean os, String getter )
    {
        try {
            String n = Character.toUpperCase( getter.charAt(0) ) + getter.substring( 1 );
            Method m = os.getClass().getMethod( "get" + n );
            Object v = m.invoke( os, (Object[])null );
            if( v != null ) {
                return v.toString();
            }
        }
        catch( Exception ex ) {}
        return "";
    }

    private static final long ONE_KB = 1024;
    private static final long ONE_MB = ONE_KB * ONE_KB;
    private static final long ONE_GB = ONE_KB * ONE_MB;

    /**
     * Return good default units based on byte size.
     */
    private static String humanReadableUnits(long bytes, DecimalFormat df) {
        String newSizeAndUnits;

        if (bytes / ONE_GB > 0) {
            newSizeAndUnits = String.valueOf(df.format((float)bytes / ONE_GB)) + " GB";
        } else if (bytes / ONE_MB > 0) {
            newSizeAndUnits = String.valueOf(df.format((float)bytes / ONE_MB)) + " MB";
        } else if (bytes / ONE_KB > 0) {
            newSizeAndUnits = String.valueOf(df.format((float)bytes / ONE_KB)) + " KB";
        } else {
            newSizeAndUnits = String.valueOf(bytes) + " bytes";
        }

        return newSizeAndUnits;
    }

    private static String convertTime(long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}
