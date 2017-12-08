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

public final class ResourceHolder extends Ice.ObjectHolderBase<Resource>
{
    public
    ResourceHolder()
    {
    }

    public
    ResourceHolder(Resource value)
    {
        this.value = value;
    }

    public void
    patch(Ice.Object v)
    {
        if(v == null || v instanceof Resource)
        {
            value = (Resource)v;
        }
        else
        {
            IceInternal.Ex.throwUOE(type(), v);
        }
    }

    public String
    type()
    {
        return _ResourceDisp.ice_staticId();
    }
}
