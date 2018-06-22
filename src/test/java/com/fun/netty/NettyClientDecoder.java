/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fun.netty;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 协议解码器
 *
 * @author shijia.wxr<vintage.wang       @       gmail.com>
 * @since 2013-7-13
 */
public class NettyClientDecoder extends LineBasedFrameDecoder {

    private static final Log log = LogFactory.getLog(NettyClientDecoder.class);

    public NettyClientDecoder(int maxLength) {
        super(maxLength);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        try {
            ByteBuf buf = (ByteBuf)super.decode(ctx, in);
            if (buf == null) {
                return null;
            }
            String jsonValue = buf.toString(NettySystemConfig.DefaultCharset);
            if (StringUtils.isBlank(jsonValue)) {
                return null;
            }
            if(jsonValue.startsWith(">")){
                jsonValue = jsonValue.substring(1);
            }
            System.out.println("client decode==>" + jsonValue);
            RemotingCommand command = JSON.parseObject(jsonValue, RemotingCommand.class);
            System.out.println("receive message==>" + command.getValue());
            return command;
        } catch (Throwable e) {
            log.error("decode exception, " + RemotingUtil.parseChannelRemoteAddr(ctx.channel()), e);
            // 这里关闭后， 会在pipeline中产生事件，通过具体的close事件来清理数据结构
            RemotingUtil.closeChannel(ctx.channel());
        }

        return null;
    }
}
