package us.skidrevenant.cuckprotocolapi.adapter.adapters;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;
import us.skidrevenant.cuckprotocolapi.adapter.ProtocolAdapter;
import us.skidrevenant.cuckprotocolapi.packet.PacketDirection;
import us.skidrevenant.cuckprotocolapi.util.PacketUtil;

/**
 * @author SkidRevenant
 * at 03/05/2018
 */
public class NewProtocolAdapter implements ProtocolAdapter {

    @Override
    public void addAdapter(Object channelObject, Player player) {
        Channel channel = (Channel) channelObject;

        channel.pipeline().addBefore("packet_handler", "cuckprotocol_api", new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                try {

                    if (!PacketUtil.processPacket(msg, PacketDirection.INBOUND, player)) {
                        return;//event is cancelled, don't process packet
                    }

                } catch (Exception e) {
                    e.printStackTrace(); //otherwise the player will be kicked
                }

                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                try {

                    if (!PacketUtil.processPacket(msg, PacketDirection.OUTBOUND, player)) {
                        return; //event is cancelled, don't process packet
                    }

                } catch (Exception e) {
                    e.printStackTrace(); //otherwise the player will be kicked
                }

                super.write(ctx, msg, promise);
            }
        });
    }

    @Override
    public void removeAdapter(Object channelObject, Player player) {
        Channel channel = (Channel) channelObject;

        channel.pipeline().remove("cuckprotocol_api");
    }
}
