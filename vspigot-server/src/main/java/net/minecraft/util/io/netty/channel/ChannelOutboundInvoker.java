//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.io.netty.channel;

import java.net.SocketAddress;

interface ChannelOutboundInvoker {
    ChannelFuture bind(SocketAddress var1);

    ChannelFuture connect(SocketAddress var1);

    ChannelFuture connect(SocketAddress var1, SocketAddress var2);

    ChannelFuture disconnect();

    ChannelFuture close();

    /** @deprecated */
    @Deprecated
    ChannelFuture deregister();

    ChannelFuture bind(SocketAddress var1, ChannelPromise var2);

    ChannelFuture connect(SocketAddress var1, ChannelPromise var2);

    ChannelFuture connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3);

    ChannelFuture disconnect(ChannelPromise var1);

    ChannelFuture close(ChannelPromise var1);

    /** @deprecated */
    @Deprecated
    ChannelFuture deregister(ChannelPromise var1);

    ChannelOutboundInvoker read();

    ChannelFuture write(Object var1);

    ChannelFuture write(Object var1, ChannelPromise var2);

    ChannelOutboundInvoker flush();

    ChannelFuture writeAndFlush(Object var1, ChannelPromise var2);

    ChannelFuture writeAndFlush(Object var1);
}
