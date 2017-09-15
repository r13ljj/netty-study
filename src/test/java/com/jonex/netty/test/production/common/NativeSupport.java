package com.jonex.netty.test.production.common;

/**
 * Created by Guest on 2017/9/12.
 */
public final class NativeSupport {

    private final static boolean SUPPORT_NATIVE_ET;
    public static final String OS_NAME = System.getProperty("os.name");
    private static boolean isLinuxPlatform = false;

    static {
        boolean epoll = false;
        /*try{
            Class.forName("io.netty.channel.epoll.Native");
            epoll = true;
        }catch (Exception e){
            epoll = false;
        }*/
        SUPPORT_NATIVE_ET = epoll;
        if (OS_NAME != null && OS_NAME.toLowerCase().indexOf("linux") >= 0) {
            isLinuxPlatform = true;
        }
    }

    /**
     * The native socket transport for Linux using JNI.
     */
    public static boolean isSupportNativeEt(){
        return SUPPORT_NATIVE_ET;
    }

    public static boolean isLinuxPlatform() {
        return isLinuxPlatform;
    }

}
