package net.orbu.corvet.web.dto

data class Failures(val failures: MutableList<Failure> = ArrayList()) {
    constructor(vararg failures: Failure) : this(failures.toMutableList())
}

data class Failure(val message: String)
