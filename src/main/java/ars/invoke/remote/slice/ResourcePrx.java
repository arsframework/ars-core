// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.6.2
//
// <auto-generated>
//
// Generated from file `ars-remote.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package ars.invoke.remote.slice;

/**
 * 远程资源
 **/
public interface ResourcePrx extends Ice.ObjectPrx {
    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client    客户标识
     * @param token     令牌对象
     * @param uri       资源地址
     * @param parameter 请求参数（JSON格式）
     * @return 结果对象
     **/
    public Iresult invoke(String client, Itoken token, String uri, String parameter);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client    客户标识
     * @param token     令牌对象
     * @param uri       资源地址
     * @param parameter 请求参数（JSON格式）
     * @param __ctx     The Context map to send with the invocation.
     * @return 结果对象
     **/
    public Iresult invoke(String client, Itoken token, String uri, String parameter,
                          java.util.Map<String, String> __ctx);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client    客户标识
     * @param token     令牌对象
     * @param uri       资源地址
     * @param parameter 请求参数（JSON格式）
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_invoke(String client, Itoken token, String uri, String parameter);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client    客户标识
     * @param token     令牌对象
     * @param uri       资源地址
     * @param parameter 请求参数（JSON格式）
     * @param __ctx     The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_invoke(String client, Itoken token, String uri, String parameter,
                                        java.util.Map<String, String> __ctx);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client    客户标识
     * @param token     令牌对象
     * @param uri       资源地址
     * @param parameter 请求参数（JSON格式）
     * @param __cb      The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_invoke(String client, Itoken token, String uri, String parameter, Ice.Callback __cb);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client    客户标识
     * @param token     令牌对象
     * @param uri       资源地址
     * @param parameter 请求参数（JSON格式）
     * @param __ctx     The Context map to send with the invocation.
     * @param __cb      The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_invoke(String client, Itoken token, String uri, String parameter,
                                        java.util.Map<String, String> __ctx, Ice.Callback __cb);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client    客户标识
     * @param token     令牌对象
     * @param uri       资源地址
     * @param parameter 请求参数（JSON格式）
     * @param __cb      The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_invoke(String client, Itoken token, String uri, String parameter,
                                        Callback_Resource_invoke __cb);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client    客户标识
     * @param token     令牌对象
     * @param uri       资源地址
     * @param parameter 请求参数（JSON格式）
     * @param __ctx     The Context map to send with the invocation.
     * @param __cb      The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_invoke(String client, Itoken token, String uri, String parameter,
                                        java.util.Map<String, String> __ctx, Callback_Resource_invoke __cb);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client        客户标识
     * @param token         令牌对象
     * @param uri           资源地址
     * @param parameter     请求参数（JSON格式）
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_invoke(String client, Itoken token, String uri, String parameter,
                                        IceInternal.Functional_GenericCallback1<Iresult> __responseCb,
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client        客户标识
     * @param token         令牌对象
     * @param uri           资源地址
     * @param parameter     请求参数（JSON格式）
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb      The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_invoke(String client, Itoken token, String uri, String parameter,
                                        IceInternal.Functional_GenericCallback1<Iresult> __responseCb,
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb,
                                        IceInternal.Functional_BoolCallback __sentCb);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client        客户标识
     * @param token         令牌对象
     * @param uri           资源地址
     * @param parameter     请求参数（JSON格式）
     * @param __ctx         The Context map to send with the invocation.
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_invoke(String client, Itoken token, String uri, String parameter,
                                        java.util.Map<String, String> __ctx, IceInternal.Functional_GenericCallback1<Iresult> __responseCb,
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param client        客户标识
     * @param token         令牌对象
     * @param uri           资源地址
     * @param parameter     请求参数（JSON格式）
     * @param __ctx         The Context map to send with the invocation.
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb      The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_invoke(String client, Itoken token, String uri, String parameter,
                                        java.util.Map<String, String> __ctx, IceInternal.Functional_GenericCallback1<Iresult> __responseCb,
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb,
                                        IceInternal.Functional_BoolCallback __sentCb);

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param __result The asynchronous result object.
     * @return 结果对象
     **/
    public Iresult end_invoke(Ice.AsyncResult __result);

    /**
     * 文件上传
     *
     * @param name   文件名称
     * @param buffer 文件字节数组缓冲区
     * @param length 缓冲字节长度
     **/
    public void upload(String name, byte[] buffer, int length);

    /**
     * 文件上传
     *
     * @param name   文件名称
     * @param buffer 文件字节数组缓冲区
     * @param length 缓冲字节长度
     * @param __ctx  The Context map to send with the invocation.
     **/
    public void upload(String name, byte[] buffer, int length, java.util.Map<String, String> __ctx);

    /**
     * 文件上传
     *
     * @param name   文件名称
     * @param buffer 文件字节数组缓冲区
     * @param length 缓冲字节长度
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_upload(String name, byte[] buffer, int length);

    /**
     * 文件上传
     *
     * @param name   文件名称
     * @param buffer 文件字节数组缓冲区
     * @param length 缓冲字节长度
     * @param __ctx  The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_upload(String name, byte[] buffer, int length, java.util.Map<String, String> __ctx);

    /**
     * 文件上传
     *
     * @param name   文件名称
     * @param buffer 文件字节数组缓冲区
     * @param length 缓冲字节长度
     * @param __cb   The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_upload(String name, byte[] buffer, int length, Ice.Callback __cb);

    /**
     * 文件上传
     *
     * @param name   文件名称
     * @param buffer 文件字节数组缓冲区
     * @param length 缓冲字节长度
     * @param __ctx  The Context map to send with the invocation.
     * @param __cb   The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_upload(String name, byte[] buffer, int length, java.util.Map<String, String> __ctx,
                                        Ice.Callback __cb);

    /**
     * 文件上传
     *
     * @param name   文件名称
     * @param buffer 文件字节数组缓冲区
     * @param length 缓冲字节长度
     * @param __cb   The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_upload(String name, byte[] buffer, int length, Callback_Resource_upload __cb);

    /**
     * 文件上传
     *
     * @param name   文件名称
     * @param buffer 文件字节数组缓冲区
     * @param length 缓冲字节长度
     * @param __ctx  The Context map to send with the invocation.
     * @param __cb   The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_upload(String name, byte[] buffer, int length, java.util.Map<String, String> __ctx,
                                        Callback_Resource_upload __cb);

    /**
     * 文件上传
     *
     * @param name          文件名称
     * @param buffer        文件字节数组缓冲区
     * @param length        缓冲字节长度
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_upload(String name, byte[] buffer, int length,
                                        IceInternal.Functional_VoidCallback __responseCb,
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * 文件上传
     *
     * @param name          文件名称
     * @param buffer        文件字节数组缓冲区
     * @param length        缓冲字节长度
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb      The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_upload(String name, byte[] buffer, int length,
                                        IceInternal.Functional_VoidCallback __responseCb,
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb,
                                        IceInternal.Functional_BoolCallback __sentCb);

    /**
     * 文件上传
     *
     * @param name          文件名称
     * @param buffer        文件字节数组缓冲区
     * @param length        缓冲字节长度
     * @param __ctx         The Context map to send with the invocation.
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_upload(String name, byte[] buffer, int length, java.util.Map<String, String> __ctx,
                                        IceInternal.Functional_VoidCallback __responseCb,
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * 文件上传
     *
     * @param name          文件名称
     * @param buffer        文件字节数组缓冲区
     * @param length        缓冲字节长度
     * @param __ctx         The Context map to send with the invocation.
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb      The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_upload(String name, byte[] buffer, int length, java.util.Map<String, String> __ctx,
                                        IceInternal.Functional_VoidCallback __responseCb,
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb,
                                        IceInternal.Functional_BoolCallback __sentCb);

    /**
     * 文件上传
     *
     * @param __result The asynchronous result object.
     **/
    public void end_upload(Ice.AsyncResult __result);

    /**
     * 文件下载
     *
     * @param id     数据流标识
     * @param index  文件流开始位置
     * @param length 文件字节长度
     * @return 文件数据字节数组
     **/
    public byte[] download(String id, int index, int length);

    /**
     * 文件下载
     *
     * @param id     数据流标识
     * @param index  文件流开始位置
     * @param length 文件字节长度
     * @param __ctx  The Context map to send with the invocation.
     * @return 文件数据字节数组
     **/
    public byte[] download(String id, int index, int length, java.util.Map<String, String> __ctx);

    /**
     * 文件下载
     *
     * @param id     数据流标识
     * @param index  文件流开始位置
     * @param length 文件字节长度
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_download(String id, int index, int length);

    /**
     * 文件下载
     *
     * @param id     数据流标识
     * @param index  文件流开始位置
     * @param length 文件字节长度
     * @param __ctx  The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_download(String id, int index, int length, java.util.Map<String, String> __ctx);

    /**
     * 文件下载
     *
     * @param id     数据流标识
     * @param index  文件流开始位置
     * @param length 文件字节长度
     * @param __cb   The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_download(String id, int index, int length, Ice.Callback __cb);

    /**
     * 文件下载
     *
     * @param id     数据流标识
     * @param index  文件流开始位置
     * @param length 文件字节长度
     * @param __ctx  The Context map to send with the invocation.
     * @param __cb   The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_download(String id, int index, int length, java.util.Map<String, String> __ctx,
                                          Ice.Callback __cb);

    /**
     * 文件下载
     *
     * @param id     数据流标识
     * @param index  文件流开始位置
     * @param length 文件字节长度
     * @param __cb   The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_download(String id, int index, int length, Callback_Resource_download __cb);

    /**
     * 文件下载
     *
     * @param id     数据流标识
     * @param index  文件流开始位置
     * @param length 文件字节长度
     * @param __ctx  The Context map to send with the invocation.
     * @param __cb   The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_download(String id, int index, int length, java.util.Map<String, String> __ctx,
                                          Callback_Resource_download __cb);

    /**
     * 文件下载
     *
     * @param id            数据流标识
     * @param index         文件流开始位置
     * @param length        文件字节长度
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_download(String id, int index, int length,
                                          IceInternal.Functional_GenericCallback1<byte[]> __responseCb,
                                          IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * 文件下载
     *
     * @param id            数据流标识
     * @param index         文件流开始位置
     * @param length        文件字节长度
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb      The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_download(String id, int index, int length,
                                          IceInternal.Functional_GenericCallback1<byte[]> __responseCb,
                                          IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb,
                                          IceInternal.Functional_BoolCallback __sentCb);

    /**
     * 文件下载
     *
     * @param id            数据流标识
     * @param index         文件流开始位置
     * @param length        文件字节长度
     * @param __ctx         The Context map to send with the invocation.
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_download(String id, int index, int length, java.util.Map<String, String> __ctx,
                                          IceInternal.Functional_GenericCallback1<byte[]> __responseCb,
                                          IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * 文件下载
     *
     * @param id            数据流标识
     * @param index         文件流开始位置
     * @param length        文件字节长度
     * @param __ctx         The Context map to send with the invocation.
     * @param __responseCb  The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb      The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_download(String id, int index, int length, java.util.Map<String, String> __ctx,
                                          IceInternal.Functional_GenericCallback1<byte[]> __responseCb,
                                          IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb,
                                          IceInternal.Functional_BoolCallback __sentCb);

    /**
     * 文件下载
     *
     * @param __result The asynchronous result object.
     * @return 文件数据字节数组
     **/
    public byte[] end_download(Ice.AsyncResult __result);
}
