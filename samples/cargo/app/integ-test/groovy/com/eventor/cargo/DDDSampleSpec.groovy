package com.eventor.cargo

import geb.Page
import geb.spock.GebReportingSpec
import spock.lang.Shared

class DDDSampleSpec extends GebReportingSpec {

    def "Cargos will be on Cargo Administration page"() {
        when: to CargoAdministration
        then: cargos
    }

    def "Cargo info will appear on Tracking cargo page"() {
        when: trackCargo(trackingId)
        then: assertTrackingCargo(trackingId, currentStatus, misdirected)
        where: [trackingId, currentStatus, misdirected] <<
                [[trackingIds[0], 'In port New York', false], [trackingIds[1], 'Onboard voyage 0100S', true]]
    }

    def "Cargo info will appear on Cargo Administration page"() {
        when: selectCargo(trackingId)
        then: assertRoutedCargoDetails()
        where: trackingId << trackingIds
    }

    def "Book and route new cargo on Cargo Administration page"() {
        when: bookNewCargo(origin, destination, arrivalDeadline)
        then: assertCargoDetails()
        when: routeNewCargo(1)
        then: assertRoutedCargoDetails()
        where: [origin, destination, arrivalDeadline] << [['CNHGH', 'NLRTM', '1/1/2345']]
    }

    def "Change handling history for cargo on Tracking cargo pages"() {
        setup: uploadFileWith(text)
        Thread.sleep(2000)
        when: trackCargo(trackingIds[0])
        then: assertTrackingCargo(trackingIds[0], currentStatus, misdirected)
        where: [text, currentStatus, misdirected] <<
                [[events[0], 'In port Helsinki', false], [events[1], 'Onboard voyage 0400S', true]]
    }

    def setup() {
        // Runner.main()
    }

    def cleanup() {
        // todo implement stop server method
    }

    void selectCargo(String trackingIdVal) {
        to CargoAdministration
        cargoLink(trackingIdVal).click()
    }

    void trackCargo(String trackingIdVal) {
        to TrackingCargo
        trackingId << trackingIdVal
        track.click()
    }

    void bookNewCargo(String originVal, String destinationVal, String arrivalDeadlineVal) {
        to CargoAdministration
        bookNewCargoLink.click()
        origin.value(originVal)
        destination.value(destinationVal)
        arrivalDeadline << arrivalDeadlineVal
        book.click()
    }

    void routeNewCargo(int routeCandidate) {
        routeThisCargoLink.click()
        assignCargoToRoute(routeCandidate).click()
    }

    boolean assertTrackingCargo(String trackingId, String currentStatus, boolean misdirected) {
        assert status.contains(trackingId)
        assert status.contains(currentStatus)
        if (misdirected) {
            assert misdirect
        }
        return true
    }

    boolean assertRoutedCargoDetails() {
        assert caption.contains("Details for cargo")
        assert itinerary
        return true
    }

    boolean assertCargoDetails() {
        assert caption.contains("Details for cargo")
        assert routeThisCargoLink
        return true
    }

    void uploadFileWith(String text) {
        new File('samples/cargo/upload', 'handling_events.csv').withWriterAppend { w -> w << text }
    }

    @Shared
    def trackingIds = ['ABC123', 'JKL567']
    @Shared
    def ls = System.getProperty("line.separator")
    @Shared
    String[] events = ['2009-03-06 12:30	ABC123	0200T	USNYC	LOAD' + ls +
            '2009-03-08 04:00	ABC123	0200T	USDAL	UNLOAD' + ls +
            '2009-03-09 08:12	ABC123	0300A	USDAL	LOAD' + ls +
            '2009-03-12 19:25	ABC123	0300A	FIHEL	UNLOAD',
            '2009-03-13 12:30	ABC123	0400S	CNHGH	LOAD']
}

class TrackingCargo extends Page {
    static url = "http://localhost:8080/dddsample/public/track"
    static at = { title == "Tracking cargo" }
    static content = {
        trackingId { $("input", id: "idInput") }
        track { $("input", type: "submit") }
        status { $("h2").text() }
        misdirect { $("p", class: "notify").text() }
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
        routeThisCargoLink { $("a", text: "Route this cargo") }
        origin { $("select", name: "originUnlocode") }
        destination { $("select", name: "destinationUnlocode") }
        arrivalDeadline { $("input", name: "arrivalDeadline") }
        book { $("input", value: "Book") }
        assignCargoToRoute { i -> $("input", value: "Assign cargo to this route")[i - 1] }
    }
}
