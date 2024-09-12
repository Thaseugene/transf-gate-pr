package com.trans.transcript.configuration

object TranscriptionConfiguration {

    val TRANSCRIPTION_API_TOKEN: String = System.getenv("TRANSCRIPTION_API_TOKEN") ?: "some-token"

}
