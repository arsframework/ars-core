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
 * 请求结果
 *
 **/
public class Iresult extends Ice.ObjectImpl {
	private static class __F implements Ice.ObjectFactory {
		public Ice.Object create(String type) {
			assert (type.equals(ice_staticId()));
			return new Iresult();
		}

		public void destroy() {
		}
	}

	private static Ice.ObjectFactory _factory = new __F();

	public static Ice.ObjectFactory ice_factory() {
		return _factory;
	}

	public static final String[] __ids = { "::Ice::Object", "::ars::invoke::remote::slice::Iresult" };

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

	protected void __writeImpl(IceInternal.BasicStream __os) {
		__os.startWriteSlice(ice_staticId(), -1, true);
		__os.endWriteSlice();
	}

	protected void __readImpl(IceInternal.BasicStream __is) {
		__is.startReadSlice();
		__is.endReadSlice();
	}

	public Iresult clone() {
		return (Iresult) super.clone();
	}

	public static final long serialVersionUID = -1397731367L;
}
