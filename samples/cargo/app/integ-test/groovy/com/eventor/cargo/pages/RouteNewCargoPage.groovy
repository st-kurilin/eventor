package com.eventor.cargo.pages

import geb.Page

class RouteNewCargoPage extends Page {
    static at = { caption == "Select route" }
    static content = {
        caption { $("caption")*.text()[0] }
        assignCargoToRoute { i -> $("input", value: "Assign cargo to this route")[i - 1] }
        routeCandidateInfo { i -> $("tbody")[i].find("tr")*.text() }
    }
}
