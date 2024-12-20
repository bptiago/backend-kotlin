package br.pucpr.authserver.utils


enum class SortDir {
    ASC, DESC;

    companion object {
        fun getByName(name: String?): SortDir? {
            val dir = name?.uppercase() ?: "ASC"
            return entries.firstOrNull { it.name == dir }
        }
    }
}
