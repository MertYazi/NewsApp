package com.mertyazi.newsapp.model

import java.io.Serializable

data class Source(
    val id: String,
    val name: String
): Serializable {
    override fun hashCode(): Int {
        var result = id.hashCode()
        if(name.isNullOrEmpty()){
            result = 31 * result + name.hashCode()
        }
        return result
    }
}