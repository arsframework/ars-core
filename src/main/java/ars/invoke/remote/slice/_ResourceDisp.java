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
public abstract class _ResourceDisp extends Ice.ObjectImpl implements Resource {
    protected void ice_copyStateFrom(Ice.Object __obj) throws java.lang.CloneNotSupportedException {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids = {"::Ice::Object", "::ars::invoke::remote::slice::Resource"};

    public boolean ice_isA(String s) {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean ice_isA(String s, Ice.Current __current) {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[] ice_ids() {
        return __ids;
    }

    public String[] ice_ids(Ice.Current __current) {
        return __ids;
    }

    public String ice_id() {
        return __ids[1];
    }

    public String ice_id(Ice.Current __current) {
        return __ids[1];
    }

    public static String ice_staticId() {
        return __ids[1];
    }

    /**
     * 文件下载
     *
     * @param __cb   The callback object for the operation.
     * @param id     数据流标识
     * @param length 文件字节长度
     **/
    public final void download_async(AMD_Resource_download __cb, String id, int index, int length) {
        download_async(__cb, id, index, length, null);
    }

    /**
     * 远程调用
     * <p>
     * 如果参数包含文件，则需要先将文件上传，然后将参数名称修改为：__file_ + 实际参数名称
     *
     * @param __cb   The callback object for the operation.
     * @param client 客户标识
     * @param uri    资源地址
     **/
    public final void invoke_async(AMD_Resource_invoke __cb, String client, Itoken token, String uri,
                                   String parameter) {
        invoke_async(__cb, client, token, uri, parameter, null);
    }

    /**
     * 文件上传
     *
     * @param __cb   The callback object for the operation.
     * @param name   文件名称
     * @param length 缓冲字节长度
     **/
    public final void upload_async(AMD_Resource_upload __cb, String name, byte[] buffer, int length) {
        upload_async(__cb, name, buffer, length, null);
    }

    public static Ice.DispatchStatus ___invoke(Resource __obj, IceInternal.Incoming __inS, Ice.Current __current) {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String client;
        Itoken token = null;
        String uri;
        String parameter;
        client = __is.readString();
        token = Itoken.__read(__is, token);
        uri = __is.readString();
        parameter = __is.readString();
        __inS.endReadParams();
        AMD_Resource_invoke __cb = new _AMD_Resource_invoke(__inS);
        try {
            __obj.invoke_async(__cb, client, token, uri, parameter, __current);
        } catch (java.lang.Exception ex) {
            __cb.ice_exception(ex);
        }
        return Ice.DispatchStatus.DispatchAsync;
    }

    public static Ice.DispatchStatus ___upload(Resource __obj, IceInternal.Incoming __inS, Ice.Current __current) {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String name;
        byte[] buffer;
        int length;
        name = __is.readString();
        buffer = ByteArrayHelper.read(__is);
        length = __is.readInt();
        __inS.endReadParams();
        AMD_Resource_upload __cb = new _AMD_Resource_upload(__inS);
        try {
            __obj.upload_async(__cb, name, buffer, length, __current);
        } catch (java.lang.Exception ex) {
            __cb.ice_exception(ex);
        }
        return Ice.DispatchStatus.DispatchAsync;
    }

    public static Ice.DispatchStatus ___download(Resource __obj, IceInternal.Incoming __inS, Ice.Current __current) {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String id;
        int index;
        int length;
        id = __is.readString();
        index = __is.readInt();
        length = __is.readInt();
        __inS.endReadParams();
        AMD_Resource_download __cb = new _AMD_Resource_download(__inS);
        try {
            __obj.download_async(__cb, id, index, length, __current);
        } catch (java.lang.Exception ex) {
            __cb.ice_exception(ex);
        }
        return Ice.DispatchStatus.DispatchAsync;
    }

    private final static String[] __all = {"download", "ice_id", "ice_ids", "ice_isA", "ice_ping", "invoke",
        "upload"};

    public Ice.DispatchStatus __dispatch(IceInternal.Incoming in, Ice.Current __current) {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if (pos < 0) {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
        }

        switch (pos) {
            case 0: {
                return ___download(this, in, __current);
            }
            case 1: {
                return ___ice_id(this, in, __current);
            }
            case 2: {
                return ___ice_ids(this, in, __current);
            }
            case 3: {
                return ___ice_isA(this, in, __current);
            }
            case 4: {
                return ___ice_ping(this, in, __current);
            }
            case 5: {
                return ___invoke(this, in, __current);
            }
            case 6: {
                return ___upload(this, in, __current);
            }
        }

        assert (false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    protected void __writeImpl(IceInternal.BasicStream __os) {
        __os.startWriteSlice(ice_staticId(), -1, true);
        __os.endWriteSlice();
    }

    protected void __readImpl(IceInternal.BasicStream __is) {
        __is.startReadSlice();
        __is.endReadSlice();
    }

    public static final long serialVersionUID = 0L;
}
