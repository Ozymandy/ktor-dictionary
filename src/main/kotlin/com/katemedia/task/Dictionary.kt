package com.katemedia.task

import kotlinx.serialization.Serializable

@Serializable
data class Dictionary(val name: String,
                      val counter: Int
)
