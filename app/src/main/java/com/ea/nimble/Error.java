/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Parcel
 *  android.os.Parcelable
 *  android.os.Parcelable$Creator
 */
package com.ea.nimble;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class Error
extends Exception
implements Parcelable,
Externalizable {
    public static final Parcelable.Creator<Error> CREATOR = new Parcelable.Creator<Error>(){

        public Error createFromParcel(Parcel parcel) {
            return new Error(parcel);
        }

        public Error[] newArray(int n2) {
            return new Error[n2];
        }
    };
    public static final String ERROR_DOMAIN = "NimbleError";
    private static final long serialVersionUID = 1L;
    private int m_code;
    private String m_domain;

    public Error() {
    }

    public Error(Parcel parcel) {
        this.readFromParcel(parcel);
    }

    public Error(Code code, String string2) {
        this(code, string2, null);
    }

    public Error(Code code, String string2, Throwable throwable) {
        this(ERROR_DOMAIN, code.intValue(), string2, throwable);
    }

    public Error(String string2, int n2, String string3) {
        this(string2, n2, string3, null);
    }

    public Error(String string2, int n2, String string3, Throwable throwable) {
        super(string3, throwable);
        this.m_domain = string2;
        this.m_code = n2;
    }

    public int describeContents() {
        return 0;
    }

    public int getCode() {
        return this.m_code;
    }

    public String getDomain() {
        return this.m_domain;
    }

    public boolean isError(Code code) {
        if (this.m_code != code.intValue()) return false;
        return true;
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.m_domain = objectInput.readUTF();
        this.m_code = objectInput.readInt();
        this.initCause((Throwable)objectInput.readObject());
    }

    public void readFromParcel(Parcel parcel) {
        this.m_domain = parcel.readString();
        this.m_code = parcel.readInt();
        this.initCause((Throwable)parcel.readSerializable());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.m_domain != null && this.m_domain.length() > 0) {
            stringBuilder.append(this.m_domain).append("(");
        } else {
            stringBuilder.append("Error").append("(");
        }
        stringBuilder.append(this.m_code).append(")");
        Object object = this.getLocalizedMessage();
        if (object != null && ((String)object).length() > 0) {
            stringBuilder.append(": ").append((String)object);
        }
        if ((object = this.getCause()) == null) return stringBuilder.toString();
        stringBuilder.append("\nCaused by: ");
        StringWriter stringWriter = new StringWriter();
        ((Throwable)object).printStackTrace(new PrintWriter(stringWriter));
        stringBuilder.append(stringWriter.toString());
        return stringBuilder.toString();
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        if (this.m_domain != null && this.m_domain.length() > 0) {
            objectOutput.writeUTF(this.m_domain);
        } else {
            objectOutput.writeUTF("");
        }
        objectOutput.writeInt(this.m_code);
        objectOutput.writeObject(this.getCause());
    }

    public void writeToParcel(Parcel parcel, int n2) {
        if (this.m_domain != null && this.m_domain.length() > 0) {
            parcel.writeString(this.m_domain);
        } else {
            parcel.writeString("");
        }
        parcel.writeInt(this.m_code);
        Throwable throwable = this.getCause();
        if (throwable != null) {
            parcel.writeSerializable((Serializable)throwable);
            return;
        }
        parcel.writeSerializable((Serializable)((Object)""));
    }

    public static enum Code {
        UNKNOWN(0),
        SYSTEM_UNEXPECTED(100),
        NOT_READY(101),
        UNSUPPORTED(102),
        NOT_AVAILABLE(103),
        NOT_IMPLEMENTED(104),
        INVALID_ARGUMENT(301),
        MISSING_CALLBACK(300),
        NETWORK_UNSUPPORTED_CONNECTION_TYPE(1001),
        NETWORK_NO_CONNECTION(1002),
        NETWORK_UNREACHABLE(1003),
        NETWORK_OVERSIZE_DATA(1004),
        NETWORK_OPERATION_CANCELLED(1005),
        NETWORK_INVALID_SERVER_RESPONSE(1006),
        NETWORK_TIMEOUT(1007),
        NETWORK_CONNECTION_ERROR(1010),
        SYNERGY_SERVER_FULL(2001),
        SYNERGY_GET_DIRECTION_TIMEOUT(2002),
        SYNERGY_GET_EA_DEVICE_ID_FAILURE(2003),
        SYNERGY_VALIDATE_EA_DEVICE_ID_FAILURE(2004),
        SYNERGY_GET_ANONYMOUS_ID_FAILURE(2005),
        SYNERGY_ENVIRONMENT_UPDATE_FAILURE(2006),
        SYNERGY_PURCHASE_VERIFICATION_FAILURE(2007),
        SYNERGY_GET_NONCE_FAILURE(2008),
        SYNERGY_GET_AGE_COMPLIANCE_FAILURE(2009);

        private int m_value;

        private Code(int n3) {
            this.m_value = n3;
        }

        public int intValue() {
            return this.m_value;
        }
    }
}

