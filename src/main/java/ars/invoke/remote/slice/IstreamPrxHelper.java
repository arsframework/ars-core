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
 * Provides type-specific helper functions.
 **/
public final class IstreamPrxHelper extends Ice.ObjectPrxHelperBase implements IstreamPrx {
	/**
	 * Contacts the remote server to verify that the object implements this type.
	 * Raises a local exception if a communication error occurs.
	 * 
	 * @param __obj
	 *            The untyped proxy.
	 * @return A proxy for this type, or null if the object does not support this
	 *         type.
	 **/
	public static IstreamPrx checkedCast(Ice.ObjectPrx __obj) {
		return checkedCastImpl(__obj, ice_staticId(), IstreamPrx.class, IstreamPrxHelper.class);
	}

	/**
	 * Contacts the remote server to verify that the object implements this type.
	 * Raises a local exception if a communication error occurs.
	 * 
	 * @param __obj
	 *            The untyped proxy.
	 * @param __ctx
	 *            The Context map to send with the invocation.
	 * @return A proxy for this type, or null if the object does not support this
	 *         type.
	 **/
	public static IstreamPrx checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx) {
		return checkedCastImpl(__obj, __ctx, ice_staticId(), IstreamPrx.class, IstreamPrxHelper.class);
	}

	/**
	 * Contacts the remote server to verify that a facet of the object implements
	 * this type. Raises a local exception if a communication error occurs.
	 * 
	 * @param __obj
	 *            The untyped proxy.
	 * @param __facet
	 *            The name of the desired facet.
	 * @return A proxy for this type, or null if the object does not support this
	 *         type.
	 **/
	public static IstreamPrx checkedCast(Ice.ObjectPrx __obj, String __facet) {
		return checkedCastImpl(__obj, __facet, ice_staticId(), IstreamPrx.class, IstreamPrxHelper.class);
	}

	/**
	 * Contacts the remote server to verify that a facet of the object implements
	 * this type. Raises a local exception if a communication error occurs.
	 * 
	 * @param __obj
	 *            The untyped proxy.
	 * @param __facet
	 *            The name of the desired facet.
	 * @param __ctx
	 *            The Context map to send with the invocation.
	 * @return A proxy for this type, or null if the object does not support this
	 *         type.
	 **/
	public static IstreamPrx checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx) {
		return checkedCastImpl(__obj, __facet, __ctx, ice_staticId(), IstreamPrx.class, IstreamPrxHelper.class);
	}

	/**
	 * Downcasts the given proxy to this type without contacting the remote server.
	 * 
	 * @param __obj
	 *            The untyped proxy.
	 * @return A proxy for this type.
	 **/
	public static IstreamPrx uncheckedCast(Ice.ObjectPrx __obj) {
		return uncheckedCastImpl(__obj, IstreamPrx.class, IstreamPrxHelper.class);
	}

	/**
	 * Downcasts the given proxy to this type without contacting the remote server.
	 * 
	 * @param __obj
	 *            The untyped proxy.
	 * @param __facet
	 *            The name of the desired facet.
	 * @return A proxy for this type.
	 **/
	public static IstreamPrx uncheckedCast(Ice.ObjectPrx __obj, String __facet) {
		return uncheckedCastImpl(__obj, __facet, IstreamPrx.class, IstreamPrxHelper.class);
	}

	public static final String[] __ids = { "::Ice::Object", "::ars::invoke::remote::slice::Iresult",
			"::ars::invoke::remote::slice::Istream" };

	/**
	 * Provides the Slice type ID of this type.
	 * 
	 * @return The Slice type ID.
	 **/
	public static String ice_staticId() {
		return __ids[2];
	}

	public static void __write(IceInternal.BasicStream __os, IstreamPrx v) {
		__os.writeProxy(v);
	}

	public static IstreamPrx __read(IceInternal.BasicStream __is) {
		Ice.ObjectPrx proxy = __is.readProxy();
		if (proxy != null) {
			IstreamPrxHelper result = new IstreamPrxHelper();
			result.__copyFrom(proxy);
			return result;
		}
		return null;
	}

	public static final long serialVersionUID = 0L;
}
