package com.eventor.cargo.pages

import geb.Page

class CargoAdministrationPage extends Page {
    static at = { caption == "All cargos" }
    static content = {
        caption { $("caption").text() }
        allCargos { $("tbody tr")*.text() }
        cargoLink { name -> $("tbody a").find { item -> item.text() == name } }
        listAllCargoLink { $("a", text: "List all cargos") }
        bookNewCargoLink { $("a", text: "Book new cargo") }
    }
}
