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
 * 文件下载
 * 
 **/

public abstract class Callback_Resource_download extends IceInternal.TwowayCallback
		implements Ice.TwowayCallbackArg1<byte[]> {
	public final void __completed(Ice.AsyncResult __result) {
		ResourcePrxHelper.__download_completed(this, __result);
	}
}