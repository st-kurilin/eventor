package com.eventor.cargo.pages

import geb.Page

class BookNewCargoPage extends Page {
    static at = { caption == "Book new cargo" }
    static content = {
        caption { $("caption").text() }
        origin { $("select", name: "originUnlocode") }
        destination { $("select", name: "destinationUnlocode") }
        arrivalDeadline { $("input", name: "arrivalDeadline") }
        book { $("input", value: "Book") }
    }
}
