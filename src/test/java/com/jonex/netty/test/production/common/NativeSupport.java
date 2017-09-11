package com.jonex.netty.test.production.common;

/**
 * Created by Guest on 2017/9/12.
 */
public final class NativeSupport {

    private final static boolean SUPPORT_NATIVE_ET;

    static {
        boolean epoll = false;
        try{
            Class.forName("io.netty.channel.epoll.Native");
            epoll = true;
        }catch (Exception e){
            epoll = false;
        }
        SUPPORT_NATIVE_ET = epoll;
    }

    /**
     * The native socket transport for Linux using JNI.
     */
    public static boolean isSupportNativeEt(){
        return SUPPORT_NATIVE_ET;
    }

}
