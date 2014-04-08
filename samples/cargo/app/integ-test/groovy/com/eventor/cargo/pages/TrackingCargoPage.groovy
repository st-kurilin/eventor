package com.eventor.cargo.pages

import geb.Page

class TrackingCargoPage extends Page {
    static at = { title == "Tracking cargo" }
    static content = {
        trackingId { $("input", id: "idInput") }
        track { $("input", type: "submit") }
        status { $("h2").text() }
        additionalInfo { $("p")*.text()*.trim().findAll { item -> !item.isEmpty() } }
    }
}