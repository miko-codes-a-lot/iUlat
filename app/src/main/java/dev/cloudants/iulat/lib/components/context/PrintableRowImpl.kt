package dev.cloudants.iulat.lib.components.context

data class PrintableRowImpl(
    private val _columns: List<Any>
) : PrintableRow {
    override fun getColumns(): List<Any> = _columns
}
