package com.eventor.cargo.pages

import geb.Page

class CargoInfoPage extends Page {
    static at = { caption.contains("Details for cargo") }
    static content = {
        caption { $("caption")*.text()[0] }
        cell { title -> $("td", text: title).next().text() }
        origin { cell("Origin") }
        destination { cell("Destination") }
        arrivalDeadline { cell("Arrival deadline") }
        itineraryInfo { $("tbody")[1].find("tr")*.text() }
        routeThisCargo { $("a", text: "Route this cargo") }
    }
}