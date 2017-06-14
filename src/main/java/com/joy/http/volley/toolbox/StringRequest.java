/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.joy.http.volley.toolbox;

import com.joy.http.volley.Response;
import com.joy.http.volley.Result;
import com.joy.http.volley.ServerError;

import java.io.IOException;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class StringRequest extends JsonRequest<String> {

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the string at
     */
    public StringRequest(Method method, String url) {
        super(method, url);
    }

    @Override
    protected Result<String> parseNetworkResponse(Response response) {
        try {
            String json = toString(response);
            if (json == null) {
                return Result.error(new NullPointerException("the json string is null."));
            }
            return Result.success(json);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e);
        } catch (ServerError e) {
            e.printStackTrace();
            return Result.error(e);
        }
    }
}
