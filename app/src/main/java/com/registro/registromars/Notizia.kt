package com.registro.registromars

class Notizia {
    var title: String? = null
    var link: String? = null
    var description: String? = null

    override fun toString(): String {
        return " Title = $title\n link = $link\n description = $description"
    }
}