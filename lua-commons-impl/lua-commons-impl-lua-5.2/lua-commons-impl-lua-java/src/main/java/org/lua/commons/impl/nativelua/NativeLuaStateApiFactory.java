package org.lua.commons.impl.nativelua;

import org.lua.commons.nativeapi.LuaStateApi;
import org.lua.commons.nativeapi.LuaStateApiFactory;
import org.lua.commons.nativeapi.LuaStateApiProvider;

import java.io.*;

public class NativeLuaStateApiFactory implements LuaStateApiFactory {

    static {
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        if (osArch.contains("64")) {
            osArch = "64";
        } else {
            osArch = "32";
        }
        String libName = "lua2java-lib-" + osName.toLowerCase() + osArch;
        try {
            File tmpFile = File.createTempFile("lua2java", ".lib");
            OutputStream out = new FileOutputStream(tmpFile);
            InputStream in = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(libName);
            try {
                int length = 0;
                byte[] buf = new byte[10 * 1024];
                while (length >= 0) {
                    out.write(buf, 0, length);
                    length = in.read(buf);
                }
            } finally {
                in.close();
                out.close();
            }
            System.load(tmpFile.getAbsolutePath());
            tmpFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
    }

    public LuaStateApi getLuaStateApi() {
        return new NativeLuaStateApi();
    }

}
