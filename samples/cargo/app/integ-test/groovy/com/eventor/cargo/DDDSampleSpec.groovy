package com.eventor.cargo

import com.eventor.cargo.pages.*
import geb.spock.GebReportingSpec

class DDDSampleSpec extends GebReportingSpec {

    def "Should open welcome DDDSample page"() {
        when:
        to WelcomePage
        then:
        at WelcomePage
    }

    def "Should open Tracing cargo page"() {
        setup:
        to WelcomePage
        when:
        cargoTracking.click()
        then:
        at TrackingCargoPage
    }

    def "Should open Cargo administration page"() {
        setup:
        to WelcomePage
        when:
        bookingAndRouting.click()
        then:
        at CargoAdministrationPage
    }

    def "Check tracking cargo info"() {
        setup:
        to WelcomePage
        cargoTracking.click()
        at TrackingCargoPage
        when:
        trackingId << trackingIdVal
        track.click()
        then:
        [status, additionalInfo] == [statusVal, additionalInfoVal]
        where:
        [trackingIdVal, statusVal, additionalInfoVal] << [
                ['ABC123', 'Cargo ABC123 is now: In port New York',
                        ['Estimated time of arrival in Helsinki: 2009-03-12 12:00',
                                'Next expected activity is to load cargo onto voyage 0200T in New York',
                                'Received in Hongkong, at 3/1/09 12:00 AM.',
                                'Loaded onto voyage 0100S in Hongkong, at 3/2/09 12:00 AM.',
                                'Unloaded off voyage 0100S in New York, at 3/5/09 12:00 AM.']],
                ['JKL567', 'Cargo JKL567 is now: Onboard voyage 0100S',
                        ['Estimated time of arrival in Stockholm: ?', 'Cargo is misdirected',
                                'Received in Hangzhou, at 3/1/09 12:00 AM.',
                                'Loaded onto voyage 0100S in Hangzhou, at 3/3/09 12:00 AM.',
                                'Unloaded off voyage 0100S in New York, at 3/5/09 12:00 AM.',
                                'Loaded onto voyage 0100S in New York, at 3/6/09 12:00 AM.']]
        ]
    }

    def "Check default cargos in All cargos table"() {
        setup:
        to WelcomePage
        when:
        bookingAndRouting.click()
        at CargoAdministrationPage
        then:
        allCargos[rowIndex] == value
        where:
        rowIndex | value
        0        | 'ABC123 CNHKG FIHEL Yes'
        1        | 'JKL567 CNHGH SESTO Yes'
    }

    def "Check default cargo info"() {
        setup:
        to WelcomePage
        bookingAndRouting.click()
        at CargoAdministrationPage
        when:
        cargoLink(row).click()
        at CargoInfoPage
        then:
        [caption, origin, destination, arrivalDeadline, itineraryInfo] == [captionVal, originVal, destinationVal,
                arrivalDeadlineVal, itenararyInfoVal]
        where:
        [row, captionVal, originVal, destinationVal, arrivalDeadlineVal, itenararyInfoVal] << [
                ['ABC123', 'Details for cargo ABC123', 'CNHKG', 'FIHEL', '2009-03-15 12:00',
                        ['0100S CNHKG (2009-03-02 12:00) USNYC (2009-03-05 12:00)',
                                '0200T USNYC (2009-03-06 12:00) USDAL (2009-03-08 12:00)',
                                '0300A USDAL (2009-03-09 12:00) FIHEL (2009-03-12 12:00)']],
                ['JKL567', 'Details for cargo JKL567', 'CNHGH', 'SESTO', '2009-03-18 12:00',
                        ['0100S CNHGH (2009-03-03 12:00) USNYC (2009-03-05 12:00)',
                                '0200T USNYC (2009-03-06 12:00) USDAL (2009-03-08 12:00)',
                                '0300A USDAL (2009-03-09 12:00) SESTO (2009-03-11 12:00)']]
        ]
    }

    def "Create new cargo"() {
        setup:
        to WelcomePage
        bookingAndRouting.click()
        at CargoAdministrationPage
        bookNewCargoLink.click()
        at BookNewCargoPage
        when:
        origin.value(originVal)
        destination.value(destinationVal)
        arrivalDeadline << arrivalDeadlineVal
        book.click()
        then:
        at CargoInfoPage
        where:
        originVal | destinationVal | arrivalDeadlineVal
        'DEHAM'   | 'USNYC'        | '1/1/2345'
        'FIHEL'   | 'DEHAM'        | '1/1/2345'
    }

    def "Route recently created cargo"() {
        setup:
        to WelcomePage
        bookingAndRouting.click()
        at CargoAdministrationPage
        bookNewCargoLink.click()
        at BookNewCargoPage
        origin.value(originVal)
        destination.value(destinationVal)
        arrivalDeadline << arrivalDeadlineVal
        book.click()
        at CargoInfoPage
        when:
        routeThisCargo.click()
        at RouteNewCargoPage
        def routeCandidateVal = routeCandidateInfo(routeCandidate)
        assignCargoToRoute(routeCandidate).click()
        then:
        at CargoInfoPage
        removeParentheses(itineraryInfo) == routeCandidateVal
        where:
        originVal | destinationVal | arrivalDeadlineVal | routeCandidate
        'FIHEL'   | 'USNYC'        | '1/1/2345'         | 1
    }

    // todo where is the place for that?
    def removeParentheses(List<String> list) {
        list.collect{ it.replaceAll("[\\(\\)]", "") }
    }

    def setupSpec() {
        // Runner.main();
    }

    def cleanupSpec() {
        // todo need to implement stop server method.
    }
}
