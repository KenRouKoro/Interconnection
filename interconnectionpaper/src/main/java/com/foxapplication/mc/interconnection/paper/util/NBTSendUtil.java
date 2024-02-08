package com.foxapplication.mc.interconnection.paper.util;

import com.foxapplication.embed.hutool.core.io.FastByteArrayOutputStream;
import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.mc.interconnection.common.util.MessageUtil;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * NBTSendUtil类提供了一些用于发送和解析NBT数据的工具方法。
 */
public class NBTSendUtil {
    private static final Log log = LogFactory.get();

    /**
     * 将任意类型的NBT数据发送给指定的目标和服务。
     *
     * @param target  目标
     * @param service 服务
     * @param message NBT数据
     */
    public static void sendNBTAny(String target, String service, Tag message) {
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(out);
        try {
            NbtIo.writeAnyTag(message, outStream);
        } catch (IOException e) {
            //实际上这里永远不可能触发，因为这是在内存中输出
            log.error(e);
        }
        MessageUtil.send(target, service, out.toByteArray());
    }

    /**
     * 将任意类型的NBT数据以SNBT格式发送给指定的目标和服务。
     *
     * @param target  目标
     * @param service 服务
     * @param message NBT数据
     */
    public static void sendSNBTAny(String target, String service, Tag message) {
        MessageUtil.send(target, service, message.getAsString());
    }

    /**
     * 将给定的CompoundTag数据以SNBT格式发送给指定的目标和服务。
     *
     * @param target  目标
     * @param service 服务
     * @param message CompoundTag数据
     */
    public static void sendSNBT(String target, String service, CompoundTag message) {
        MessageUtil.send(target, service, NbtUtils.structureToSnbt(message));
    }

    /**
     * 将给定的CompoundTag数据发送给指定的目标和服务。
     *
     * @param target  目标
     * @param service 服务
     * @param message CompoundTag数据
     */
    public static void sendNBT(String target, String service, CompoundTag message) {
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(out);
        try {
            NbtIo.write(message, outStream);
        } catch (IOException e) {
            //实际上这里永远不可能触发，因为这是在内存中输出
            log.error(e);
        }
        MessageUtil.send(target, service, out.toByteArray());
    }

    /**
     * 解析给定的SNBT字符串为Tag对象。
     *
     * @param SNBT SNBT字符串
     * @return 解析后的Tag对象
     * @throws CommandSyntaxException 如果解析失败
     */
    public static Tag parseNBT(String SNBT) throws CommandSyntaxException {
        return NbtUtils.snbtToStructure(SNBT);
    }

    /**
     * 解析给定的NBT字节数组为Tag对象。
     *
     * @param NBT NBT字节数组
     * @return 解析后的Tag对象
     * @throws IOException 如果解析失败
     */
    public static Tag parseNBT(byte[] NBT) throws IOException {
        FastByteArrayInputStream in = new FastByteArrayInputStream(NBT);
        DataInputStream inStream = new DataInputStream(in);
        return NbtIo.read(inStream);
    }

    /**
     * 将任意类型的NBT数据转换为字节数组。
     * @param message NBT数据
     * @return 字节数组
     */
    public static byte[] NBT2BytesAny(Tag message){
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(out);
        try {
            NbtIo.writeAnyTag(message, outStream);
        } catch (IOException e) {
            //实际上这里永远不可能触发，因为这是在内存中输出
            log.error(e);
        }
        return out.toByteArray();
    }

    /**
     * 将给定的CompoundTag数据转换为字节数组。
     * @param message CompoundTag数据
     * @return 字节数组
     */
    public static byte[] NBT2Bytes(CompoundTag message){
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(out);
        try {
            NbtIo.write(message, outStream);
        } catch (IOException e) {
            //实际上这里永远不可能触发，因为这是在内存中输出
            log.error(e);
        }
        return out.toByteArray();
    }
}
