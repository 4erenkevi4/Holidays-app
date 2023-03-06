package com.elinext.holidays.core.ktor.engine

import io.ktor.client.engine.*

expect class HttpEngineFactory constructor() {
    fun createEngine(): HttpClientEngineFactory<HttpClientEngineConfig>
}