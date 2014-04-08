package com.eventor.cargo.pages

import geb.Page

class WelcomePage extends Page {
    static url = "http://localhost:8080/dddsample/"
    static at = { title == "DDDSample" }
    static content = {
        cargoTracking { $("a", href: contains("public/track")) }
        bookingAndRouting { $("a", href: contains("admin/list")) }
    }
}