package com.eventor.cargo

import geb.Page
import geb.spock.GebReportingSpec
import spock.lang.Shared

class DDDSampleSpec extends GebReportingSpec {

    def "Cargo info will appear on Tracking cargo page"() {
        setup: to TrackingCargo
        when: trackCargo(trackingId)
        then: assertCargo(trackingId, currentStatus, misdirected)
        where: trackingId << trackingIds
        currentStatus          | misdirected
        'In port New York'     | false
        'Onboard voyage 0100S' | true

    }

    def "Cargos will be on Cargo Administration page"() {
        when: to CargoAdministration
        then: cargos
    }

    def "Cargo info will appear on Cargo Administration page"() {
        setup: to CargoAdministration
        when: cargoLink(trackingId).click()
        then: assertCargo(true)
        where: trackingId << trackingIds
    }

    def "Book and route new cargo on Cargo Administration page"() {
        setup: to CargoAdministration
        when: bookNewCargo(origin, destination, arrivalDeadline)
        then: assertCargo(false)
        when: routeNewCargo(1)
        then: assertCargo(true)
        where: [origin, destination, arrivalDeadline] << [['CNHGH', 'NLRTM', '1/1/2345']]
    }

    @Shared
    def trackingIds = ['ABC123', 'JKL567']
}

class TrackingCargo extends Page {
    static url = "http://localhost:8080/dddsample/public/track"
    static at = { title == "Tracking cargo" }
    static content = {
        trackingId { $("input", id: "idInput") }
        track { $("input", type: "submit") }
        status { $("h2").text() }
        misdirected { $("p", class: "notify").text() }
    }

    void trackCargo(String id) {
        trackingId << id
        track.click()
    }

    boolean assertCargo(String trackingId, String currentStatus, boolean misdirected) {
        assert status.contains(trackingId)
        assert status.contains(currentStatus)
        if (misdirected) {
            assert misdirected
        }
        return true;
    }
}

class CargoAdministration extends Page {
    static url = "http://localhost:8080/dddsample/admin/list"
    static at = { title == "Cargo Administration" }
    static content = {
        caption { $("caption")[0].text() }
        cargos { $("tbody tr")*.text() }
        cargoLink { name -> $("tbody a").find { item -> item.text() == name } }
        listAllCargoLink { $("a", text: "List all cargos") }
        bookNewCargoLink { $("a", text: "Book new cargo") }
        itinerary { $("tbody")[1].find("tr")*.text() }
        routeThisCargo { $("a", text: "Route this cargo") }
        origin { $("select", name: "originUnlocode") }
        destination { $("select", name: "destinationUnlocode") }
        arrivalDeadline { $("input", name: "arrivalDeadline") }
        book { $("input", value: "Book") }
        assignCargoToRoute { i -> $("input", value: "Assign cargo to this route")[i - 1] }
    }

    void bookNewCargo(String originVal, String destinationVal, String arrivalDeadlineVal) {
        bookNewCargoLink.click()
        origin.value(originVal)
        destination.value(destinationVal)
        arrivalDeadline << arrivalDeadlineVal
        book.click()
    }

    void routeNewCargo(int routeCandidate) {
        routeThisCargo.click()
        assignCargoToRoute(routeCandidate).click()
    }

    boolean assertCargo(boolean routed) {
        assert caption.contains("Details for cargo")
        if (routed) {
            assert itinerary
        } else {
            assert routeThisCargo
        }
        return true;
    }
}
