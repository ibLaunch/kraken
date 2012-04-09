// **********************************************************************
//
// Copyright (c) 2003-2010 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.4.1

package Ice;

// <auto-generated>
//
// Generated from file `Locator.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


/**
 * The Ice locator interface. This interface is used by clients to
 * lookup adapters and objects. It is also used by servers to get the
 * locator registry proxy.
 * 
 * <p class="Note">The {@link Locator} interface is intended to be used by
 * Ice internals and by locator implementations. Regular user code
 * should not attempt to use any functionality of this interface
 * directly.
 * 
 **/
public abstract class _LocatorDisp extends Ice.ObjectImpl implements Locator
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::Ice::Locator",
        "::Ice::Object"
    };

    public boolean
    ice_isA(String s)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean
    ice_isA(String s, Ice.Current __current)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[]
    ice_ids()
    {
        return __ids;
    }

    public String[]
    ice_ids(Ice.Current __current)
    {
        return __ids;
    }

    public String
    ice_id()
    {
        return __ids[0];
    }

    public String
    ice_id(Ice.Current __current)
    {
        return __ids[0];
    }

    public static String
    ice_staticId()
    {
        return __ids[0];
    }

    /**
     * Find an adapter by id and return its proxy (a dummy direct
     * proxy created by the adapter).
     * 
     * @param __cb The callback object for the operation.
     * @param id The adapter id.
     * 
     **/
    public final void
    findAdapterById_async(AMD_Locator_findAdapterById __cb, String id)
        throws AdapterNotFoundException
    {
        findAdapterById_async(__cb, id, null);
    }

    /**
     * Find an object by identity and return its proxy.
     * 
     * @param __cb The callback object for the operation.
     * @param id The identity.
     * 
     **/
    public final void
    findObjectById_async(AMD_Locator_findObjectById __cb, Identity id)
        throws ObjectNotFoundException
    {
        findObjectById_async(__cb, id, null);
    }

    /**
     * Get the locator registry.
     * 
     * @return The locator registry.
     * 
     **/
    public final LocatorRegistryPrx
    getRegistry()
    {
        return getRegistry(null);
    }

    public static Ice.DispatchStatus
    ___findObjectById(Locator __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Idempotent, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        Identity id;
        id = new Identity();
        id.__read(__is);
        __is.endReadEncaps();
        AMD_Locator_findObjectById __cb = new _AMD_Locator_findObjectById(__inS);
        try
        {
            __obj.findObjectById_async(__cb, id, __current);
        }
        catch(java.lang.Exception ex)
        {
            __cb.ice_exception(ex);
        }
        return Ice.DispatchStatus.DispatchAsync;
    }

    public static Ice.DispatchStatus
    ___findAdapterById(Locator __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Idempotent, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        __is.endReadEncaps();
        AMD_Locator_findAdapterById __cb = new _AMD_Locator_findAdapterById(__inS);
        try
        {
            __obj.findAdapterById_async(__cb, id, __current);
        }
        catch(java.lang.Exception ex)
        {
            __cb.ice_exception(ex);
        }
        return Ice.DispatchStatus.DispatchAsync;
    }

    public static Ice.DispatchStatus
    ___getRegistry(Locator __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Idempotent, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        LocatorRegistryPrx __ret = __obj.getRegistry(__current);
        LocatorRegistryPrxHelper.__write(__os, __ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "findAdapterById",
        "findObjectById",
        "getRegistry",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping"
    };

    public Ice.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return ___findAdapterById(this, in, __current);
            }
            case 1:
            {
                return ___findObjectById(this, in, __current);
            }
            case 2:
            {
                return ___getRegistry(this, in, __current);
            }
            case 3:
            {
                return ___ice_id(this, in, __current);
            }
            case 4:
            {
                return ___ice_ids(this, in, __current);
            }
            case 5:
            {
                return ___ice_isA(this, in, __current);
            }
            case 6:
            {
                return ___ice_ping(this, in, __current);
            }
        }

        assert(false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeTypeId(ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
        if(__rid)
        {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type Ice::Locator was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type Ice::Locator was not generated with stream support";
        throw ex;
    }
}