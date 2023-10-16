/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.ea.nimble;

import com.ea.nimble.Error;
import com.ea.nimble.IHttpResponse;
import com.ea.nimble.ISynergyResponse;
import com.ea.nimble.SynergyServerError;
import com.ea.nimble.Utility;
import java.util.Map;
import org.json.JSONObject;

public class SynergyResponse
implements ISynergyResponse {
    public Error error = null;
    public IHttpResponse httpResponse = null;
    public Map<String, Object> jsonData = null;

    @Override
    public Exception getError() {
        if (this.error != null) return this.error;
        if (this.httpResponse != null) return this.httpResponse.getError();
        return this.error;
    }

    @Override
    public IHttpResponse getHttpResponse() {
        return this.httpResponse;
    }

    @Override
    public Map<String, Object> getJsonData() {
        return this.jsonData;
    }

    @Override
    public boolean isCompleted() {
        if (this.httpResponse != null) return this.httpResponse.isCompleted();
        return false;
    }

    public void parseData() {
        if (this.jsonData != null) {
            return;
        }
        if (this.httpResponse != null && this.httpResponse.getError() == null) {
            String string2 = "<empty>";
            try {
                String string3;
                string2 = string3 = Utility.readStringFromStream(this.httpResponse.getDataStream());
                this.jsonData = Utility.convertJSONObjectToMap(new JSONObject(string3));
                if (!this.jsonData.containsKey("resultCode")) return;
                int n2 = (Integer)this.jsonData.get("resultCode");
                if (n2 >= 0) return;
                this.error = new SynergyServerError(n2, (String)this.jsonData.get("message"));
                return;
            }
            catch (Exception exception) {
                this.jsonData = null;
                this.error = new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Unparseable synergy json response " + string2);
                return;
            }
        }
        this.jsonData = null;
        this.error = null;
    }
}

