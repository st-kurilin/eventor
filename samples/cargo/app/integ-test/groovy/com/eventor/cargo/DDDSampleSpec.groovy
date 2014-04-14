package com.eventor.cargo
import geb.Page
import geb.spock.GebReportingSpec
import spock.lang.Shared

import static java.nio.file.Files.copy
import static java.nio.file.Paths.get

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
        then: assertCargoDetails(true)
        where: trackingId << trackingIds
    }

    def "Book and route new cargo on Cargo Administration page"() {
        when: bookNewCargo(origin, destination, arrivalDeadline)
        then: assertCargoDetails(false)
        when: routeNewCargo(1)
        then: assertCargoDetails(true)
        where: [origin, destination, arrivalDeadline] << [['CNHGH', 'NLRTM', '1/1/2345']]
    }

    def "Events will generate through file"() {
        setup: copy(get(testFilesPath, file), get(uploadPath, file))
        Thread.sleep(2000)
        when: trackCargo(trackingIds[0])
        then: assertTrackingCargo(trackingIds[0], currentStatus, misdirected)
        where: [file, currentStatus, misdirected] <<
                [['handling_events.csv', 'In port Helsinki', false], ['handling_wrong_event.csv', 'Onboard voyage 0400S', true]]
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
        routeThisCargo.click()
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

    boolean assertCargoDetails(boolean routed) {
        assert caption.contains("Details for cargo")
        if (routed) {
            assert itinerary
        } else {
            assert routeThisCargo
        }
        return true
    }

    @Shared
    def trackingIds = ['ABC123', 'JKL567']
    // todo temporary solve. also, it only works in IdeaJ,
    // see: http://forums.gradle.org/gradle/topics/static_resource_files_not_found_from_from_test_code_via_gradle
    def uploadPath = 'samples/cargo/app/integ-test/resources/upload'
    def testFilesPath = 'samples/cargo/app/integ-test/resources/files'
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
        routeThisCargo { $("a", text: "Route this cargo") }
        origin { $("select", name: "originUnlocode") }
        destination { $("select", name: "destinationUnlocode") }
        arrivalDeadline { $("input", name: "arrivalDeadline") }
        book { $("input", value: "Book") }
        assignCargoToRoute { i -> $("input", value: "Assign cargo to this route")[i - 1] }
    }
}
