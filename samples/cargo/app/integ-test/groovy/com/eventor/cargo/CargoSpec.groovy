package com.eventor.cargo

import geb.Module
import geb.Page
import geb.spock.GebReportingSpec
import groovy.time.TimeCategory
import groovyx.net.http.URIBuilder

class CargoSpec extends GebReportingSpec {

    static def SOME_DESTINATION = 1
    static def ANOTHER_DESTINATION = 2
    static def NEXT_MONTH = use(TimeCategory) {
        new Date() + 1.month
    }
    static def DEFAULT_CARGO =
            [originNumber: SOME_DESTINATION, destinationNumber: ANOTHER_DESTINATION, arrivalDeadline: NEXT_MONTH]

    def "User should not be able to book new cargo with the same values for origin and destination"() {
        when:
        book(originNumber: SOME_DESTINATION, destinationNumber: SOME_DESTINATION)
        then:
        title.contains("Error")
    }

    def "User should be able to book new cargo"() {
        when:
        def cargoBooked = book(DEFAULT_CARGO)
        then:
        assertCargoBooked(cargoBooked)
    }

    def book(newCargo) {
        to BookNewCargoPage
        def cargoInfo = [:]
        cargoInfo.origin = originValue(newCargo.originNumber ?: DEFAULT_CARGO.originNumber)
        cargoInfo.destination = destinationValue(newCargo.destinationNumber ?: DEFAULT_CARGO.destinationNumber)
        cargoInfo.arrivalDeadline = newCargo.arrivalDeadline ?: DEFAULT_CARGO.arrivalDeadline
        origin.value(cargoInfo.origin)
        destination.value(cargoInfo.destination)
        arrivalDeadline << cargoInfo.arrivalDeadline.format("M/d/yyyy")
        bookBtn.click()
        cargoInfo.trackingId = getParamVal()
        return cargoInfo
    }

    def getParamVal(param = 'trackingId') {
        def builder = new URIBuilder(driver.currentUrl)
        return builder.query != null ? builder.query.get(param) : null
    }

    def assertCargoBooked(cargoBooked) {
        assertCargoPresentedOnListOfCargos(cargoBooked)
        assertCargoAvailableOnCargoDetailsPage(cargoBooked)
    }

    def assertCargoAvailableOnCargoDetailsPage(cargoBooked) {
        to CargoDetailsPage, trackingId: cargoBooked.trackingId
        assert caption.contains(cargoBooked.trackingId)
        assert origin == cargoBooked.origin
        assert destination == cargoBooked.destination
        assert arrivalDeadline.dateString == cargoBooked.arrivalDeadline.dateString
        return true
    }

    def assertCargoPresentedOnListOfCargos(cargoBooked) {
        to AllCargosPage
        def cargoRow = cargoRows.find { it.trackingId == cargoBooked.trackingId }
        assert cargoRow
        assert cargoRow.origin == cargoBooked.origin
        assert cargoRow.destination == cargoBooked.destination
        return true
    }
}

class CargoDetailsPage extends Page {
    static url = "dddsample/admin/show.html"
    static at = { $("caption")[0].text().contains("Details for cargo") }
    static content = {
        caption { $("caption")[0].text() }
        nextCellText { t -> $("tr td", text: t).next().text() }
        origin { nextCellText("Origin") }
        destination { nextCellText("Destination") }
        arrivalDeadline { Date.parse("yyyy-MM-dd", nextCellText("Arrival deadline")) }
    }
}

class BookNewCargoPage extends Page {
    static url = "dddsample/admin/registrationForm.html"
    static at = { $("caption")[0].text() == "Book new cargo" }
    static content = {
        origin { $("select", name: "originUnlocode") }
        destination { $("select", name: "destinationUnlocode") }
        arrivalDeadline { $("input", name: "arrivalDeadline") }
        bookBtn { $("input", value: "Book") }
        originValue { i -> origin.children()*.value()[i - 1] }
        destinationValue { i -> destination.children()*.value()[i - 1] }
    }
}

class AllCargosPage extends Page {
    static url = "dddsample/admin/list.html"
    static at = { $("caption")[0].text() == "All cargos" }
    static content = {
        allCargosTable { $("caption", text: "All cargos").parent() }
        cargoRows { table -> moduleList CargoRow, allCargosTable.find("tbody tr") }
    }
}

class CargoRow extends Module {
    static content = {
        cell { $("td", it) }
        trackingId { cell(0).text() }
        origin { cell(1).text() }
        destination { cell(2).text() }
        routed { cell(3).text() }
    }
}
