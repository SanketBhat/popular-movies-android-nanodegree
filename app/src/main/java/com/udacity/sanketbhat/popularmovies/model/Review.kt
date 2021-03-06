/*
 * Copyright 2018 Sanket Bhat
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.udacity.sanketbhat.popularmovies.model

import com.google.gson.annotations.SerializedName

//Some methods are used by retrofit API
class Review {
    @SerializedName("author")
    var author: String? = null

    @SerializedName("content")
    var content: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("url")
    var url: String? = null
}