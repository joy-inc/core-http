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

package com.joy.http.volley;

/**
 * Encapsulates a parsed response for delivery.
 *
 * @param <T> Parsed type of this response
 */
public class Result<T> {

    /** Returns a successful response containing the parsed result. */
    public static <T> Result<T> success(T result) {
        return new Result<>(result);
    }

    /**
     * Returns a failed response containing the given error code and an optional
     * localized message displayed to the user.
     */
    public static <T> Result<T> error(Throwable error) {
        return new Result<>(error);
    }

    /** Parsed response, or null in the case of error. */
    public final T result;

    /** Detailed error information if <code>errorCode != OK</code>. */
    public final Throwable error;

    /**
     * Returns whether this response is considered successful.
     */
    public boolean isSuccess() {
        return error == null;
    }

    private Result(T result) {
        this.result = result;
        this.error = null;
    }

    private Result(Throwable error) {
        this.result = null;
        this.error = error;
    }
}
